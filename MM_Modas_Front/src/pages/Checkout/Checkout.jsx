// =========================================================
// Checkout — finalizar compra (/checkout) — MM GAMES
// ---------------------------------------------------------
// Correções estruturais aplicadas:
//  - Endereço completo (CEP, rua, número, complemento, bairro, cidade, UF)
//  - Busca automática de CEP via ViaCEP com proteção contra race condition
//  - Recálculo automático do frete por UF (via API / fallback local)
//  - Resumo completo exibindo Subtotal, Desconto, Frete e Total
//  - Envio do pedido para POST /api/pedidos com chave de idempotência
// =========================================================
import { useEffect, useRef, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import Mascot from '../../components/Logo/Mascot.jsx'
import FormField from '../../components/FormField/FormField.jsx'
import { useCart } from '../../context/CartContext.jsx'
import { useUser } from '../../context/UserContext.jsx'
import { formatPrice } from '../../utils/format.js'
import { maskCep, isCep, isEmail, fetchAddressByCep, UFS } from '../../utils/validators.js'
import { apiCreateOrder } from '../../services/api.js'
import './Checkout.css'

const paymentOptions = [
  { id: 'pix', label: 'Pix', hint: 'aprovação na hora' },
  { id: 'cartao', label: 'Cartão', hint: 'até 3x sem juros' },
  { id: 'boleto', label: 'Boleto', hint: 'vence em 2 dias' },
]

function Checkout() {
  const { items, subtotal, discount, shipping, total, coupon, count, clearCart, setShippingUf } = useCart()
  const { user, updateUserAddress } = useUser()
  const cepRequestRef = useRef(0)

  const [idempotencyKey] = useState(() =>
    crypto.randomUUID ? crypto.randomUUID() : `mm-${Date.now()}-${Math.random().toString(36).slice(2)}`,
  )

  const [form, setForm] = useState(() => ({
    nome: user?.nome ?? '',
    email: user?.email ?? '',
    cep: user?.cep ?? '',
    endereco: user?.endereco ?? '',
    numero: user?.numero ?? '',
    complemento: user?.complemento ?? '',
    bairro: user?.bairro ?? '',
    cidade: user?.cidade ?? '',
    uf: user?.uf ?? 'SP',
    pagamento: 'pix',
  }))
  const [errors, setErrors] = useState({})
  const [cepStatus, setCepStatus] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState('')
  const [orderNumber, setOrderNumber] = useState(null)

  useEffect(() => {
    if (form.uf) {
      setShippingUf(form.uf)
    }
  }, [form.uf, setShippingUf])

  if (orderNumber) {
    return (
      <div className="container">
        <div className="order-ok">
          <Mascot size={150} title="" />
          <span className="eyebrow">Pedido #{orderNumber}</span>
          <h1>Boa jogada, {form.nome.split(' ')[0]}!</h1>
          <p className="muted">
            Seu pedido foi confirmado e os itens já foram reservados. Enviamos os detalhes para{' '}
            <strong>{form.email}</strong>.
          </p>
          <Link to="/loja" className="btn btn--primary">
            Voltar ao catálogo
          </Link>
        </div>
      </div>
    )
  }

  if (!user) return <Navigate to="/cadastro" replace state={{ from: '/checkout' }} />
  if (items.length === 0) return <Navigate to="/carrinho" replace />

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((f) => ({ ...f, [name]: value }))
    setErrors((err) => ({ ...err, [name]: undefined }))
  }

  const handleCep = async (e) => {
    const cep = maskCep(e.target.value)
    setForm((f) => ({ ...f, cep }))
    setErrors((err) => ({ ...err, cep: undefined }))

    if (cep.length !== 8) {
      setCepStatus(null)
      return
    }

    const reqId = ++cepRequestRef.current
    setCepStatus('loading')
    const address = await fetchAddressByCep(cep)
    if (reqId !== cepRequestRef.current) return

    if (address) {
      setForm((f) => ({
        ...f,
        endereco: address.endereco || f.endereco,
        bairro: address.bairro || f.bairro,
        cidade: address.cidade || f.cidade,
        uf: address.uf || f.uf,
      }))
      setErrors((err) => ({
        ...err,
        endereco: undefined,
        bairro: undefined,
        cidade: undefined,
        uf: undefined,
      }))
      setCepStatus('ok')
    } else {
      setCepStatus('notfound')
    }
  }

  const validate = () => {
    const err = {}
    if (form.nome.trim().length < 3) err.nome = 'Informe seu nome completo.'
    if (!isEmail(form.email)) err.email = 'E-mail inválido.'
    if (!isCep(form.cep)) err.cep = 'O CEP deve ter exatamente 8 números.'
    if (!form.endereco.trim()) err.endereco = 'Informe a rua ou avenida.'
    if (!form.numero.trim()) err.numero = 'Informe o número.'
    if (!form.bairro.trim()) err.bairro = 'Informe o bairro.'
    if (!form.cidade.trim()) err.cidade = 'Informe a cidade.'
    if (!form.uf) err.uf = 'Escolha a UF.'
    return err
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const err = validate()
    setErrors(err)
    setSubmitError('')

    if (Object.keys(err).length > 0) return

    setSubmitting(true)
    try {
      const pedido = await apiCreateOrder({
        user,
        items,
        couponCode: coupon?.code,
        delivery: form,
        idempotencyKey,
      })
      updateUserAddress({
        cep: form.cep,
        endereco: form.endereco,
        numero: form.numero,
        complemento: form.complemento,
        bairro: form.bairro,
        cidade: form.cidade,
        uf: form.uf,
      })
      setOrderNumber(pedido.numero)
      clearCart()
    } catch (error) {
      setSubmitError(error.message || 'Não foi possível concluir o pedido. Tente novamente.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="container">
      <header className="page-head">
        <span className="eyebrow">Último passo</span>
        <h1>Finalizar pedido</h1>
        <p className="muted">
          Comprando como <strong>{user.nome}</strong>
          {user.tipo === 'funcionario' && ' (equipe MM)'}. <Link to="/cadastro">Ver minha conta</Link>
        </p>
      </header>

      <form className="checkout" onSubmit={handleSubmit} noValidate>
        <div className="checkout__form">
          <h2>Seus dados</h2>
          <FormField
            id="chk-nome"
            name="nome"
            label="Nome completo"
            value={form.nome}
            onChange={handleChange}
            error={errors.nome}
            inputProps={{ autoComplete: 'name' }}
          />
          <FormField
            id="chk-email"
            name="email"
            label="E-mail"
            value={form.email}
            onChange={handleChange}
            error={errors.email}
            inputProps={{ type: 'email', autoComplete: 'email' }}
          />

          <h2>Endereço de entrega</h2>
          <div className="checkout__row">
            <FormField
              id="chk-cep"
              name="cep"
              label="CEP"
              value={form.cep}
              onChange={handleCep}
              error={errors.cep}
              hint={
                cepStatus === 'loading'
                  ? 'Buscando CEP…'
                  : cepStatus === 'ok'
                    ? 'Endereço atualizado!'
                    : cepStatus === 'notfound'
                      ? 'CEP não encontrado — preencha abaixo.'
                      : undefined
              }
              inputProps={{ inputMode: 'numeric', maxLength: 9, placeholder: '8 dígitos, só números' }}
            />
            <FormField
              id="chk-numero"
              name="numero"
              label="Número"
              value={form.numero}
              onChange={handleChange}
              error={errors.numero}
            />
          </div>
          <FormField
            id="chk-endereco"
            name="endereco"
            label="Rua / Avenida"
            value={form.endereco}
            onChange={handleChange}
            error={errors.endereco}
            inputProps={{ autoComplete: 'street-address' }}
          />
          <div className="checkout__row">
            <FormField
              id="chk-bairro"
              name="bairro"
              label="Bairro"
              value={form.bairro}
              onChange={handleChange}
              error={errors.bairro}
            />
            <FormField
              id="chk-complemento"
              name="complemento"
              label="Complemento (opcional)"
              value={form.complemento}
              onChange={handleChange}
            />
          </div>
          <div className="checkout__row">
            <FormField
              id="chk-cidade"
              name="cidade"
              label="Cidade"
              value={form.cidade}
              onChange={handleChange}
              error={errors.cidade}
            />
            <div className="field">
              <label htmlFor="chk-uf">UF</label>
              <select
                id="chk-uf"
                name="uf"
                className={`input ${errors.uf ? 'input--error' : ''}`}
                value={form.uf}
                onChange={handleChange}
              >
                <option value="">--</option>
                {UFS.map((uf) => (
                  <option key={uf} value={uf}>
                    {uf}
                  </option>
                ))}
              </select>
              {errors.uf && <span className="field-error">{errors.uf}</span>}
            </div>
          </div>

          <h2>Pagamento</h2>
          <div className="pay-options">
            {paymentOptions.map((opt) => (
              <label key={opt.id} className={`pay-option ${form.pagamento === opt.id ? 'pay-option--active' : ''}`}>
                <input
                  type="radio"
                  name="pagamento"
                  value={opt.id}
                  checked={form.pagamento === opt.id}
                  onChange={handleChange}
                />
                <strong>{opt.label}</strong>
                <span>{opt.hint}</span>
              </label>
            ))}
          </div>

          {submitError && (
            <p className="field-error" role="alert" style={{ marginTop: 'var(--space-4)' }}>
              {submitError}
            </p>
          )}
        </div>

        <aside className="summary">
          <h2>Seu pedido</h2>
          <p className="muted">
            {count} {count === 1 ? 'item' : 'itens'} no carrinho
          </p>
          <ul className="checkout__list">
            {items.map((i) => (
              <li key={i.key}>
                <span>
                  {i.qty}× {i.name} <small className="muted">({i.size})</small>
                </span>
                <span>{formatPrice(i.price * i.qty)}</span>
              </li>
            ))}
          </ul>

          <dl>
            <div>
              <dt>Subtotal</dt>
              <dd>{formatPrice(subtotal)}</dd>
            </div>
            {discount > 0 && (
              <div className="summary__discount">
                <dt>Desconto {coupon ? `(${coupon.code})` : ''}</dt>
                <dd>− {formatPrice(discount)}</dd>
              </div>
            )}
            <div>
              <dt>Frete ({form.uf || 'SP'})</dt>
              <dd>{shipping === 0 ? 'Grátis' : formatPrice(shipping)}</dd>
            </div>
            <div className="summary__total">
              <dt>Total</dt>
              <dd>{formatPrice(total)}</dd>
            </div>
          </dl>

          <button type="submit" className="btn btn--primary btn--block" disabled={submitting}>
            {submitting ? 'Confirmando pedido…' : 'Confirmar pedido'}
          </button>
          <Link to="/carrinho" className="summary__continue">
            Voltar ao carrinho
          </Link>
        </aside>
      </form>
    </div>
  )
}

export default Checkout
