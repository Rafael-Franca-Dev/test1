// =========================================================
// Mascote "Menino Mau" desenhado em SVG, inspirado na caricatura do grupo:
// cabelo cacheado, sobrancelhas arqueadas e aquele sorriso de quem aprontou.
// Props: size (px), title (texto alternativo; vazio = decorativo)
// =========================================================
function Mascot({ size = 56, className = '', title = 'Mascote Menino Mau' }) {
  // Cores do desenho
  const ink = '#2c1f4a'
  const hair = '#c8b3f0'
  const skin = '#ffefe0'
  const blush = '#ffaa6b'

  return (
    <svg
      className={className}
      width={size}
      height={size}
      viewBox="0 0 120 120"
      {...(title ? { role: 'img', 'aria-label': title } : { 'aria-hidden': true })}
    >
      {/* Cachos de trás */}
      <g fill={hair} stroke={ink} strokeWidth="4">
        <circle cx="28" cy="54" r="14" />
        <circle cx="33" cy="34" r="15" />
        <circle cx="50" cy="21" r="16" />
        <circle cx="70" cy="21" r="16" />
        <circle cx="87" cy="34" r="15" />
        <circle cx="92" cy="54" r="14" />
      </g>
      {/* Preenchimento para não aparecer contorno interno */}
      <g fill={hair}>
        <circle cx="60" cy="38" r="26" />
      </g>

      {/* Orelhas */}
      <g fill={skin} stroke={ink} strokeWidth="4">
        <ellipse cx="25" cy="72" rx="7" ry="9" />
        <ellipse cx="95" cy="72" rx="7" ry="9" />
      </g>

      {/* Rosto */}
      <ellipse cx="60" cy="72" rx="35" ry="34" fill={skin} stroke={ink} strokeWidth="4" />

      {/* Franja cacheada */}
      <g fill={hair} stroke={ink} strokeWidth="4">
        <circle cx="42" cy="42" r="10" />
        <circle cx="60" cy="38" r="11" />
        <circle cx="78" cy="42" r="10" />
      </g>

      {/* Espirais dos cachos */}
      <g fill="none" stroke={ink} strokeWidth="2.5" strokeLinecap="round">
        <path d="M46 20 a5 5 0 1 1 5 6" />
        <path d="M68 18 a5 5 0 1 1 5 6" />
        <path d="M28 33 a4 4 0 1 1 4 5" />
        <path d="M86 33 a4 4 0 1 1 4 5" />
        <path d="M57 37 a4 4 0 1 1 4 5" />
      </g>

      {/* Bochechas */}
      <g fill={blush} opacity="0.55">
        <ellipse cx="36" cy="84" rx="6" ry="4" />
        <ellipse cx="84" cy="84" rx="6" ry="4" />
      </g>

      {/* Sobrancelhas marotas */}
      <g stroke={ink} strokeWidth="5" strokeLinecap="round">
        <path d="M37 56 L53 62" />
        <path d="M83 56 L67 62" />
      </g>

      {/* Olhos */}
      <g>
        <circle cx="47" cy="68" r="6.5" fill="#fff" stroke={ink} strokeWidth="3" />
        <circle cx="73" cy="68" r="6.5" fill="#fff" stroke={ink} strokeWidth="3" />
        <circle cx="48.5" cy="68.5" r="2.8" fill={ink} />
        <circle cx="71.5" cy="68.5" r="2.8" fill={ink} />
      </g>

      {/* Nariz */}
      <path d="M56 76 q-4 6 1 8 q3 1.5 6 0 q5 -2 1 -8" fill="none" stroke={ink} strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />

      {/* Sorrisão */}
      <path
        d="M36 86 Q60 93 84 86 Q79 106 60 106 Q41 106 36 86 Z"
        fill="#fff"
        stroke={ink}
        strokeWidth="4"
        strokeLinejoin="round"
      />
      <g stroke={ink} strokeWidth="2.2" strokeLinecap="round">
        <path d="M40 96 Q60 101 80 96" fill="none" />
        <path d="M47 89 L47 97.5" />
        <path d="M55 90 L55 99" />
        <path d="M65 90 L65 99" />
        <path d="M73 89 L73 97.5" />
        <path d="M60 99.5 L60 105" />
      </g>
    </svg>
  )
}

export default Mascot
