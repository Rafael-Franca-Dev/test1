// =========================================================
// Cliente HTTP da API MM GAMES (com fallback local transparente)
// ---------------------------------------------------------
// Conecta o Front-end ao Back-end Spring Boot (http://localhost:8080/api).
// Se o servidor ou o banco PostgreSQL não estiverem ativos no momento,
// todas as operações continuam funcionando via fallback local.
// =========================================================
import { products as localProducts, categories as localCategories } from '../data/products.js'
import { calcularFreteLocal, CUPONS, FRETE_GRATIS_A_PARTIR } from '../context/cartConfig.js'
import { onlyDigits } from '../utils/validators.js'

const API_BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'
const TOKEN_KEY = 'mm-games:tokens'
const LOCAL_ACCOUNTS_KEY = 'mm-games:contas-locais'
const LOCAL_ORDERS_KEY = 'mm-games:pedidos-locais'

export function getSavedTokens() {
  try {
    const raw = localStorage.getItem(TOKEN_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function saveTokens(tokens) {
  try {
    if (tokens) localStorage.setItem(TOKEN_KEY, JSON.stringify(tokens))
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    /* ignora se localStorage estiver bloqueado */
  }
}

async function request(path, options = {}, timeoutMs = 2800) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)
  const tokens = getSavedTokens()

  const headers = {
    'Content-Type': 'application/json',
    ...(tokens?.accessToken ? { Authorization: `Bearer ${tokens.accessToken}` } : {}),
    ...options.headers,
  }

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers,
      signal: controller.signal,
    })

    const isJson = response.headers.get('content-type')?.includes('application/json')
    const data = isJson ? await response.json() : null

    if (!response.ok) {
      const error = new Error(data?.mensagem || `Erro HTTP ${response.status}`)
      error.status = response.status
      error.code = data?.codigo
      error.details = data?.detalhes
      error.isHttpError = true
      throw error
    }

    return data
  } finally {
    clearTimeout(timer)
  }
}

// ---------- Contas locais (Fallback quando a API está offline) ----------
function loadLocalAccounts() {
  try {
    const saved = localStorage.getItem(LOCAL_ACCOUNTS_KEY)
    const parsed = saved ? JSON.parse(saved) : []
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function saveLocalAccounts(accounts) {
  try {
    localStorage.setItem(LOCAL_ACCOUNTS_KEY, JSON.stringify(accounts))
  } catch {
    /* sem armazenamento */
  }
}

function normalizeBackendUser(usuario, extra = {}) {
  const end = usuario.enderecoPrincipal
  return {
    id: usuario.id,
    tipo: extra.tipo ?? (usuario.perfil === 'ADMIN' ? 'funcionario' : 'cliente'),
    nome: usuario.nome,
    email: usuario.email,
    cpf: usuario.cpf ?? extra.cpf ?? '',
    telefone: usuario.telefone ?? extra.telefone ?? '',
    matricula: extra.matricula,
    cargo: extra.cargo,
    enderecoId: end?.id ?? null,
    cep: end?.cep ?? extra.cep ?? '',
    endereco: end?.logradouro ?? extra.endereco ?? '',
    numero: end?.numero ?? extra.numero ?? '',
    complemento: end?.complemento ?? extra.complemento ?? '',
    bairro: end?.bairro ?? extra.bairro ?? '',
    cidade: end?.cidade ?? extra.cidade ?? '',
    uf: end?.uf ?? extra.uf ?? '',
    criadoEm: usuario.criadoEm ?? new Date().toISOString(),
    origem: 'api',
  }
}

// ---------- Autenticação ----------
export async function apiRegister(formData) {
  const cpfDigits = onlyDigits(formData.cpf || '')
  const cepDigits = onlyDigits(formData.cep || '')

  const payload = {
    nome: formData.nome.trim(),
    email: formData.email.trim().toLowerCase(),
    senha: formData.senha,
    cpf: cpfDigits.length === 11 ? cpfDigits : null,
    telefone: formData.telefone,
    endereco: {
      apelido: 'Principal',
      destinatario: formData.nome.trim(),
      cep: cepDigits,
      logradouro: formData.endereco.trim(),
      numero: formData.numero.trim(),
      complemento: formData.complemento?.trim() || null,
      bairro: formData.bairro.trim(),
      cidade: formData.cidade.trim(),
      uf: formData.uf.trim().toUpperCase(),
      principal: true,
    },
  }

  try {
    const res = await request('/auth/registrar', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
    saveTokens({ accessToken: res.accessToken, refreshToken: res.refreshToken })
    return normalizeBackendUser(res.usuario, formData)
  } catch (err) {
    if (err.isHttpError) throw err

    // Fallback local se o servidor Spring Boot estiver desligado
    const accounts = loadLocalAccounts()
    const emailLower = formData.email.trim().toLowerCase()
    if (accounts.some((a) => a.email.toLowerCase() === emailLower)) {
      throw new Error('Já existe uma conta cadastrada com este e-mail.')
    }

    const { confirmarSenha: _confirmarSenha, ...record } = formData
    const newAccount = {
      ...record,
      id: crypto.randomUUID ? crypto.randomUUID() : String(Date.now()),
      email: emailLower,
      criadoEm: new Date().toISOString(),
      origem: 'local',
    }
    saveLocalAccounts([...accounts, newAccount])
    const { senha: _senha, ...safeUser } = newAccount
    return safeUser
  }
}

export async function apiLogin(email, senha) {
  const emailLower = email.trim().toLowerCase()
  try {
    const res = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email: emailLower, senha }),
    })
    saveTokens({ accessToken: res.accessToken, refreshToken: res.refreshToken })
    return normalizeBackendUser(res.usuario)
  } catch (err) {
    if (err.isHttpError) throw err

    // Fallback local
    const accounts = loadLocalAccounts()
    const found = accounts.find((a) => a.email.toLowerCase() === emailLower && a.senha === senha)
    if (!found) {
      throw new Error('E-mail ou senha inválidos.')
    }
    const { senha: _senha, ...safeUser } = found
    return safeUser
  }
}

