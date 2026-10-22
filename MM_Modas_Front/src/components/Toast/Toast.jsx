// =========================================================
// Toast — aviso flutuante no canto da tela
// ---------------------------------------------------------
// Aparece quando algo acontece (jogo adicionado, cadastro
// concluído...) e some sozinho depois de ~3 segundos.
// =========================================================
import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import Mascot from '../Logo/Mascot.jsx'
import { useToast } from '../../context/ToastContext.jsx'
import './Toast.css'

function Toast() {
  const { toast, dismissToast } = useToast()

  useEffect(() => {
    if (!toast) return
    const timer = setTimeout(dismissToast, 3200)
    return () => clearTimeout(timer)
  }, [toast, dismissToast])

  if (!toast) return null

  return (
    <div className="toast" role="status" key={toast.id}>
      <Mascot size={40} title="" />
      <div>
        <p className="toast__msg">{toast.message}</p>
        {toast.link && (
          <Link to={toast.link.to} className="toast__link" onClick={dismissToast}>
            {toast.link.label}
          </Link>
        )}
      </div>
    </div>
  )
}

export default Toast
