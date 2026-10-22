// =========================================================
// Home — página inicial (/) — MM GAMES
// =========================================================
import { useState } from 'react'
import { Link } from 'react-router-dom'
import Mascot from '../../components/Logo/Mascot.jsx'
import ProductArt from '../../components/ProductArt/ProductArt.jsx'
import ProductCard from '../../components/ProductCard/ProductCard.jsx'
import { ArrowIcon, TruckIcon, RefreshIcon, LeafIcon } from '../../components/Icons.jsx'
import { categories, products } from '../../data/products.js'
import { isEmail } from '../../utils/validators.js'
import './Home.css'

const marqueeWords = [
  'Role o D20',
  'Sem tédio na mesa',
  'RPG & Board Games',
  'Menino Mau aprova',
  'Estratégia com atitude',
]

const pillars = [
  {
    title: 'Imersão',
    text: 'Nada de partida morna. Cada jogo do catálogo foi escolhido pra criar histórias memoráveis na mesa.',
    color: 'var(--laranja-200)',
  },
  {
    title: 'Qualidade',
    text: 'Componentes caprichados, livros de capa dura, dados balanceados e envio protegido com cantoneiras.',
    color: 'var(--roxo-200)',
  },
  {
    title: 'Atitude',
    text: 'Um blefe na hora certa, um 20 natural no dado. Jogos pra quem não tem medo de desafiar a mesa.',
    color: 'var(--laranja-300)',
  },
]

