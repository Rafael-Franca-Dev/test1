-- language: SQL (PostgreSQL 15+), file: V1__schema_inicial.sql, projeto: MM Modas
-- Migracao inicial. Executada uma vez pelo Flyway na primeira subida da API.
--
-- Convencoes deste schema:
--   * dinheiro em NUMERIC(12,2) - nunca float/double, que arredonda errado em centavos
--   * ids em UUID, gerados pela aplicacao (Hibernate) com fallback no banco
--   * enums como VARCHAR + CHECK, para mapear limpo em @Enumerated(EnumType.STRING)
--   * item de pedido guarda snapshot de nome/sku/preco: historico nao muda quando
--     o produto muda de preco ou sai do catalogo

-- ===========================================================================
-- CLIENTES
-- ===========================================================================

CREATE TABLE clientes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                TEXT        NOT NULL,
    email               VARCHAR(160) NOT NULL,
    senha_hash          TEXT        NOT NULL,
    cpf                 VARCHAR(11),
    telefone            TEXT,
    data_nascimento     DATE,
    aceita_marketing    BOOLEAN     NOT NULL DEFAULT FALSE,
    ativo               BOOLEAN     NOT NULL DEFAULT TRUE,
    email_verificado_em TIMESTAMPTZ,
    perfil              VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE'
                                     CHECK (perfil IN ('CLIENTE','ADMIN')),
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_clientes_cpf        UNIQUE (cpf),
    CONSTRAINT ck_clientes_cpf_digitos CHECK (cpf IS NULL OR cpf ~ '^[0-9]{11}$')
);
-- Email unico ignorando maiuscula/minuscula. Indice funcional em vez do tipo
-- CITEXT: o tipo customizado faz o Hibernate falhar no ddl-auto=validate,
-- porque o driver reporta citext como "OTHER" e nao como VARCHAR.
CREATE UNIQUE INDEX uq_clientes_email_lower ON clientes (lower(email));

CREATE TABLE enderecos (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id   UUID        NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    apelido      TEXT,
    destinatario TEXT        NOT NULL,
    cep          VARCHAR(8)  NOT NULL,
    logradouro   TEXT        NOT NULL,
    numero       TEXT        NOT NULL,
    complemento  TEXT,
    bairro       TEXT        NOT NULL,
    cidade       TEXT        NOT NULL,
    uf           VARCHAR(2)  NOT NULL,
    principal    BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_enderecos_cep CHECK (cep ~ '^[0-9]{8}$'),
    CONSTRAINT ck_enderecos_uf  CHECK (uf ~ '^[A-Z]{2}$')
);

-- Um unico endereco principal por cliente. Indice parcial: so as linhas
-- principal=true entram na regra, as demais ficam livres.
CREATE UNIQUE INDEX uq_enderecos_principal_por_cliente
    ON enderecos (cliente_id) WHERE principal;

-- ===========================================================================
-- CATALOGO
-- ===========================================================================

CREATE TABLE categorias (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome      TEXT    NOT NULL,
    slug      TEXT    NOT NULL,
    parent_id UUID REFERENCES categorias(id) ON DELETE SET NULL,
    ordem     INTEGER NOT NULL DEFAULT 0,
    ativa     BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_categorias_slug UNIQUE (slug),
    CONSTRAINT ck_categorias_sem_auto_pai CHECK (parent_id IS NULL OR parent_id <> id)
);

CREATE TABLE produtos (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome              TEXT          NOT NULL,
    slug              TEXT          NOT NULL,
    descricao         TEXT,
    descricao_curta   TEXT,
    categoria_id      UUID REFERENCES categorias(id) ON DELETE SET NULL,
    marca             TEXT,
    preco_base        NUMERIC(12,2) NOT NULL,
    preco_promocional NUMERIC(12,2),
    ativo             BOOLEAN       NOT NULL DEFAULT TRUE,
    destaque          BOOLEAN       NOT NULL DEFAULT FALSE,
    criado_em         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    atualizado_em     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uq_produtos_slug        UNIQUE (slug),
    CONSTRAINT ck_produtos_preco_base  CHECK (preco_base >= 0),
    -- promocional nunca maior que o preco cheio: barra cadastro invertido
    CONSTRAINT ck_produtos_promocional CHECK (
        preco_promocional IS NULL
        OR (preco_promocional >= 0 AND preco_promocional <= preco_base)
    )
);

