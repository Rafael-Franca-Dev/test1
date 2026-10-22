// =========================================================
// ProductArt — ilustração SVG temática de cada item da MM GAMES
// ---------------------------------------------------------
// Quando o produto possui "image" (foto real), exibe a foto.
// Caso contrário, desenha uma arte vetorial de jogo/RPG em SVG.
// =========================================================
import './ProductArt.css'

const INK = '#2c1f4a'

const shapes = {
  // Dado D20 icônico de RPG
  d20: (c) => (
    <>
      <polygon points="100,22 164,60 164,140 100,178 36,140 36,60" fill={c} />
      <polygon points="100,48 142,124 58,124" fill="#fff" opacity="0.45" />
      <path d="M100 22 L100 48 M164 60 L142 124 M36 60 L58 124 M100 48 L164 60 M100 48 L36 60 M58 124 L100 178 M142 124 L100 178 M36 140 L58 124 M164 140 L142 124" fill="none" />
      <text x="100" y="112" textAnchor="middle" className="art-print">20</text>
    </>
  ),

  // Tabuleiro com trilha e peões
  board: (c) => (
    <>
      <rect x="32" y="32" width="136" height="136" rx="16" fill={c} />
      <rect x="48" y="48" width="104" height="104" rx="8" fill="#fff" />
      <path d="M48 82 H152 M48 118 H152 M82 48 V152 M118 48 V152" fill="none" />
      <circle cx="65" cy="65" r="9" fill={c} />
      <circle cx="135" cy="135" r="9" fill="#ffaa6b" />
      <path d="M92 92 L108 108 M108 92 L92 108" fill="none" strokeWidth="4" />
    </>
  ),

  // Livro de RPG / Grimório com marcador
  rpgbook: (c) => (
    <>
      <path d="M48 30 H148 A10 10 0 0 1 158 40 V162 A10 10 0 0 1 148 172 H48 A8 8 0 0 1 40 164 V38 A8 8 0 0 1 48 30 Z" fill={c} />
      <path d="M56 30 V172" fill="none" />
      <rect x="74" y="54" width="64" height="76" rx="6" fill="#fff" />
      <polygon points="106,68 124,92 106,116 88,92" fill={c} />
      <path d="M96 172 V188 L106 180 L116 188 V172" fill="#ffaa6b" />
    </>
  ),

  // Cartas de Card Game / TCG
  cards: (c) => (
    <>
      <g transform="rotate(-10 86 104)">
        <rect x="44" y="42" width="84" height="122" rx="10" fill="#fff" />
        <rect x="54" y="52" width="64" height="102" rx="6" fill={c} />
      </g>
      <g transform="rotate(8 116 104)">
        <rect x="72" y="42" width="84" height="122" rx="10" fill={c} />
        <rect x="84" y="54" width="60" height="64" rx="6" fill="#fff" />
        <text x="114" y="96" textAnchor="middle" className="art-print art-print--sm">MM</text>
        <path d="M88 134 H140 M88 146 H124" fill="none" />
      </g>
    </>
  ),

  // Peça de Xadrez / Estratégia
  chess: (c) => (
    <>
      <path d="M56 164 H144 L136 146 H64 Z" fill={c} />
      <path d="M68 146 L76 82 H124 L132 146 Z" fill={c} />
      <path d="M66 82 H134 V62 H120 V72 H108 V62 H92 V72 H80 V62 H66 Z" fill={c} />
      <circle cx="100" cy="44" r="10" fill="#fff" />
    </>
  ),

  // Meeple clássico + dado (Party Games)
  meeple: (c) => (
    <>
      <path
        d="M100 32 C112 32 120 42 114 54 L148 76 C154 80 150 92 140 90 L124 86 L136 162 H108 L100 124 L92 162 H64 L76 86 L60 90 C50 92 46 80 52 76 L86 54 C80 42 88 32 100 32 Z"
        fill={c}
      />
      <circle cx="93" cy="48" r="2.5" fill={INK} stroke="none" />
      <circle cx="107" cy="48" r="2.5" fill={INK} stroke="none" />
    </>
  ),

  // Torre de dados
  tower: (c) => (
    <>
      <path d="M60 36 V56 H74 V46 H92 V56 H108 V46 H126 V56 H140 V36" fill={c} />
      <rect x="64" y="56" width="72" height="96" fill={c} />
      <rect x="48" y="142" width="112" height="24" rx="6" fill="#fff" />
      <rect x="84" y="114" width="32" height="28" rx="4" fill={INK} />
      <path d="M76 78 H96 M106 96 H124" fill="none" />
    </>
  ),

  // Escudo do Mestre / Brasão Tático
  shield: (c) => (
    <>
      <path d="M44 36 L100 24 L156 36 V98 C156 138 100 168 100 176 C100 168 44 138 44 98 Z" fill={c} />
      <path d="M60 50 L100 40 L140 50 V96 C140 124 100 148 100 154 C100 148 60 124 60 96 Z" fill="#fff" opacity="0.4" />
      <path d="M100 40 V154 M60 94 H140" fill="none" />
      <text x="100" y="86" textAnchor="middle" className="art-print art-print--sm">MM</text>
    </>
  ),

  // Playmat / Mapa Tático
  playmat: (c) => (
    <>
      <rect x="28" y="48" width="144" height="104" rx="12" fill={c} />
      <rect x="40" y="60" width="120" height="80" rx="6" fill="#fff" />
      <g strokeWidth="1.8" opacity="0.55">
        <path d="M70 60 V140 M100 60 V140 M130 60 V140" />
        <path d="M40 86 H160 M40 114 H160" />
      </g>
      <circle cx="85" cy="100" r="8" fill={c} />
      <circle cx="115" cy="74" r="8" fill="#ffaa6b" />
    </>
  ),

  // Algibeira / Bolsa de Dados
  dicebag: (c) => (
    <>
      <path d="M66 56 L134 56 L152 132 C156 154 136 168 100 168 C64 168 44 154 48 132 Z" fill={c} />
      <path d="M70 40 L82 56 H118 L130 40 Z" fill={c} />
      <path d="M58 64 Q100 74 142 64" fill="none" strokeWidth="4" />
      <polygon points="100,96 118,108 112,128 88,128 82,108" fill="#fff" />
    </>
  ),

  // Caixa de Board Game
  box: (c) => (
    <>
      <polygon points="100,32 164,62 100,92 36,62" fill="#fff" />
      <polygon points="36,62 100,92 100,164 36,134" fill={c} />
      <polygon points="164,62 100,92 100,164 164,134" fill={c} opacity="0.85" />
      <text x="68" y="128" textAnchor="middle" className="art-print art-print--sm">MM</text>
    </>
  ),

  // Mochila Boardgame Vault
  backpack: (c) => (
    <>
      <path d="M86 42 Q86 22 100 22 Q114 22 114 42" fill="none" strokeWidth="5" />
      <path d="M50 58 Q50 40 70 40 L130 40 Q150 40 150 58 L150 166 Q150 178 136 178 L64 178 Q50 178 50 166 Z" fill={c} />
      <rect x="64" y="62" width="72" height="32" rx="6" fill="#fff" />
      <rect x="64" y="106" width="72" height="54" rx="8" fill="#fff" opacity="0.55" />
      <text x="100" y="84" textAnchor="middle" className="art-print art-print--sm">MM</text>
    </>
  ),
}

// Aliases de segurança para qualquer código antigo
shapes.tee = shapes.d20
shapes.shirt = shapes.board
shapes.hoodie = shapes.rpgbook
shapes.jacket = shapes.cards
shapes.cap = shapes.tower

function ProductArt({ type = 'd20', color = '#c8b3f0', bg, className = '', label, image }) {
  const draw = shapes[type] ?? shapes.d20

  return (
    <div className={`product-art ${className}`} style={bg ? { background: bg } : undefined}>
      {image ? (
        <img src={image} alt={label ?? ''} className="product-art__img" loading="lazy" />
      ) : (
        <svg
          viewBox="0 0 200 200"
          className="product-art__svg"
          {...(label ? { role: 'img', 'aria-label': label } : { 'aria-hidden': true })}
        >
          <g stroke={INK} strokeWidth="3.5" strokeLinejoin="round" strokeLinecap="round">
            {draw(color)}
          </g>
        </svg>
      )}
    </div>
  )
}

export default ProductArt
