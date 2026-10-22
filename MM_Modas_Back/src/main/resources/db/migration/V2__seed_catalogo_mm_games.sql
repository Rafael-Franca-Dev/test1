-- Seed inicial do catalogo MM GAMES (jogos de tabuleiro, RPG, cartas e acessorios)
-- e cupom promocional MENINOMAU10.

INSERT INTO cupons (codigo, tipo, valor, valor_minimo, usos_maximos, ativo)
VALUES ('MENINOMAU10', 'PERCENTUAL', 10.00, 0.00, NULL, TRUE)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO categorias (id, nome, slug, ordem, ativa) VALUES
    ('11111111-1111-1111-1111-111111111101', 'RPG de Mesa', 'rpg', 1, TRUE),
    ('11111111-1111-1111-1111-111111111102', 'Jogos em Família', 'familia', 2, TRUE),
    ('11111111-1111-1111-1111-111111111103', 'Estratégia & Guerra', 'estrategia', 3, TRUE),
    ('11111111-1111-1111-1111-111111111104', 'Card Games', 'cartas', 4, TRUE),
    ('11111111-1111-1111-1111-111111111105', 'Party Games', 'party', 5, TRUE),
    ('11111111-1111-1111-1111-111111111106', 'Acessórios & Dados', 'acessorios', 6, TRUE)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO produtos (id, nome, slug, descricao, descricao_curta, categoria_id, marca, preco_base, preco_promocional, ativo, destaque) VALUES
    ('22222222-2222-2222-2222-222222222201', 'Jogo da Velha Deluxe', 'jogo-da-velha', 'O clássico duelo rápido agora em peça de madeira maciça entalhada para partidas rápidas de mesa.', 'Tabuleiro clássico em madeira', '11111111-1111-1111-1111-111111111102', 'MM Games', 119.90, 89.90, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222203', 'Monopoly Edição Magnata', 'monopoli', 'Negocie propriedades, construa hotéis e leve seus amigos à falência com estilo na edição completa de tabuleiro.', 'Clássico de negociação imobiliária', '11111111-1111-1111-1111-111111111102', 'MM Games', 149.90, NULL, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222206', 'Xadrez Oficial Magnético', 'xadrez-oficial', 'Não, você não é o Magnus Carlsen, porém ainda pode derrotar seu amigo no mesmo tabuleiro com peças balanceadas.', 'Tabuleiro dobrável com peças pesadas', '11111111-1111-1111-1111-111111111103', 'MM Games', 189.90, NULL, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222208', 'Dungeons & Dragons: Livro do Jogador', 'dnd-livro-do-jogador', 'O guia definitivo para criar heróis, conjurar magias e explorar masmorras épicas com seu grupo de RPG.', 'Sistema clássico de fantasia medieval', '11111111-1111-1111-1111-111111111101', 'Wizards / MM', 219.90, NULL, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222214', 'Chamado de Cthulhu — Livro do Guardião', 'call-of-cthulhu', 'Investigação e horror cósmico nos anos 1920. Descubra segredos proibidos antes que sua sanidade acabe.', 'RPG de investigação e horror cósmico', '11111111-1111-1111-1111-111111111101', 'Chaosium / MM', 159.90, NULL, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222217', 'Ordem Paranormal RPG', 'ordem-paranormal', 'A membrana está frágil. Reúna seus agentes da Ordo Realitas e enfrente entidades do Outro Lado.', 'RPG brasileiro de investigação paranormal', '11111111-1111-1111-1111-111111111101', 'Jambô / MM', 119.70, 99.90, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222220', 'Tormenta20 — Edição Jogo do Ano', 'tormenta20', 'O maior RPG de fantasia do Brasil em sua versão revisada e expandida para aventuras em Arton.', 'O cenário de fantasia mais famoso do Brasil', '11111111-1111-1111-1111-111111111101', 'Jambô / MM', 149.90, 119.90, TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222221', 'War — Estratégia Global', 'war-estrategia', 'Conquiste continentes, mobilize exércitos e teste a resistência das suas amizades em batalhas táticas.', 'Jogo clássico de conquista de territórios', '11111111-1111-1111-1111-111111111103', 'Grow / MM', 129.90, 99.90, TRUE, TRUE)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO produto_imagens (id, produto_id, url, alt, ordem, principal) VALUES
    ('33333333-3333-3333-3333-333333333301', '22222222-2222-2222-2222-222222222201', '/produtos/Jogodavelha.png', 'Jogo da Velha Deluxe', 0, TRUE),
    ('33333333-3333-3333-3333-333333333303', '22222222-2222-2222-2222-222222222203', '/produtos/monopoli.png', 'Monopoly Edição Magnata', 0, TRUE),
    ('33333333-3333-3333-3333-333333333306', '22222222-2222-2222-2222-222222222206', '/produtos/Xadrez.png', 'Xadrez Oficial Magnético', 0, TRUE),
    ('33333333-3333-3333-3333-333333333308', '22222222-2222-2222-2222-222222222208', '/produtos/DND.png', 'Dungeons & Dragons', 0, TRUE),
    ('33333333-3333-3333-3333-333333333314', '22222222-2222-2222-2222-222222222214', '/produtos/callofcthulhu.png', 'Chamado de Cthulhu', 0, TRUE),
    ('33333333-3333-3333-3333-333333333317', '22222222-2222-2222-2222-222222222217', '/produtos/ordemparanormal.png', 'Ordem Paranormal RPG', 0, TRUE),
    ('33333333-3333-3333-3333-333333333320', '22222222-2222-2222-2222-222222222220', '/produtos/tormenta20.png', 'Tormenta20', 0, TRUE),
    ('33333333-3333-3333-3333-333333333321', '22222222-2222-2222-2222-222222222221', '/produtos/War.png', 'War — Estratégia Global', 0, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO variacoes (id, produto_id, sku, tamanho, cor, preco, ativo) VALUES
    ('44444444-4444-4444-4444-444444444401', '22222222-2222-2222-2222-222222222201', 'MM-JDV-PAD', 'Padrão', 'Lavanda', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444403', '22222222-2222-2222-2222-222222222203', 'MM-MON-PAD', 'Padrão', 'Pêssego', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444406', '22222222-2222-2222-2222-222222222206', 'MM-XAD-PAD', 'Padrão', 'Azul céu', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444408', '22222222-2222-2222-2222-222222222208', 'MM-DND-FIS', 'Físico + PDF', 'Roxo claro', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444414', '22222222-2222-2222-2222-222222222214', 'MM-COC-FIS', 'Físico + PDF', 'Grafite lilás', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444417', '22222222-2222-2222-2222-222222222217', 'MM-ORD-FIS', 'Físico + PDF', 'Lavanda', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444420', '22222222-2222-2222-2222-222222222220', 'MM-T20-FIS', 'Físico + PDF', 'Pêssego', NULL, TRUE),
    ('44444444-4444-4444-4444-444444444421', '22222222-2222-2222-2222-222222222221', 'MM-WAR-PAD', 'Padrão', 'Roxo claro', NULL, TRUE)
ON CONFLICT (sku) DO NOTHING;

INSERT INTO estoque (variacao_id, quantidade, reservado, estoque_minimo) VALUES
    ('44444444-4444-4444-4444-444444444401', 25, 0, 3),
    ('44444444-4444-4444-4444-444444444403', 20, 0, 3),
    ('44444444-4444-4444-4444-444444444406', 15, 0, 2),
    ('44444444-4444-4444-4444-444444444408', 30, 0, 5),
    ('44444444-4444-4444-4444-444444444414', 18, 0, 3),
    ('44444444-4444-4444-4444-444444444417', 40, 0, 5),
    ('44444444-4444-4444-4444-444444444420', 35, 0, 5),
    ('44444444-4444-4444-4444-444444444421', 22, 0, 3)
ON CONFLICT (variacao_id) DO NOTHING;

