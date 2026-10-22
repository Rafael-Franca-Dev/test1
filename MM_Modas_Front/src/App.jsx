// =========================================================
// App — estrutura fixa da página + tabela de rotas
// ---------------------------------------------------------
// Header e Footer aparecem em todas as páginas; o conteúdo
// do <main> muda conforme a URL (definido em <Routes>).
// =========================================================
import { Routes, Route } from 'react-router-dom'
import Header from './components/Header/Header.jsx'
import Footer from './components/Footer/Footer.jsx'
import Toast from './components/Toast/Toast.jsx'
import ScrollToTop from './components/ScrollToTop.jsx'
import Home from './pages/Home/Home.jsx'
import Shop from './pages/Shop/Shop.jsx'
import ProductDetail from './pages/ProductDetail/ProductDetail.jsx'
import Cart from './pages/Cart/Cart.jsx'
import Checkout from './pages/Checkout/Checkout.jsx'
import Register from './pages/Register/Register.jsx'
import About from './pages/About/About.jsx'
import Contact from './pages/Contact/Contact.jsx'
import NotFound from './pages/NotFound/NotFound.jsx'

function App() {
  return (
    <>
      {/* Controla a rolagem ao trocar de página (topo ou âncora #) */}
      <ScrollToTop />
      <Header />

      <main className="main">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/loja" element={<Shop />} />
          {/* :slug é um parâmetro: /produto/camiseta-sorriso-maroto */}
          <Route path="/produto/:slug" element={<ProductDetail />} />
          <Route path="/carrinho" element={<Cart />} />
          {/* O Checkout redireciona para /cadastro se não houver usuário */}
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/cadastro" element={<Register />} />
          <Route path="/sobre" element={<About />} />
          <Route path="/contato" element={<Contact />} />
          {/* Qualquer outra URL cai na página 404 */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>

      <Footer />
      {/* Aviso flutuante (ex.: "foi pro carrinho!") */}
      <Toast />
    </>
  )
}

export default App