CREATE TABLE produto_imagens (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id UUID    NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    url        TEXT    NOT NULL,
    alt        TEXT,
    ordem      INTEGER NOT NULL DEFAULT 0,
    principal  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX uq_produto_imagem_principal
    ON produto_imagens (produto_id) WHERE principal;

-- Variacao e o SKU real que o cliente compra (P branca, M preta...).
-- Preco e estoque vivem aqui, nao no produto.
CREATE TABLE variacoes (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id    UUID          NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    sku           TEXT          NOT NULL,
    tamanho       TEXT,
    cor           TEXT,
    preco         NUMERIC(12,2),
    ativo         BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uq_variacoes_sku   UNIQUE (sku),
    CONSTRAINT ck_variacoes_preco CHECK (preco IS NULL OR preco >= 0),
    -- Nao pode existir "M / Preto" duas vezes no mesmo produto.
    CONSTRAINT uq_variacoes_combinacao
        UNIQUE NULLS NOT DISTINCT (produto_id, tamanho, cor)
);

-- ===========================================================================
-- ESTOQUE
-- ===========================================================================

-- Tabela separada da variacao: isola a linha quente (toda venda escreve aqui)
-- e mantem o CHECK de nao-negativo como ultima barreira contra oversell.
CREATE TABLE estoque (
    variacao_id    UUID PRIMARY KEY REFERENCES variacoes(id) ON DELETE CASCADE,
    quantidade     INTEGER     NOT NULL DEFAULT 0,
    reservado      INTEGER     NOT NULL DEFAULT 0,
    estoque_minimo INTEGER     NOT NULL DEFAULT 0,
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_estoque_quantidade CHECK (quantidade >= 0),
    CONSTRAINT ck_estoque_reservado  CHECK (reservado  >= 0),
    CONSTRAINT ck_estoque_reserva    CHECK (reservado <= quantidade)
);

-- Trilha de auditoria. Sem ela, divergencia entre estoque do sistema e
-- prateleira e impossivel de explicar.
CREATE TABLE movimentacoes_estoque (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    variacao_id UUID        NOT NULL REFERENCES variacoes(id) ON DELETE CASCADE,
    tipo        VARCHAR(20) NOT NULL,
    quantidade  INTEGER     NOT NULL,
    pedido_id   UUID,
    motivo      TEXT,
    criado_em   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_movimentacoes_tipo CHECK (tipo IN (
        'ENTRADA', 'SAIDA', 'AJUSTE', 'RESERVA', 'LIBERACAO_RESERVA', 'ESTORNO'
    )),
    CONSTRAINT ck_movimentacoes_qtd CHECK (quantidade <> 0)
);

-- ===========================================================================
-- CARRINHO
-- ===========================================================================

CREATE TABLE carrinhos (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id    UUID REFERENCES clientes(id) ON DELETE CASCADE,
    session_token TEXT,
    status        VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    expira_em     TIMESTAMPTZ,
    criado_em     TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_carrinhos_status CHECK (status IN ('ATIVO','CONVERTIDO','ABANDONADO','EXPIRADO')),
    -- carrinho sem dono nao existe: ou cliente logado, ou token de sessao
    CONSTRAINT ck_carrinhos_dono CHECK (cliente_id IS NOT NULL OR session_token IS NOT NULL)
);

CREATE UNIQUE INDEX uq_carrinhos_ativo_por_cliente
    ON carrinhos (cliente_id) WHERE status = 'ATIVO' AND cliente_id IS NOT NULL;

CREATE TABLE carrinho_itens (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    carrinho_id    UUID          NOT NULL REFERENCES carrinhos(id) ON DELETE CASCADE,
    variacao_id    UUID          NOT NULL REFERENCES variacoes(id) ON DELETE CASCADE,
    quantidade     INTEGER       NOT NULL,
    preco_unitario NUMERIC(12,2) NOT NULL,
    criado_em      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_carrinho_itens_qtd CHECK (quantidade > 0),
    CONSTRAINT uq_carrinho_itens_variacao UNIQUE (carrinho_id, variacao_id)
);

-- ===========================================================================
-- CUPONS
-- ===========================================================================

CREATE TABLE cupons (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo          VARCHAR(40)   NOT NULL,
    tipo            VARCHAR(20)   NOT NULL,
    valor           NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_minimo    NUMERIC(12,2) NOT NULL DEFAULT 0,
    usos_maximos    INTEGER,
    usos_realizados INTEGER       NOT NULL DEFAULT 0,
    valido_de       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    valido_ate      TIMESTAMPTZ,
    ativo           BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_cupons_codigo       UNIQUE (codigo),
    CONSTRAINT ck_cupons_tipo         CHECK (tipo IN ('PERCENTUAL','VALOR_FIXO','FRETE_GRATIS')),
    CONSTRAINT ck_cupons_valor        CHECK (valor >= 0 AND valor_minimo >= 0),
    CONSTRAINT ck_cupons_janela       CHECK (valido_ate IS NULL OR valido_ate > valido_de),
    CONSTRAINT ck_cupons_usos         CHECK (usos_maximos IS NULL OR usos_realizados <= usos_maximos)
);

-- ===========================================================================
-- PEDIDOS
-- ===========================================================================

CREATE SEQUENCE pedido_numero_seq START 1000;

CREATE TABLE pedidos (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero  TEXT NOT NULL DEFAULT 'MM-' || to_char(nextval('pedido_numero_seq'), 'FM000000'),
    cliente_id UUID NOT NULL REFERENCES clientes(id) ON DELETE RESTRICT,
    status  VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',

    subtotal    NUMERIC(12,2) NOT NULL DEFAULT 0,
    desconto    NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_frete NUMERIC(12,2) NOT NULL DEFAULT 0,
    total       NUMERIC(12,2) NOT NULL DEFAULT 0,

    cupom_id UUID REFERENCES cupons(id) ON DELETE SET NULL,

    -- Snapshot do endereco. O cliente pode editar ou apagar o endereco depois;
    -- o pedido precisa continuar mostrando para onde foi.
    entrega_destinatario TEXT  NOT NULL,
    entrega_cep          VARCHAR(8) NOT NULL,
    entrega_logradouro   TEXT  NOT NULL,
    entrega_numero       TEXT  NOT NULL,
    entrega_complemento  TEXT,
    entrega_bairro       TEXT  NOT NULL,
    entrega_cidade       TEXT  NOT NULL,
    entrega_uf           VARCHAR(2) NOT NULL,

    -- Chave de idempotencia vinda do front. Bloqueia pedido duplicado quando o
    -- usuario clica duas vezes ou a rede reenvia a requisicao.
    idempotency_key TEXT,

    observacoes  TEXT,
    pago_em      TIMESTAMPTZ,
    enviado_em   TIMESTAMPTZ,
    entregue_em  TIMESTAMPTZ,
    cancelado_em TIMESTAMPTZ,
    criado_em    TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_pedidos_numero      UNIQUE (numero),
    CONSTRAINT uq_pedidos_idempotency UNIQUE (idempotency_key),
    CONSTRAINT ck_pedidos_status CHECK (status IN (
        'PENDENTE','PAGO','EM_SEPARACAO','ENVIADO','ENTREGUE','CANCELADO','REEMBOLSADO'
    )),
    CONSTRAINT ck_pedidos_valores CHECK (
        subtotal >= 0 AND desconto >= 0 AND valor_frete >= 0 AND total >= 0
    )
);

CREATE TABLE pedido_itens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id   UUID NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    -- RESTRICT: nao deixa apagar uma variacao que ja foi vendida
    variacao_id UUID NOT NULL REFERENCES variacoes(id) ON DELETE RESTRICT,

    produto_nome TEXT NOT NULL,
    sku          TEXT NOT NULL,
    tamanho      TEXT,
    cor          TEXT,

    quantidade     INTEGER       NOT NULL,
    preco_unitario NUMERIC(12,2) NOT NULL,
    -- Coluna gerada: o banco calcula, ninguem digita errado.
    total_item     NUMERIC(12,2) GENERATED ALWAYS AS (quantidade * preco_unitario) STORED,

    CONSTRAINT ck_pedido_itens_qtd   CHECK (quantidade > 0),
    CONSTRAINT ck_pedido_itens_preco CHECK (preco_unitario >= 0)
);

