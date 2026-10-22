// =========================================================
// Regras de negócio do carrinho (alinhadas ao FreteService do Back-end)
// =========================================================

export const FRETE_GRATIS_A_PARTIR = 299

export const VALOR_FRETE_BASE = 24.9

export const ADICIONAL_POR_UF = {
  SP: 0.0,
  RJ: 5.0,
  MG: 5.0,
  ES: 7.0,
  PR: 7.0,
  SC: 9.0,
  RS: 9.0,
}

export const ADICIONAL_PADRAO_UF = 15.0

export function calcularFreteLocal(subtotal, uf) {
  if (!subtotal || subtotal <= 0 || subtotal >= FRETE_GRATIS_A_PARTIR) return 0
  if (!uf) return VALOR_FRETE_BASE
  const sigla = uf.trim().toUpperCase()
  const adicional = ADICIONAL_POR_UF[sigla] ?? ADICIONAL_PADRAO_UF
  return Number((VALOR_FRETE_BASE + adicional).toFixed(2))
}

// Cupons válidos no fallback local: código -> percentual de desconto (0.1 = 10%)
export const CUPONS = { MENINOMAU10: 0.1 }
