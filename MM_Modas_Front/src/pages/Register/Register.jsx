// =========================================================
// Register — página de Login, Cadastro e Minha Conta (/cadastro)
// ---------------------------------------------------------
// Correções estruturais aplicadas:
//  - Adicionada aba de Login para que usuários já cadastrados
//    possam sair (logout) e entrar novamente sem perder a conta.
//  - Desacoplado do CartContext para notificações (usa useToast).
//  - Usa o componente reutilizável <FormField />.
//  - Controle de concorrência (requestId) na busca do ViaCEP.
// =========================================================
import { useRef, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import Mascot from '../../components/Logo/Mascot.jsx'
import FormField from '../../components/FormField/FormField.jsx'
import { useUser } from '../../context/UserContext.jsx'
import { useCart } from '../../context/CartContext.jsx'
import { useToast } from '../../context/ToastContext.jsx'
import {
  maskCep,
  maskCpf,
  maskPhone,
  maskMatricula,
  isEmail,
  isCep,
  isCpf,
  isPhone,
  isMatricula,
  fetchAddressByCep,
  UFS,
} from '../../utils/validators.js'
import './Register.css'

const initialForm = {
  tipo: 'cliente',
  nome: '',
  email: '',
  cpf: '',
  telefone: '',
  matricula: '',
  cargo: 'Atendimento',
  cep: '',
  endereco: '',
  numero: '',
  complemento: '',
  bairro: '',
  cidade: '',
  uf: '',
  senha: '',
  confirmarSenha: '',
  termos: false,
}

const cargos = ['Atendimento', 'Curadoria de Jogos', 'Estoque', 'Mestre de Mesa / Eventos', 'Administrativo']

function validateRegister(form) {
  const err = {}
  if (form.nome.trim().split(/\s+/).length < 2) err.nome = 'Informe nome e sobrenome.'
  if (!isEmail(form.email)) err.email = 'E-mail inválido.'
  if (!isCpf(form.cpf)) err.cpf = 'CPF inválido. Confira os números.'
  if (!isPhone(form.telefone)) err.telefone = 'Telefone com DDD, ex.: (81) 99999-9999.'
  if (form.tipo === 'funcionario' && !isMatricula(form.matricula)) {
    err.matricula = 'A matrícula deve ter exatamente 8 caracteres (letras e números).'
  }
  if (!isCep(form.cep)) err.cep = 'CEP deve ter 8 números.'
  if (!form.endereco.trim()) err.endereco = 'Informe a rua.'
  if (!form.numero.trim()) err.numero = 'Informe o número.'
  if (!form.bairro.trim()) err.bairro = 'Informe o bairro.'
  if (!form.cidade.trim()) err.cidade = 'Informe a cidade.'
  if (!form.uf) err.uf = 'Escolha o estado.'
  if (form.senha.length < 6) err.senha = 'Mínimo de 6 caracteres.'
  if (form.confirmarSenha !== form.senha) err.confirmarSenha = 'As senhas não conferem.'
  if (!form.termos) err.termos = 'Você precisa aceitar os termos.'
  return err
}

function Register() {
  const { user, register, login, logout } = useUser()
  const { count } = useCart()
  const { showToast } = useToast()
  const navigate = useNavigate()
  const location = useLocation()
  const cepRequestRef = useRef(0)

  const from = location.state?.from

  const [mode, setMode] = useState('register') // 'register' | 'login'
  const [form, setForm] = useState(initialForm)
  const [loginForm, setLoginForm] = useState({ email: '', senha: '' })
  const [errors, setErrors] = useState({})
  const [authError, setAuthError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [cepStatus, setCepStatus] = useState(null)

  // ---------- Usuário autenticado: exibe "Minha conta" ----------
  if (user) {
    return (
      <div className="container">
        <header className="page-head">
          <span className="eyebrow">{user.tipo === 'funcionario' ? 'Conta da equipe MM' : 'Conta de jogador'}</span>
          <h1>Olá, {user.nome.split(' ')[0]}!</h1>
          <p className="muted">Sua conta está ativa e pronta para a próxima campanha.</p>
        </header>

        <div className="account">
          <div className="account__card">
            <h2>Seus dados</h2>
            <dl className="account__data">
              <div><dt>Nome</dt><dd>{user.nome}</dd></div>
              <div><dt>E-mail</dt><dd>{user.email}</dd></div>
              {user.cpf && <div><dt>CPF</dt><dd>{user.cpf}</dd></div>}
              {user.telefone && <div><dt>Telefone</dt><dd>{user.telefone}</dd></div>}
              {user.tipo === 'funcionario' && (
                <>
                  {user.matricula && <div><dt>Matrícula</dt><dd>{user.matricula}</dd></div>}
                  {user.cargo && <div><dt>Cargo</dt><dd>{user.cargo}</dd></div>}
                </>
              )}
              {user.endereco && (
                <div className="account__full">
                  <dt>Endereço principal</dt>
                  <dd>
                    {user.endereco}, {user.numero}
                    {user.complemento && ` — ${user.complemento}`} · {user.bairro} · {user.cidade}/{user.uf} · CEP{' '}
                    {user.cep}
                  </dd>
                </div>
              )}
            </dl>
          </div>

          <aside className="account__actions">
            {count > 0 && (
              <Link to="/checkout" className="btn btn--primary btn--block">
                Finalizar minha compra
              </Link>
            )}
            <Link to="/loja" className="btn btn--accent btn--block">
              Explorar catálogo
            </Link>
            <button
              type="button"
              className="btn btn--ghost btn--block"
              onClick={() => {
                logout()
                setMode('login')
                showToast('Você saiu da sua conta.')
              }}
            >
              Sair da conta
            </button>
          </aside>
        </div>
      </div>
    )
  }

  // ---------- Handlers de Cadastro ----------
  const setField = (name, value) => {
    setForm((f) => ({ ...f, [name]: value }))
    setErrors((e) => ({ ...e, [name]: undefined }))
    setAuthError('')
  }

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    if (type === 'checkbox') return setField(name, checked)
    if (name === 'cpf') return setField(name, maskCpf(value))
    if (name === 'telefone') return setField(name, maskPhone(value))
    if (name === 'matricula') return setField(name, maskMatricula(value))
    setField(name, value)
  }

  const handleCep = async (e) => {
    const cep = maskCep(e.target.value)
    setField('cep', cep)
    if (cep.length !== 8) {
      setCepStatus(null)
      return
    }

    const reqId = ++cepRequestRef.current
    setCepStatus('loading')
    const address = await fetchAddressByCep(cep)
    if (reqId !== cepRequestRef.current) return

    if (address) {
      setForm((f) => ({ ...f, ...address }))
      setErrors((err) => ({ ...err, endereco: undefined, bairro: undefined, cidade: undefined, uf: undefined }))
      setCepStatus('ok')
    } else {
      setCepStatus('notfound')
    }
  }

  const handleRegisterSubmit = async (e) => {
    e.preventDefault()
    const err = validateRegister(form)
    setErrors(err)
    setAuthError('')

    if (Object.keys(err).length > 0) return

    const data = { ...form }
    if (data.tipo === 'cliente') {
      delete data.matricula
      delete data.cargo
    }

    setSubmitting(true)
    try {
      await register(data)
      showToast(`Cadastro feito! Bem-vindo à guilda, ${form.nome.split(' ')[0]}.`)
      if (from) navigate(from, { replace: true })
    } catch (error) {
      setAuthError(error.message || 'Erro ao criar conta.')
    } finally {
      setSubmitting(false)
    }
  }

  // ---------- Handlers de Login ----------
  const handleLoginSubmit = async (e) => {
    e.preventDefault()
    const err = {}
    if (!isEmail(loginForm.email)) err.loginEmail = 'Informe um e-mail válido.'
    if (!loginForm.senha) err.loginSenha = 'Informe sua senha.'
    setErrors(err)
    setAuthError('')

    if (Object.keys(err).length > 0) return

    setSubmitting(true)
    try {
      const logged = await login(loginForm.email, loginForm.senha)
      showToast(`Bem-vindo de volta, ${logged.nome.split(' ')[0]}!`)
      if (from) navigate(from, { replace: true })
    } catch (error) {
      setAuthError(error.message || 'E-mail ou senha inválidos.')
    } finally {
      setSubmitting(false)
    }
  }

  const isStaff = form.tipo === 'funcionario'

  return (
    <div className="container">
      <header className="page-head">
        <span className="eyebrow">Acesso MM GAMES</span>
        <h1>{mode === 'login' ? 'Bem-vindo de volta' : 'Entra pra guilda'}</h1>
        <p className="muted">
          {mode === 'login'
            ? 'Acesse sua conta com e-mail e senha cadastrados.'
            : 'Crie sua conta para salvar seu endereço e finalizar pedidos.'}
        </p>
      </header>

      {from === '/checkout' && (
        <p className="register__notice" role="alert">
          Para finalizar seu pedido, entre na sua conta ou faça seu cadastro rapidinho!
        </p>
      )}

      {/* Alternador Entrar / Criar conta */}
      <div className="auth-tabs" role="tablist" aria-label="Modo de acesso">
        <button
          type="button"
          role="tab"
          aria-selected={mode === 'register'}
          className={`chip ${mode === 'register' ? 'chip--active' : ''}`}
          onClick={() => {
            setMode('register')
            setErrors({})
            setAuthError('')
          }}
        >
          Criar conta
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={mode === 'login'}
          className={`chip ${mode === 'login' ? 'chip--active' : ''}`}
          onClick={() => {
            setMode('login')
            setErrors({})
            setAuthError('')
          }}
        >
          Já tenho conta (Entrar)
        </button>
      </div>

      <div className="register">
        {mode === 'login' ? (
          <form className="register__form" onSubmit={handleLoginSubmit} noValidate>
            <h2 style={{ marginTop: 0, paddingTop: 0, borderTop: 0 }}>Entrar na conta</h2>
            <FormField
              id="login-email"
              name="loginEmail"
              label="E-mail"
              value={loginForm.email}
              onChange={(e) => {
                setLoginForm((f) => ({ ...f, email: e.target.value }))
                setErrors((err) => ({ ...err, loginEmail: undefined }))
                setAuthError('')
              }}
              error={errors.loginEmail}
              inputProps={{ type: 'email', autoComplete: 'email', placeholder: 'seu@email.com' }}
            />
            <FormField
              id="login-senha"
              name="loginSenha"
              label="Senha"
              value={loginForm.senha}
              onChange={(e) => {
                setLoginForm((f) => ({ ...f, senha: e.target.value }))
                setErrors((err) => ({ ...err, loginSenha: undefined }))
                setAuthError('')
              }}
              error={errors.loginSenha}
              inputProps={{ type: 'password', autoComplete: 'current-password' }}
            />

            {authError && (
              <p className="field-error" role="alert">
                {authError}
              </p>
            )}

            <button type="submit" className="btn btn--primary btn--block register__submit" disabled={submitting}>
              {submitting ? 'Entrando…' : 'Entrar'}
            </button>
          </form>
        ) : (
          <form className="register__form" onSubmit={handleRegisterSubmit} noValidate>
            <fieldset className="register__types">
              <legend className="sr-only">Tipo de cadastro</legend>
              {[
                { id: 'cliente', title: 'Sou jogador / cliente', text: 'Quero comprar jogos e acompanhar meus pedidos.' },
                { id: 'funcionario', title: 'Sou da equipe', text: 'Faço parte da equipe MM GAMES.' },
              ].map((opt) => (
                <label key={opt.id} className={`type-option ${form.tipo === opt.id ? 'type-option--active' : ''}`}>
                  <input
                    type="radio"
                    name="tipo"
                    value={opt.id}
                    checked={form.tipo === opt.id}
                    onChange={handleChange}
                  />
                  <strong>{opt.title}</strong>
                  <span>{opt.text}</span>
                </label>
              ))}
            </fieldset>

            <h2>Dados pessoais</h2>
            <FormField
              id="reg-nome"
              name="nome"
              label="Nome completo"
              value={form.nome}
              onChange={handleChange}
              error={errors.nome}
              inputProps={{ autoComplete: 'name' }}
            />
            <div className="register__row">
              <FormField
                id="reg-email"
                name="email"
                label="E-mail"
                value={form.email}
                onChange={handleChange}
                error={errors.email}
                inputProps={{ type: 'email', autoComplete: 'email' }}
              />
              <FormField
                id="reg-telefone"
                name="telefone"
                label="Telefone"
                value={form.telefone}
                onChange={handleChange}
                error={errors.telefone}
                inputProps={{ inputMode: 'numeric', placeholder: '(81) 99999-9999' }}
              />
            </div>
            <FormField
              id="reg-cpf"
              name="cpf"
              label="CPF"
              value={form.cpf}
              onChange={handleChange}
              error={errors.cpf}
              inputProps={{ inputMode: 'numeric', placeholder: '000.000.000-00', maxLength: 14 }}
            />

            {isStaff && (
              <div className="register__staff">
                <h2>Dados da equipe</h2>
                <div className="register__row">
                  <FormField
                    id="reg-matricula"
                    name="matricula"
                    label="Matrícula"
                    value={form.matricula}
                    onChange={handleChange}
                    error={errors.matricula}
                    hint={`${form.matricula.length}/8 caracteres`}
                    inputProps={{ maxLength: 8, placeholder: 'Ex.: MM2026A1' }}
                  />
                  <div className="field">
                    <label htmlFor="reg-cargo">Setor</label>
                    <select id="reg-cargo" name="cargo" className="input" value={form.cargo} onChange={handleChange}>
                      {cargos.map((c) => (
                        <option key={c}>{c}</option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
            )}

            <h2>Endereço</h2>
            <div className="register__row register__row--cep">
              <FormField
                id="reg-cep"
                name="cep"
                label="CEP"
                value={form.cep}
                onChange={handleCep}
                error={errors.cep}
                hint={
                  cepStatus === 'loading'
                    ? 'Buscando endereço…'
                    : cepStatus === 'ok'
                      ? 'Endereço encontrado!'
                      : cepStatus === 'notfound'
                        ? 'CEP não encontrado — preencha manualmente.'
                        : 'Preenchemos o endereço pra você.'
                }
                inputProps={{ inputMode: 'numeric', maxLength: 9, placeholder: 'Só números (8 dígitos)' }}
              />
              <FormField
                id="reg-numero"
                name="numero"
                label="Número"
                value={form.numero}
                onChange={handleChange}
                error={errors.numero}
              />
            </div>
            <FormField
              id="reg-endereco"
              name="endereco"
              label="Rua / Avenida"
              value={form.endereco}
              onChange={handleChange}
              error={errors.endereco}
              inputProps={{ autoComplete: 'address-line1' }}
            />
            <div className="register__row">
              <FormField
                id="reg-complemento"
                name="complemento"
                label="Complemento (opcional)"
                value={form.complemento}
                onChange={handleChange}
              />
              <FormField
                id="reg-bairro"
                name="bairro"
                label="Bairro"
                value={form.bairro}
                onChange={handleChange}
                error={errors.bairro}
              />
            </div>
            <div className="register__row register__row--city">
              <FormField
                id="reg-cidade"
                name="cidade"
                label="Cidade"
                value={form.cidade}
                onChange={handleChange}
                error={errors.cidade}
              />
              <div className="field">
                <label htmlFor="reg-uf">UF</label>
                <select
                  id="reg-uf"
                  name="uf"
                  className={`input ${errors.uf ? 'input--error' : ''}`}
                  value={form.uf}
                  onChange={handleChange}
                >
                  <option value="">--</option>
                  {UFS.map((uf) => (
                    <option key={uf}>{uf}</option>
                  ))}
                </select>
                {errors.uf && <span className="field-error">{errors.uf}</span>}
              </div>
            </div>

            <h2>Senha</h2>
            <div className="register__row">
              <FormField
                id="reg-senha"
                name="senha"
                label="Senha"
                value={form.senha}
                onChange={handleChange}
                error={errors.senha}
                inputProps={{ type: 'password', autoComplete: 'new-password' }}
              />
              <FormField
                id="reg-confirmarSenha"
                name="confirmarSenha"
                label="Confirmar senha"
                value={form.confirmarSenha}
                onChange={handleChange}
                error={errors.confirmarSenha}
                inputProps={{ type: 'password', autoComplete: 'new-password' }}
              />
            </div>

            <label className="register__terms">
              <input type="checkbox" name="termos" checked={form.termos} onChange={handleChange} />
              Li e aceito os termos de uso e a política de privacidade.
            </label>
            {errors.termos && <p className="field-error">{errors.termos}</p>}

            {authError && (
              <p className="field-error" role="alert">
                {authError}
              </p>
            )}

            <button type="submit" className="btn btn--primary btn--block register__submit" disabled={submitting}>
              {submitting ? 'Salvando…' : isStaff ? 'Cadastrar membro da equipe' : 'Criar minha conta'}
            </button>
          </form>
        )}

        <aside className="register__side">
          <Mascot size={120} title="" />
          <h2>{isStaff ? 'Bem-vindo à equipe!' : 'Vantagens da guilda'}</h2>
          {isStaff ? (
            <ul>
              <li>Acesso com sua matrícula</li>
              <li>Desconto de colaborador nos jogos</li>
              <li>Acompanhe os pedidos da loja</li>
            </ul>
          ) : (
            <ul>
              <li>Finalize pedidos em segundos</li>
              <li>Endereço salvo para as próximas compras</li>
              <li>Cupom MENINOMAU10 na primeira compra</li>
            </ul>
          )}
        </aside>
      </div>
    </div>
  )
}

export default Register