CREATE TABLE pagamentos (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id     UUID          NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    metodo        VARCHAR(30)   NOT NULL,
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
    valor         NUMERIC(12,2) NOT NULL,
    parcelas      INTEGER       NOT NULL DEFAULT 1,
    provider      VARCHAR(30),
    provider_txid TEXT,
    payload       JSONB,
    criado_em     TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_pagamentos_metodo CHECK (metodo IN ('PIX','CARTAO_CREDITO','BOLETO')),
    CONSTRAINT ck_pagamentos_status CHECK (status IN (
        'PENDENTE','EM_ANALISE','APROVADO','RECUSADO','ESTORNADO'
    )),
    CONSTRAINT ck_pagamentos_valor    CHECK (valor > 0),
    CONSTRAINT ck_pagamentos_parcelas CHECK (parcelas BETWEEN 1 AND 24)
);

-- FK que so pode existir depois de pedidos existir.
ALTER TABLE movimentacoes_estoque
    ADD CONSTRAINT fk_movimentacoes_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL;

-- ===========================================================================
-- INDICES
-- ===========================================================================

CREATE INDEX idx_enderecos_cliente       ON enderecos (cliente_id);
CREATE INDEX idx_produtos_categoria      ON produtos (categoria_id) WHERE ativo;
CREATE INDEX idx_produtos_destaque       ON produtos (destaque) WHERE ativo AND destaque;
CREATE INDEX idx_variacoes_produto       ON variacoes (produto_id);
CREATE INDEX idx_movimentacoes_variacao  ON movimentacoes_estoque (variacao_id, criado_em DESC);
CREATE INDEX idx_movimentacoes_pedido    ON movimentacoes_estoque (pedido_id);
CREATE INDEX idx_carrinho_itens_carrinho ON carrinho_itens (carrinho_id);
CREATE INDEX idx_pedidos_cliente         ON pedidos (cliente_id, criado_em DESC);
CREATE INDEX idx_pedidos_status          ON pedidos (status, criado_em DESC);
CREATE INDEX idx_pedido_itens_pedido     ON pedido_itens (pedido_id);
CREATE INDEX idx_pagamentos_pedido       ON pagamentos (pedido_id);
CREATE INDEX idx_pagamentos_txid         ON pagamentos (provider, provider_txid);

