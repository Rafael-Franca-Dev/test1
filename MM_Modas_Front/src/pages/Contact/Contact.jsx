// =========================================================
// Contact — página de contato (/contato) — MM GAMES
// =========================================================
import { useState } from 'react'
import { useLocation } from 'react-router-dom'
import Mascot from '../../components/Logo/Mascot.jsx'
import { InstagramIcon, WhatsIcon } from '../../components/Icons.jsx'
import { faq } from '../../data/faq.js'
import { isEmail } from '../../utils/validators.js'
import './Contact.css'

const subjects = [
  'Dúvida sobre jogo ou RPG',
  'Meu pedido',
  'Trocas e reposição de peças',
  'Eventos e parcerias',
  'Outro assunto',
]

const emptyForm = { nome: '', email: '', assunto: subjects[0], mensagem: '' }

function Contact() {
  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [sent, setSent] = useState(false)

  const { hash } = useLocation()
  const targetFaq = hash.startsWith('#faq-') ? hash.replace('#faq-', '') : null

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((f) => ({ ...f, [name]: value }))
    setErrors((err) => ({ ...err, [name]: undefined }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    const err = {}
    if (!form.nome.trim()) err.nome = 'Como a gente te chama?'
    if (!isEmail(form.email)) err.email = 'Coloca um e-mail válido.'
    if (form.mensagem.trim().length < 10) err.mensagem = 'Escreve um pouquinho mais (mín. 10 caracteres).'
    setErrors(err)
    if (Object.keys(err).length === 0) setSent(true)
  }

  return (
    <div className="container">
      <header className="page-head">
        <span className="eyebrow">Fale com a gente</span>
        <h1>Bora trocar uma ideia?</h1>
        <p className="muted">Dúvida de regra, recomendação de jogo ou pedido: a gente responde rapidinho.</p>
      </header>

      <div className="contact">
        <div className="contact__card">
          {sent ? (
            <div className="contact__sent">
              <Mascot size={110} title="" />
              <h2>Mensagem enviada!</h2>
              <p className="muted">
                Valeu, {form.nome.split(' ')[0]}. Logo a gente te responde no {form.email}.
              </p>
              <button
                type="button"
                className="btn btn--accent"
                onClick={() => {
                  setSent(false)
                  setForm(emptyForm)
                }}
              >
                Enviar outra
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit} noValidate>
              <div className="contact__row">
                <div className="field">
                  <label htmlFor="nome">Nome</label>
                  <input
                    id="nome"
                    name="nome"
                    className={`input ${errors.nome ? 'input--error' : ''}`}
                    value={form.nome}
                    onChange={handleChange}
                  />
                  {errors.nome && <span className="field-error">{errors.nome}</span>}
                </div>
                <div className="field">
                  <label htmlFor="email">E-mail</label>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    className={`input ${errors.email ? 'input--error' : ''}`}
                    value={form.email}
                    onChange={handleChange}
                  />
                  {errors.email && <span className="field-error">{errors.email}</span>}
                </div>
              </div>

              <div className="field">
                <label htmlFor="assunto">Assunto</label>
                <select id="assunto" name="assunto" className="input" value={form.assunto} onChange={handleChange}>
                  {subjects.map((s) => (
                    <option key={s}>{s}</option>
                  ))}
                </select>
              </div>

              <div className="field">
                <label htmlFor="mensagem">Mensagem</label>
                <textarea
                  id="mensagem"
                  name="mensagem"
                  rows="5"
                  className={`input ${errors.mensagem ? 'input--error' : ''}`}
                  value={form.mensagem}
                  onChange={handleChange}
                />
                {errors.mensagem && <span className="field-error">{errors.mensagem}</span>}
              </div>

              <button type="submit" className="btn btn--primary">
                Enviar mensagem
              </button>
            </form>
          )}
        </div>

        <aside className="contact__side">
          <div className="contact__channels">
            <h2>Outros canais</h2>
            <a href="https://wa.me/5581900000000" target="_blank" rel="noreferrer" className="channel">
              <WhatsIcon /> <span>WhatsApp<small>(81) 90000-0000</small></span>
            </a>
            <a href="https://instagram.com" target="_blank" rel="noreferrer" className="channel">
              <InstagramIcon /> <span>Instagram<small>@mm.games</small></span>
            </a>
          </div>

          <div className="faq" id="faq">
            <h2>Perguntas frequentes</h2>
            {faq.map((item) => (
              <details
                key={`${item.id}-${hash}`}
                id={`faq-${item.id}`}
                className={`faq__item ${targetFaq === item.id ? 'faq__item--target' : ''}`}
                open={targetFaq === item.id}
              >
                <summary>{item.q}</summary>
                <p>{item.a}</p>
              </details>
            ))}
          </div>
        </aside>
      </div>
    </div>
  )
}

export default Contact
