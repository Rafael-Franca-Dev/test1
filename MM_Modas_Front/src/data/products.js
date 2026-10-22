// =========================================================
// Catálogo oficial da MM GAMES (Jogos de Tabuleiro, RPG & Card Games)
// ---------------------------------------------------------
// - categories: categorias exibidas na Home, na Loja e no rodapé.
// - products: cada jogo/acessório possui "type" (ilustração SVG em ProductArt
//   quando não há foto), "image" (foto real quando disponível), "colors" (tema
//   da caixa/componentes) e "sizes" (edições disponíveis).
// =========================================================

const cor = {
  lavanda: { name: 'Lavanda Arcana', hex: '#c8b3f0' },
  roxoClaro: { name: 'Roxo Místico', hex: '#b7a0ea' },
  lilas: { name: 'Lilás Nebulosa', hex: '#d3c1f5' },
  pessego: { name: 'Pêssego Solar', hex: '#ffc596' },
  laranja: { name: 'Laranja Crítico', hex: '#ffb57d' },
  offWhite: { name: 'Pergaminho', hex: '#fbf6ee' },
  areia: { name: 'Areia de Duna', hex: '#e9d6bf' },
  grafite: { name: 'Grafite Sombrio', hex: '#6e6190' },
  azulCeu: { name: 'Azul Astral', hex: '#bcd3f2' },
  verdeMenta: { name: 'Verde Alquimia', hex: '#bfe3cf' },
}

// Edições e formatos reais do universo de jogos de mesa
const EDICAO_TABULEIRO = ['Padrão', 'Deluxe', 'Colecionador']
const EDICAO_RPG = ['Físico + PDF', 'Livro Físico', 'Box Iniciante']
const EDICAO_CARTAS = ['Deck Base', 'Booster Box', 'Kit Completo']
const EDICAO_ACESSORIO = ['Padrão', 'Edição Especial']

export const categories = [
  { id: 'rpg', name: 'RPG de Mesa', art: 'd20', bg: 'var(--laranja-200)' },
  { id: 'familia', name: 'Família', art: 'board', bg: 'var(--roxo-200)' },
  { id: 'estrategia', name: 'Estratégia', art: 'chess', bg: 'var(--laranja-100)' },
  { id: 'cartas', name: 'Card Games', art: 'cards', bg: 'var(--roxo-100)' },
  { id: 'party', name: 'Party Games', art: 'meeple', bg: 'var(--laranja-200)' },
  { id: 'acessorios', name: 'Acessórios & Dados', art: 'tower', bg: 'var(--roxo-200)' },
]

