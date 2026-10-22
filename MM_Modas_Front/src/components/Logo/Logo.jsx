// =========================================================
// Logo — selo com o mascote + "MM / MODAS"
// Props: size (tamanho do mascote) e light (versão para fundo escuro)
// =========================================================
import Mascot from './Mascot.jsx'
import './Logo.css'

function Logo({ size = 48, light = false }) {
  return (
    <span className={`logo ${light ? 'logo--light' : ''}`}>
      <span className="logo__badge">
        <Mascot size={size} title="" />
      </span>
      <span className="logo__text">
        <span className="logo__mm">MM</span>
        <span className="logo__modas">GAMES</span>
      </span>
    </span>
  )
}

export default Logo
