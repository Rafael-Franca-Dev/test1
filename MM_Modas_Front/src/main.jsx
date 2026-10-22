// =========================================================
// Ponto de entrada da aplicação — MM GAMES
// ---------------------------------------------------------
// Monta o React dentro da <div id="root"> do index.html e
// envolve o App com os provedores globais desacoplados:
//  - BrowserRouter: rotas (React Router)
//  - ToastProvider: avisos flutuantes independentes
//  - UserProvider: sessão e autenticação do usuário
//  - CartProvider: estado do carrinho de compras
// =========================================================
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { ToastProvider } from './context/ToastContext.jsx'
import { UserProvider } from './context/UserContext.jsx'
import { CartProvider } from './context/CartContext.jsx'
import App from './App.jsx'
import './styles/global.css'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <ToastProvider>
        <UserProvider>
          <CartProvider>
            <App />
          </CartProvider>
        </UserProvider>
      </ToastProvider>
    </BrowserRouter>
  </StrictMode>,
)