export const products = [
  // ---------- Família & Clássicos ----------
  {
    id: 1,
    variacaoId: '44444444-4444-4444-4444-444444444401',
    slug: 'jogo-da-velha',
    name: 'Jogo da Velha Deluxe',
    category: 'familia',
    type: 'board',
    image: '/produtos/Jogodavelha.png',
    price: 89.9,
    oldPrice: 119.9,
    colors: [cor.lavanda, cor.pessego, cor.offWhite],
    sizes: EDICAO_TABULEIRO,
    tags: ['novo'],
    description:
      'O clássico duelo rápido agora em peças maciças e tabuleiro estilizado. Partidas de 5 minutos perfeitas para abrir a noite de jogos ou decorar a estante.',
  },
  {
    id: 2,
    slug: 'carcassonne-reinos',
    name: 'Cidadela dos Meeples',
    category: 'familia',
    type: 'meeple',
    price: 139.9,
    colors: [cor.offWhite, cor.roxoClaro],
    sizes: EDICAO_TABULEIRO,
    tags: [],
    description:
      'Construa estradas, fortalezas e monastérios peça por peça. Regras simples em 5 minutos e disputas acirradas pelo controle de cada território.',
  },
  {
    id: 3,
    variacaoId: '44444444-4444-4444-4444-444444444403',
    slug: 'monopoli',
    name: 'Monopoly Edição Magnata',
    category: 'familia',
    type: 'board',
    image: '/produtos/monopoli.png',
    price: 149.9,
    colors: [cor.pessego, cor.lavanda, cor.offWhite],
    sizes: EDICAO_TABULEIRO,
    tags: ['novo'],
    description:
      'Negocie avenidas, construa hotéis e cobre aluguéis impiedosos. O clássico absoluto para testar quem realmente manda na economia da mesa.',
  },
  {
    id: 4,
    slug: 'expresso-transcontinental',
    name: 'Expresso das Rotas',
    category: 'familia',
    type: 'board',
    price: 179.9,
    colors: [cor.lilas, cor.areia],
    sizes: EDICAO_TABULEIRO,
    tags: [],
    description:
      'Conecte cidades pelo mapa colecionando cartas de vagão antes que seus rivais bloqueiem sua ferrovia. Ideal para 2 a 5 jogadores.',
  },

  // ---------- Estratégia & Guerra ----------
  {
    id: 5,
    slug: 'colonos-da-ilha',
    name: 'Ilha dos Colonos',
    category: 'estrategia',
    type: 'board',
    price: 199.9,
    colors: [cor.lavanda, cor.areia],
    sizes: EDICAO_TABULEIRO,
    tags: [],
    description:
      'Troque madeira, tijolo e trigo, expanda suas vilas em um tabuleiro modular hexagonal e negocie cada recurso com diplomacia marota.',
  },
  {
    id: 6,
    variacaoId: '44444444-4444-4444-4444-444444444406',
    slug: 'xadrez-oficial',
    name: 'Xadrez Oficial Magnético',
    category: 'estrategia',
    type: 'chess',
    image: '/produtos/Xadrez.png',
    price: 189.9,
    colors: [cor.azulCeu, cor.offWhite, cor.lilas],
    sizes: EDICAO_TABULEIRO,
    tags: ['novo'],
    description:
      'Não, você não é o Magnus Carlsen, porém ainda pode derrotar seu amigo no mesmo tabuleiro com peças balanceadas e base magnética.',
  },
  {
    id: 7,
    slug: 'imperio-das-estrelas',
    name: 'Império de Nebulosa 4X',
    category: 'estrategia',
    type: 'shield',
    price: 269.9,
    colors: [cor.laranja, cor.roxoClaro],
    sizes: EDICAO_TABULEIRO,
    tags: [],
    description:
      'Explore setores galácticos, pesquise tecnologias proibidas e comande frotas em um eurogame pesado para estrategistas exigentes.',
  },
  {
    id: 21,
    variacaoId: '44444444-4444-4444-4444-444444444421',
    slug: 'war-estrategia',
    name: 'War — Estratégia Global',
    category: 'estrategia',
    type: 'shield',
    image: '/produtos/War.png',
    price: 99.9,
    oldPrice: 129.9,
    colors: [cor.roxoClaro, cor.verdeMenta],
    sizes: EDICAO_TABULEIRO,
    tags: ['novo'],
    description:
      'Objetivos secretos, alianças temporárias e dados rolando pela conquista de continentes inteiros. Prepare-se para horas de tensão tática.',
  },

  // ---------- RPG de Mesa ----------
  {
    id: 8,
    variacaoId: '44444444-4444-4444-4444-444444444408',
    slug: 'dnd-livro-do-jogador',
    name: 'Dungeons & Dragons (D&D)',
    category: 'rpg',
    type: 'rpgbook',
    image: '/produtos/DND.png',
    price: 219.9,
    colors: [cor.roxoClaro, cor.laranja],
    sizes: EDICAO_RPG,
    tags: ['novo'],
    description:
      'O RPG mais lendário do mundo. Crie seu personagem, escolha sua classe, prepare seus dados de 20 lados e desça às masmorras com seu grupo.',
  },
  {
    id: 14,
    variacaoId: '44444444-4444-4444-4444-444444444414',
    slug: 'call-of-cthulhu',
    name: 'Call of Cthulhu — Guardião',
    category: 'rpg',
    type: 'rpgbook',
    image: '/produtos/callofcthulhu.png',
    price: 159.9,
    colors: [cor.grafite, cor.lavanda],
    sizes: EDICAO_RPG,
    tags: ['novo'],
    description:
      'Horror cósmico e investigação nos anos 1920. Desvende cultos arcanos e mistérios insondáveis antes que seus pontos de sanidade cheguem a zero.',
  },
  {
    id: 17,
    variacaoId: '44444444-4444-4444-4444-444444444417',
    slug: 'ordem-paranormal',
    name: 'Ordem Paranormal RPG',
    category: 'rpg',
    type: 'rpgbook',
    image: '/produtos/ordemparanormal.png',
    price: 99.9,
    oldPrice: 119.7,
    colors: [cor.lavanda, cor.pessego, cor.grafite],
    sizes: EDICAO_RPG,
    tags: ['novo'],
    description:
      'A membrana está frágil. Crie agentes da Ordo Realitas, investigue ocorrências ocultistas e enfrente criaturas do Outro Lado.',
  },
  {
    id: 20,
    variacaoId: '44444444-4444-4444-4444-444444444420',
    slug: 'tormenta20',
    name: 'Tormenta20 — Jogo do Ano',
    category: 'rpg',
    type: 'rpgbook',
    image: '/produtos/tormenta20.png',
    price: 119.9,
    oldPrice: 149.9,
    colors: [cor.pessego, cor.lavanda, cor.offWhite],
    sizes: EDICAO_RPG,
    tags: ['novo'],
    description:
      'O maior universo de fantasia medieval do Brasil! Edição revisada e ampliada para campanhas heroicas no mundo de Arton.',
  },

  // ---------- Card Games ----------
  {
    id: 9,
    slug: 'duelo-de-grimorios',
    name: 'Duelo de Grimórios TCG',
    category: 'cartas',
    type: 'cards',
    price: 129.9,
    colors: [cor.pessego, cor.offWhite],
    sizes: EDICAO_CARTAS,
    tags: [],
    description:
      'Monte seu baralho de feitiços, combine artefatos sinérgicos e responda no turno do oponente. Inclui 2 decks prontos para duelo imediato.',
  },
  {
    id: 10,
    slug: 'masmorra-em-cartas',
    name: 'Masmorra de Bolso',
    category: 'cartas',
    type: 'cards',
    price: 79.9,
    colors: [cor.areia, cor.lavanda],
    sizes: EDICAO_CARTAS,
    tags: [],
    description:
      'Um dungeon crawler cooperativo inteiro dentro de um baralho de 110 cartas ilustradas. Derrote o chefão final sem precisar de mestre.',
  },
  {
    id: 11,
    slug: 'blefe-dos-piratas',
    name: 'Truco do Corsário',
    category: 'cartas',
    type: 'cards',
    price: 69.9,
    oldPrice: 89.9,
    colors: [cor.lilas, cor.laranja],
    sizes: EDICAO_CARTAS,
    tags: [],
    description:
      'Jogo de vazas e apostas onde acertar exatamente quantas rodadas você vai vencer vale ouro — e sabotar o amiguinho vale mais ainda.',
  },

  // ---------- Party Games ----------
  {
    id: 12,
    slug: 'infiltrado-secreto',
    name: 'Agente Infiltrado',
    category: 'party',
    type: 'meeple',
    price: 89.9,
    colors: [cor.grafite, cor.lilas],
    sizes: EDICAO_TABULEIRO,
    tags: [],
    description:
      'Dedução social para até 10 pessoas. Descubra quem na mesa está sabotando a missão antes que a desconfiança destrua a equipe.',
  },
  {
    id: 13,
    slug: 'mimica-caotica',
    name: 'Caos na Mesa Party',
    category: 'party',
    type: 'box',
    price: 94.9,
    colors: [cor.areia, cor.grafite],
    sizes: EDICAO_TABULEIRO,
    tags: [],
    description:
      '400 cartas de desafios absurdos, associação de palavras sob pressão e reviravoltas em 60 segundos. Garantia de gargalhadas.',
  },

  // ---------- Acessórios & Dados ----------
  {
    id: 15,
    slug: 'kit-dados-poliedricos-nebulosa',
    name: 'Set 7 Dados RPG Nebulosa',
    category: 'acessorios',
    type: 'd20',
    price: 59.9,
    colors: [cor.roxoClaro, cor.laranja, cor.azulCeu],
    sizes: EDICAO_ACESSORIO,
    tags: ['novo'],
    description:
      'Conjunto completo (D4, D6, D8, 2x D10, D12 e D20) em resina translúcida com glitter furta-cor e numeração dourada de alta leitura.',
  },
  {
    id: 16,
    slug: 'torre-de-dados-castelo',
    name: 'Torre de Dados Fortaleza MM',
    category: 'acessorios',
    type: 'tower',
    price: 119.9,
    colors: [cor.grafite, cor.lavanda],
    sizes: EDICAO_ACESSORIO,
    tags: [],
    description:
      'Acabe com os dados caindo no chão! Três níveis internos de defletores acústicos e bandeja forrada em feltro roxo.',
  },
  {
    id: 18,
    slug: 'escudo-do-mestre-modular',
    name: 'Escudo do Mestre Magnético',
    category: 'acessorios',
    type: 'shield',
    price: 139.9,
    colors: [cor.lavanda, cor.grafite],
    sizes: EDICAO_ACESSORIO,
    tags: [],
    description:
      'Quatro painéis rígidos com bolsos transparentes para encaixar as tabelas do seu sistema preferido e marcadores de iniciativa no topo.',
  },
  {
    id: 19,
    slug: 'playmat-grid-tatico',
    name: 'Playmat Grid Hex & Quadrado',
    category: 'acessorios',
    type: 'playmat',
    price: 129.9,
    colors: [cor.areia, cor.verdeMenta],
    sizes: EDICAO_ACESSORIO,
    tags: [],
    description:
      'Tapete de neoprene antiderrapante dupla-face (quadriculado de 1 polegada de um lado, hexagonal do outro), compatível com caneta apagável.',
  },
  {
    id: 22,
    slug: 'kit-sleeves-premium',
    name: 'Kit 500 Sleeves Escudo Arcano',
    category: 'acessorios',
    type: 'cards',
    price: 64.9,
    colors: [cor.offWhite, cor.roxoClaro],
    sizes: EDICAO_ACESSORIO,
    tags: [],
    description:
      'Películas protetoras de 90 micras sem ácido para proteger suas cartas de embaralhamentos intensos (padrão 63,5 x 88 mm).',
  },
  {
    id: 23,
    slug: 'bolsa-dados-couro-dragao',
    name: 'Algibeira de Dados Menino Mau',
    category: 'acessorios',
    type: 'dicebag',
    price: 74.9,
    colors: [cor.laranja, cor.grafite],
    sizes: EDICAO_ACESSORIO,
    tags: [],
    description:
      'Bolsa com 7 divisórias internas de veludo e cordão duplo. Comporta até 140 dados poliédricos separados por tipo.',
  },
  {
    id: 30,
    slug: 'mochila-boardgame-vault',
    name: 'Mochila Boardgame Vault',
    category: 'acessorios',
    type: 'backpack',
    price: 249.9,
    oldPrice: 289.9,
    colors: [cor.lavanda, cor.grafite],
    sizes: EDICAO_ACESSORIO,
    tags: ['novo'],
    description:
      'Estrutura acolchoada quadrada projetada para transportar até 5 caixas grandes de board games na horizontal + bolso para livros de RPG.',
  },
]

// Busca um produto pelo "slug" usado na URL (/produto/:slug)
export function getProductBySlug(slug) {
  return products.find((p) => p.slug === slug)
}

// Converte o id da categoria no nome exibido (ex.: 'rpg' -> 'RPG de Mesa')
export function getCategoryName(id) {
  return categories.find((c) => c.id === id)?.name ?? id
}
