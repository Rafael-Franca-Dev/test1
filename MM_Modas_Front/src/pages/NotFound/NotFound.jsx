// =========================================================
// NotFound — página 404 (qualquer URL que não existe)
// O mascote faz o papel do "0" em "404".
// =========================================================
import { Link } from 'react-router-dom'
import Mascot from '../../components/Logo/Mascot.jsx'
import './NotFound.css'

function NotFound() {
  return (
    <div className="container not-found">
      <div className="not-found__code" aria-hidden="true">
        4<Mascot size={150} title="" />4
      </div>
      <h1>Essa página fugiu de casa.</h1>
      <p className="muted">Coisa de Menino Mau. Mas relaxa, a loja continua aqui.</p>
      <Link to="/" className="btn btn--primary">
        Voltar pro início
      </Link>
    </div>
  )
}

export default NotFound