// ---------- Frete e Cupom ----------
export async function apiSimulateShipping(subtotal, uf) {
  if (!subtotal || subtotal <= 0) {
    return { valorFrete: 0, limiteFreteGratis: FRETE_GRATIS_A_PARTIR, freteGratis: true }
  }
  if (!uf || uf.trim().length !== 2) {
    const valor = calcularFreteLocal(subtotal, uf)
    return { valorFrete: valor, limiteFreteGratis: FRETE_GRATIS_A_PARTIR, freteGratis: valor === 0 }
  }

  try {
    return await request('/frete/simular', {
      method: 'POST',
      body: JSON.stringify({ subtotal, uf: uf.trim().toUpperCase() }),
    })
  } catch {
    const valor = calcularFreteLocal(subtotal, uf)
    return { valorFrete: valor, limiteFreteGratis: FRETE_GRATIS_A_PARTIR, freteGratis: valor === 0 }
  }
}

export async function apiValidateCoupon(code) {
  const normalized = code.trim().toUpperCase()
  if (!normalized) return null

  try {
    const data = await request(`/cupons/validar/${encodeURIComponent(normalized)}`, { method: 'GET' })
    const rate = data.tipo === 'PERCENTUAL' ? Number(data.valor) / 100 : 0
    return {
      code: data.codigo,
      rate,
      fixedDiscount: data.tipo === 'VALOR_FIXO' ? Number(data.valor) : 0,
      freeShipping: Boolean(data.freteGratis),
    }
  } catch (err) {
    if (err.isHttpError) return null
    if (CUPONS[normalized]) {
      return { code: normalized, rate: CUPONS[normalized], fixedDiscount: 0, freeShipping: false }
    }
    return null
  }
}

// ---------- Catálogo ----------
export async function apiFetchCatalog() {
  try {
    const [apiCats, apiProds] = await Promise.all([
      request('/categorias', { method: 'GET' }),
      request('/produtos', { method: 'GET' }),
    ])

    if (!Array.isArray(apiProds) || apiProds.length === 0) {
      return { categories: localCategories, products: localProducts, source: 'local' }
    }

    // Mescla dados de estoque/preço vindos do banco com o catálogo visual do front
    const mergedProducts = localProducts.map((lp) => {
      const match = apiProds.find((ap) => ap.slug === lp.slug)
      if (!match) return lp
      const firstVar = match.variacoes?.[0]
      return {
        ...lp,
        backendId: match.id,
        variacaoId: firstVar?.id ?? lp.variacaoId,
        price: Number(match.precoEfetivo ?? lp.price),
        oldPrice: match.precoPromocional ? Number(match.precoBase) : lp.oldPrice,
        image: match.imagemPrincipal || lp.image,
      }
    })

    return { categories: localCategories, products: mergedProducts, source: 'api', apiCategories: apiCats }
  } catch {
    return { categories: localCategories, products: localProducts, source: 'local' }
  }
}

// ---------- Pedidos (Checkout) ----------
export async function apiCreateOrder({ user, items, couponCode, delivery, idempotencyKey }) {
  const tokens = getSavedTokens()
  const canCallApi = Boolean(tokens?.accessToken && user?.origem === 'api')

  if (canCallApi) {
    try {
      let enderecoId = user.enderecoId
      if (!enderecoId) {
        const endRes = await request('/enderecos', {
          method: 'POST',
          body: JSON.stringify({
            apelido: 'Entrega',
            destinatario: delivery.nome.trim(),
            cep: onlyDigits(delivery.cep),
            logradouro: delivery.endereco.trim(),
            numero: delivery.numero.trim(),
            complemento: delivery.complemento?.trim() || null,
            bairro: delivery.bairro.trim(),
            cidade: delivery.cidade.trim(),
            uf: delivery.uf.trim().toUpperCase(),
            principal: true,
          }),
        })
        enderecoId = endRes.id
      }

      const itensApi = items
        .filter((i) => Boolean(i.variacaoId))
        .map((i) => ({ variacaoId: i.variacaoId, quantidade: i.qty }))

      if (itensApi.length === items.length) {
        const pedido = await request('/pedidos', {
          method: 'POST',
          body: JSON.stringify({
            enderecoId,
            itens: itensApi,
            cupomCodigo: couponCode || null,
            idempotencyKey,
            observacoes: `Pagamento escolhido: ${delivery.pagamento}`,
          }),
        })
        return { numero: pedido.numero, id: pedido.id, origem: 'api' }
      }
    } catch (err) {
      if (err.isHttpError) throw err
    }
  }

  // Fallback local: grava pedido no localStorage respeitando idempotência
  try {
    const saved = JSON.parse(localStorage.getItem(LOCAL_ORDERS_KEY) || '[]')
    const existing = saved.find((o) => o.idempotencyKey === idempotencyKey)
    if (existing) return existing

    const novoPedido = {
      id: crypto.randomUUID ? crypto.randomUUID() : String(Date.now()),
      numero: `MM-${Math.floor(100000 + Math.random() * 900000)}`,
      idempotencyKey,
      criadoEm: new Date().toISOString(),
      origem: 'local',
    }
    localStorage.setItem(LOCAL_ORDERS_KEY, JSON.stringify([novoPedido, ...saved]))
    return novoPedido
  } catch {
    return {
      numero: `MM-${Math.floor(100000 + Math.random() * 900000)}`,
      origem: 'local',
    }
  }
}