function Home() {
  const novidades = products.filter((p) => p.tags.includes('novo')).slice(0, 8)

  const [email, setEmail] = useState('')
  const [emailError, setEmailError] = useState('')
  const [subscribed, setSubscribed] = useState(false)

  const handleSubscribe = (e) => {
    e.preventDefault()
    if (!isEmail(email.trim())) {
      setEmailError('Informe um e-mail válido.')
      return
    }
    setEmailError('')
    setSubscribed(true)
  }

  return (
    <>
      {/* ---------- 1. HERO ---------- */}
      <section className="hero">
        <div className="container hero__inner">
          <div className="hero__text">
            <span className="eyebrow">Temporada de Campanhas · 2026</span>
            <h1 className="hero__title">
              Partida monótona? <span className="hero__highlight">Aqui não.</span>
            </h1>
            <p className="hero__lead">
              Jogos de tabuleiro, livros de RPG e acessórios cheios de detalhe — com aquela estratégia marota de quem
              sabe exatamente como dominar a mesa.
            </p>
            <div className="hero__cta">
              <Link to="/loja" className="btn btn--primary">
                Explorar catálogo <ArrowIcon size={18} />
              </Link>
              <Link to="/sobre" className="btn btn--ghost">
                Nossa história
              </Link>
            </div>
          </div>

          <div className="hero__visual" aria-hidden="true">
            <div className="hero__blob" />
            <div className="hero__mascot">
              <Mascot size={300} title="" />
            </div>
            <span className="sticker sticker--one">20 natural!</span>
            <span className="sticker sticker--two">zero tédio ✦</span>
            <div className="hero__float hero__float--tee">
              <ProductArt type="d20" color="#ffc596" bg="var(--surface)" />
            </div>
            <div className="hero__float hero__float--cap">
              <ProductArt type="cards" color="#c8b3f0" bg="var(--surface)" />
            </div>
          </div>
        </div>
      </section>

      {/* ---------- 2. FAIXA ANIMADA ---------- */}
      <div className="marquee" aria-hidden="true">
        <div className="marquee__track">
          {[...marqueeWords, ...marqueeWords, ...marqueeWords].map((w, i) => (
            <span key={i}>
              {w} <em>✦</em>
            </span>
          ))}
        </div>
      </div>

      {/* ---------- 3. CATEGORIAS ---------- */}
      <section className="section">
        <div className="container">
          <div className="section-head">
            <div>
              <span className="eyebrow">Escolha seu estilo de jogo</span>
              <h2 className="section-title">Categorias</h2>
            </div>
            <Link to="/loja" className="link-arrow">
              Ver tudo <ArrowIcon size={18} />
            </Link>
          </div>

          <div className="categories">
            {categories.map((c) => (
              <Link key={c.id} to={`/loja?categoria=${c.id}`} className="category" style={{ background: c.bg }}>
                <ProductArt type={c.art} color="#fff" bg="transparent" className="category__art" />
                <span className="category__name">{c.name}</span>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* ---------- 4. NOVIDADES ---------- */}
      <section className="section section--tinted">
        <div className="container">
          <div className="section-head">
            <div>
              <span className="eyebrow">Acabou de chegar na taverna</span>
              <h2 className="section-title">Destaques & Lançamentos</h2>
            </div>
            <Link to="/loja" className="link-arrow">
              Ir pro catálogo <ArrowIcon size={18} />
            </Link>
          </div>
          <div className="product-grid">
            {novidades.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </div>
      </section>

      {/* ---------- 5. MANIFESTO ---------- */}
      <section className="section">
        <div className="container manifesto">
          <div className="manifesto__intro">
            <span className="eyebrow">Manifesto</span>
            <h2 className="section-title">Jogar na MM GAMES é viver grandes campanhas.</h2>
            <p className="muted">
              A MM GAMES nasceu de uma conversa entre amigos na faculdade e virou ponto de encontro pra quem leva a
              diversão a sério: criatividade, estratégia e atitude a cada rodada.
            </p>
            <Link to="/sobre" className="btn btn--accent">
              Conheça a história
            </Link>
          </div>
          <div className="pillars">
            {pillars.map((p, i) => (
              <div key={p.title} className="pillar" style={{ background: p.color }}>
                <span className="pillar__num">0{i + 1}</span>
                <h3>{p.title}</h3>
                <p>{p.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ---------- 6. BENEFÍCIOS ---------- */}
      <section className="container benefits">
        <div className="benefit">
          <TruckIcon />
          <div>
            <strong>Frete grátis</strong>
            <span>em pedidos acima de R$ 299</span>
          </div>
        </div>
        <div className="benefit">
          <RefreshIcon />
          <div>
            <strong>Garantia de componentes</strong>
            <span>troca fácil em até 30 dias</span>
          </div>
        </div>
        <div className="benefit">
          <LeafIcon />
          <div>
            <strong>Envio blindado</strong>
            <span>caixas protegidas contra amassados</span>
          </div>
        </div>
      </section>

      {/* ---------- 7. NEWSLETTER ---------- */}
      <section className="section">
        <div className="container">
          <div className="newsletter">
            <div className="newsletter__mascot" aria-hidden="true">
              <Mascot size={120} title="" />
            </div>
            <div className="newsletter__content">
              <h2>Entra pra guilda do Menino Mau</h2>
              <p>Receba avisos de novos RPGs e board games e ganhe 10% no primeiro pedido.</p>
              {subscribed ? (
                <p className="newsletter__ok">
                  Prontinho! Use o cupom <strong>MENINOMAU10</strong> no carrinho.
                </p>
              ) : (
                <form className="newsletter__form" onSubmit={handleSubscribe} noValidate>
                  <label htmlFor="newsletter-email" className="sr-only">
                    Seu e-mail
                  </label>
                  <input
                    id="newsletter-email"
                    type="email"
                    className={`input ${emailError ? 'input--error' : ''}`}
                    placeholder="seu@email.com"
                    value={email}
                    onChange={(e) => {
                      setEmail(e.target.value)
                      setEmailError('')
                    }}
                    required
                  />
                  <button type="submit" className="btn btn--primary">
                    Quero entrar
                  </button>
                </form>
              )}
              {emailError && <p className="field-error">{emailError}</p>}
            </div>
          </div>
        </div>
      </section>
    </>
  )
}

export default Home
