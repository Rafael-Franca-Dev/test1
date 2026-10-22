// =========================================================
// Formatação de valores em Real (R$)
// =========================================================

// Formatador nativo do navegador no padrão brasileiro
const brl = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

// 89.9 -> "R$ 89,90"
export function formatPrice(value) {
  return brl.format(value)
}

// Texto de parcelamento: "ou 3x de R$ 29,97 sem juros"
export function installments(value, max = 3) {
  return `ou ${max}x de ${formatPrice(value / max)} sem juros`
}