-- ===========================================================================
-- VIEWS de leitura
-- ===========================================================================

-- Vitrine: uma linha por variacao, preco resolvido e disponibilidade pronta.
CREATE VIEW vw_variacoes_disponiveis AS
SELECT
    v.id            AS variacao_id,
    v.produto_id,
    p.nome          AS produto_nome,
    p.slug          AS produto_slug,
    v.sku,
    v.tamanho,
    v.cor,
    COALESCE(v.preco, p.preco_promocional, p.preco_base) AS preco,
    p.preco_base,
    p.preco_promocional,
    e.quantidade,
    e.reservado,
    (e.quantidade - e.reservado) AS disponivel,
    (e.quantidade - e.reservado) > 0 AS em_estoque
FROM variacoes v
JOIN produtos p ON p.id = v.produto_id
JOIN estoque  e ON e.variacao_id = v.id
WHERE v.ativo AND p.ativo;

-- Alerta de reposicao.
CREATE VIEW vw_estoque_critico AS
SELECT
    v.id AS variacao_id, p.nome AS produto_nome, v.sku, v.tamanho, v.cor,
    e.quantidade, e.estoque_minimo, (e.quantidade - e.reservado) AS disponivel
FROM estoque e
JOIN variacoes v ON v.id = e.variacao_id
JOIN produtos  p ON p.id = v.produto_id
WHERE e.quantidade <= e.estoque_minimo
ORDER BY (e.quantidade - e.reservado) ASC;
