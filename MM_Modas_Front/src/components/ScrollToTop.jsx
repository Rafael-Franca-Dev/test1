// =========================================================
// ScrollToTop — controla a rolagem ao navegar
// ---------------------------------------------------------
// - URL sem âncora (/loja): volta para o topo da página.
// - URL com âncora (/contato#faq-trocas): rola até o elemento
//   com aquele id. Usado pelos links "Ajuda" do rodapé.
// Não renderiza nada na tela (retorna null).
// =========================================================
import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'

function ScrollToTop() {
  const { pathname, hash } = useLocation()

  useEffect(() => {
    if (hash) {
      // Espera a nova página renderizar antes de procurar o elemento
      const timer = setTimeout(() => {
        const el = document.getElementById(hash.slice(1))
        if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }, 60)
      return () => clearTimeout(timer)
    }
    window.scrollTo(0, 0)
  }, [pathname, hash])

  return null
}

export default ScrollToTop
