// =========================================================
// Header — barra de aviso + cabeçalho fixo do site MM GAMES
// =========================================================
import { useEffect, useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import Logo from '../Logo/Logo.jsx'
import { BagIcon, MenuIcon, CloseIcon, UserIcon } from '../Icons.jsx'
import { useCart } from '../../context/CartContext.jsx'
import { useUser } from '../../context/UserContext.jsx'
import './Header.css'

const links = [
  { to: '/', label: 'Início', end: true },
  { to: '/loja', label: 'Catálogo' },
  { to: '/sobre', label: 'Nossa história' },
  { to: '/contato', label: 'Contato' },
]

function Header() {
  const [open, setOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const { count } = useCart()
  const { user } = useUser()

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 10)
    window.addEventListener('scroll', onScroll)
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  const close = () => setOpen(false)
  const firstName = user?.nome?.split(' ')[0]

  return (
    <>
      <div className="announcement">
        <p>
          Frete grátis acima de R$ 299 · Primeira campanha? Use <strong>MENINOMAU10</strong>
        </p>
      </div>

      <header className={`header ${scrolled ? 'header--scrolled' : ''}`}>
        <div className="container header__inner">
          <Link to="/" className="header__logo" onClick={close} aria-label="MM GAMES — página inicial">
            <Logo size={44} />
          </Link>

          <nav className={`header__nav ${open ? 'header__nav--open' : ''}`} aria-label="Principal">
            {links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                end={link.end}
                onClick={close}
                className={({ isActive }) => `header__link ${isActive ? 'header__link--active' : ''}`}
              >
                {link.label}
              </NavLink>
            ))}
          </nav>

          <div className="header__actions">
            <NavLink
              to="/cadastro"
              onClick={close}
              className={({ isActive }) => `header__account ${isActive ? 'header__account--active' : ''}`}
              aria-label={user ? `Minha conta (${firstName})` : 'Entrar ou cadastrar'}
            >
              <UserIcon size={20} />
              <span className="header__account-label">{user ? `Olá, ${firstName}` : 'Entrar'}</span>
              {user?.tipo === 'funcionario' && <span className="header__staff">equipe</span>}
            </NavLink>

            <Link to="/carrinho" className="header__cart" onClick={close} aria-label={`Carrinho com ${count} itens`}>
              <BagIcon />
              {count > 0 && <span className="header__badge">{count}</span>}
            </Link>

            <button
              type="button"
              className="header__toggle"
              onClick={() => setOpen((v) => !v)}
              aria-expanded={open}
              aria-label={open ? 'Fechar menu' : 'Abrir menu'}
            >
              {open ? <CloseIcon /> : <MenuIcon />}
            </button>
          </div>
        </div>
      </header>
    </>
  )
}

export default Header
