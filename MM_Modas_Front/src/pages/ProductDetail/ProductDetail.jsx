// =========================================================
// ProductDetail — página de um jogo/RPG (/produto/:slug)
// ---------------------------------------------------------
// Exibe ilustração/foto, preço, descrição, seletor de tema
// (quando o item possui múltiplas cores de arte) e seletor
// de edição (Padrão, Deluxe, Físico + PDF...), além de
// sugestões da mesma categoria ("Combina na mesa").
// =========================================================
import { useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import ProductArt from '../../components/ProductArt/ProductArt.jsx'
import ProductCard from '../../components/ProductCard/ProductCard.jsx'
import { BackIcon, TruckIcon, RefreshIcon } from '../../components/Icons.jsx'
import { getProductBySlug, getCategoryName, products } from '../../data/products.js'
import { useCart } from '../../context/CartContext.jsx'
import { formatPrice, installments } from '../../utils/format.js'
import NotFound from '../NotFound/NotFound.jsx'
import './ProductDetail.css'

function ProductDetail() {
  const { slug } = useParams()
  const product = getProductBySlug(slug)

  if (!product) return <NotFound />

  return <ProductView key={slug} product={product} />
}

function ProductView({ product }) {
  const { addItem } = useCart()
  const navigate = useNavigate()
  const location = useLocation()

  const handleBack = () => {
    if (location.key !== 'default') navigate(-1)
    else navigate('/loja')
  }

  const [color, setColor] = useState(product.colors[0])
  const [size, setSize] = useState(product.sizes.length === 1 ? product.sizes[0] : null)
  const [qty, setQty] = useState(1)
  const [sizeError, setSizeError] = useState(false)

  const related = products.filter((p) => p.category === product.category && p.id !== product.id).slice(0, 4)
  const fallback = products.filter((p) => p.id !== product.id).slice(0, 4)

  const handleAdd = () => {
    if (!size) {
      setSizeError(true)
      return
    }
    addItem({
      id: product.id,
      variacaoId: product.variacaoId,
      slug: product.slug,
      name: product.name,
      type: product.type,
      price: product.price,
      colorHex: color.hex,
      colorName: color.name,
      image: product.image,
      size,
      qty,
    })
  }

  return (
    <div className="container">
      <button type="button" className="btn btn--ghost btn--back" onClick={handleBack}>
        <BackIcon size={18} /> Voltar
      </button>

      <nav className="breadcrumb" aria-label="Você está em">
        <Link to="/">Início</Link> / <Link to="/loja">Catálogo</Link> /{' '}
        <Link to={`/loja?categoria=${product.category}`}>{getCategoryName(product.category)}</Link> /{' '}
        <span aria-current="page">{product.name}</span>
      </nav>

      <section className="pdp">
        <div className="pdp__gallery">
          <ProductArt
            type={product.type}
            color={color.hex}
            image={product.image}
            bg="var(--roxo-100)"
            className="pdp__art"
            label={product.name}
          />
        </div>

        <div className="pdp__info">
          <div className="pdp__tags">
            {product.tags.includes('novo') && <span className="tag">Destaque</span>}
          </div>
          <h1 className="pdp__name">{product.name}</h1>

          <div className="pdp__price">
            <strong>{formatPrice(product.price)}</strong>
            {product.oldPrice && <s>{formatPrice(product.oldPrice)}</s>}
          </div>
          <p className="muted pdp__installments">{installments(product.price)}</p>

          <p className="pdp__desc">{product.description}</p>

          {product.colors.length > 1 && (
            <fieldset className="pdp__group">
              <legend>
                Tema / Acabamento: <strong>{color.name}</strong>
              </legend>
              <div className="pdp__swatches">
                {product.colors.map((c) => (
                  <button
                    key={c.name}
                    type="button"
                    className={`swatch ${c.name === color.name ? 'swatch--active' : ''}`}
                    style={{ background: c.hex }}
                    onClick={() => setColor(c)}
                    aria-label={c.name}
                    aria-pressed={c.name === color.name}
                    title={c.name}
                  />
                ))}
              </div>
            </fieldset>
          )}

          <fieldset className="pdp__group">
            <legend>
              Edição{size && <>: <strong>{size}</strong></>}
            </legend>
            <div className="pdp__sizes">
              {product.sizes.map((s) => (
                <button
                  key={s}
                  type="button"
                  className={`size ${s === size ? 'size--active' : ''}`}
                  onClick={() => {
                    setSize(s)
                    setSizeError(false)
                  }}
                  aria-pressed={s === size}
                >
                  {s}
                </button>
              ))}
            </div>
            {sizeError && <p className="field-error">Escolha a edição primeiro para continuar!</p>}
          </fieldset>

          <div className="pdp__buy">
            <div className="qty" aria-label="Quantidade">
              <button type="button" onClick={() => setQty((q) => Math.max(1, q - 1))} aria-label="Diminuir">
                −
              </button>
              <span>{qty}</span>
              <button type="button" onClick={() => setQty((q) => q + 1)} aria-label="Aumentar">
                +
              </button>
            </div>
            <button type="button" className="btn btn--primary pdp__add" onClick={handleAdd}>
              Adicionar ao carrinho
            </button>
          </div>

          <ul className="pdp__perks">
            <li>
              <TruckIcon size={20} /> Frete grátis acima de R$ 299
            </li>
            <li>
              <RefreshIcon size={20} /> Embalagem blindada e reposição de peças
            </li>
          </ul>
        </div>
      </section>

      <section className="section">
        <h2 className="section-title">Combina na mesa</h2>
        <div className="product-grid">
          {(related.length ? related : fallback).map((p) => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
      </section>
    </div>
  )
}

export default ProductDetail
