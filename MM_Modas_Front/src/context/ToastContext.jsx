// =========================================================
// ToastContext — estado global de avisos flutuantes (Toast)
// ---------------------------------------------------------
// Separado do CartContext para respeitar o princípio de
// responsabilidade única (SRP) e evitar re-renderizações
// desnecessárias nos componentes que consomem o carrinho.
// =========================================================
import { createContext, useCallback, useContext, useMemo, useState } from 'react'

const ToastContext = createContext(null)

export function ToastProvider({ children }) {
  const [toast, setToast] = useState(null)

  const showToast = useCallback((message, link = null) => {
    setToast({ message, link, id: Date.now() })
  }, [])

  const dismissToast = useCallback(() => {
    setToast(null)
  }, [])

  const value = useMemo(
    () => ({ toast, showToast, dismissToast }),
    [toast, showToast, dismissToast],
  )

  return <ToastContext.Provider value={value}>{children}</ToastContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast precisa estar dentro de <ToastProvider>')
  return ctx
}

