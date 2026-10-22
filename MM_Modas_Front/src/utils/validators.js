// =========================================================
// Máscaras e validações usadas nos formulários
// (cadastro, checkout e contato)
// =========================================================

// Remove tudo que não for número: "123.456-7" -> "1234567"
export const onlyDigits = (value) => value.replace(/\D/g, '')

// CEP: aceita só números e no máximo 8 dígitos
export const maskCep = (value) => onlyDigits(value).slice(0, 8)

// CPF: 000.000.000-00 (máximo 11 dígitos)
export function maskCpf(value) {
  const d = onlyDigits(value).slice(0, 11)
  return d
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// Telefone: (00) 00000-0000
export function maskPhone(value) {
  const d = onlyDigits(value).slice(0, 11)
  if (d.length <= 2) return d.length ? `(${d}` : ''
  if (d.length <= 7) return `(${d.slice(0, 2)}) ${d.slice(2)}`
  return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`
}

// Matrícula de funcionário: letras e números, maiúsculas, até 8 caracteres
export const maskMatricula = (value) =>
  value
    .toUpperCase()
    .replace(/[^A-Z0-9]/g, '')
    .slice(0, 8)

// ---------- Validações ----------

export const isEmail = (value) => /^\S+@\S+\.\S+$/.test(value)

// CEP válido = exatamente 8 dígitos
export const isCep = (value) => /^\d{8}$/.test(value)

// Matrícula válida = exatamente 8 caracteres alfanuméricos
export const isMatricula = (value) => /^[A-Z0-9]{8}$/.test(value)

// Telefone com DDD: 10 ou 11 dígitos
export const isPhone = (value) => [10, 11].includes(onlyDigits(value).length)

// Valida o CPF pelos dígitos verificadores (algoritmo oficial da Receita)
export function isCpf(value) {
  const cpf = onlyDigits(value)
  // Precisa ter 11 dígitos e não pode ser tudo igual (111.111.111-11)
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false

  // Calcula um dígito verificador a partir dos "n" primeiros dígitos
  const calcDigit = (n) => {
    let sum = 0
    for (let i = 0; i < n; i++) sum += Number(cpf[i]) * (n + 1 - i)
    const rest = (sum * 10) % 11
    return rest === 10 ? 0 : rest
  }

  return calcDigit(9) === Number(cpf[9]) && calcDigit(10) === Number(cpf[10])
}

// Siglas dos estados brasileiros (select de UF)
export const UFS = [
  'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA',
  'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO',
]

// Busca o endereço pelo CEP na API pública ViaCEP.
// Retorna null se o CEP não existir ou se não houver internet.
export async function fetchAddressByCep(cep) {
  try {
    const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`)
    const data = await res.json()
    if (data.erro) return null
    return { endereco: data.logradouro, bairro: data.bairro, cidade: data.localidade, uf: data.uf }
  } catch {
    return null
  }
}
