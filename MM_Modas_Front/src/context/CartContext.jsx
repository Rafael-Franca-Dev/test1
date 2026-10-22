// =========================================================
// CartContext — estado global exclusivo do carrinho de compras
// ---------------------------------------------------------
// Desacoplado do ToastContext. Identifica cada item pela
// combinação produto + edição (size) + tema/cor (colorName)
// e calcula frete regional alinhado ao servidor.
// =========================================================
import { createContext, useCallback, useContext, useEffect, useMemo, useReducer, useState } from 'react'
import { calcularFreteLocal, CUPONS } from './cartConfig.js'
import { useToast } from './ToastContext.jsx'
import { useUser } from './UserContext.jsx'
import { apiValidateCoupon } from '../services/api.js'

const CartContext = createContext(null)

const STORAGE_KEY = 'mm-games:carrinho'

// Identifica cada linha por ID + edição + cor/tema
function itemKey({ id, size, colorName }) {
  return `${id}-${size}-${colorName ?? 'padrao'}`
}

function cartReducer(state, action) {
  switch (action.type) {
    case 'ADD': {
      const key = itemKey(action.item)
      const existing = state.find((i) => i.key === key)
      if (existing) {
        return state.map((i) => (i.key === key ? { ...i, qty: i.qty + action.item.qty } : i))
      }
      return [...state, { ...action.item, key }]
    }
    case 'UPDATE_QTY':
      return state
        .map((i) => (i.key === action.key ? { ...i, qty: action.qty } : i))
        .filter((i) => i.qty > 0)
    case 'REMOVE':
      return state.filter((i) => i.key !== action.key)
    case 'CLEAR':
      return []
    default:
      return state
  }
}

function loadCart() {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (!saved) return []
    const parsed = JSON.parse(saved)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function CartProvider({ children }) {
  const [items, dispatch] = useReducer(cartReducer, [], loadCart)
  const [coupon, setCoupon] = useState(null)
  const [shippingUf, setShippingUf] = useState('')
  const { showToast } = useToast()
  const { user } = useUser()

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(items))
    } catch {
      /* armazenamento indisponível */
    }
  }, [items])

  const effectiveUf = shippingUf || user?.uf || 'SP'

  const addItem = useCallback(
    (item) => {
      dispatch({ type: 'ADD', item })
      showToast(`${item.name} foi pro carrinho!`, { to: '/carrinho', label: 'Ver carrinho →' })
    },
    [showToast],
  )

  const updateQty = useCallback((key, qty) => dispatch({ type: 'UPDATE_QTY', key, qty }), [])
  const removeItem = useCallback((key) => dispatch({ type: 'REMOVE', key }), [])
  const clearCart = useCallback(() => {
    dispatch({ type: 'CLEAR' })
    setCoupon(null)
  }, [])

  const applyCoupon = useCallback(async (code) => {
    const normalized = code.trim().toUpperCase()
    if (!normalized) return false

    // Valida primeiro no modelo síncrono/local ou via API
    const validated = await apiValidateCoupon(normalized)
    if (validated) {
      setCoupon(validated)
      return true
    }
    if (CUPONS[normalized]) {
      setCoupon({ code: normalized, rate: CUPONS[normalized], fixedDiscount: 0, freeShipping: false })
      return true
    }
    return false
  }, [])

  const removeCoupon = useCallback(() => setCoupon(null), [])

  const count = useMemo(() => items.reduce((sum, i) => sum + i.qty, 0), [items])
  const subtotal = useMemo(() => items.reduce((sum, i) => sum + i.price * i.qty, 0), [items])

  const discount = useMemo(() => {
    if (!coupon || subtotal <= 0) return 0
    if (coupon.fixedDiscount > 0) return Math.min(subtotal, coupon.fixedDiscount)
    return Number((subtotal * (coupon.rate ?? 0)).toFixed(2))
  }, [coupon, subtotal])

  const shipping = useMemo(() => {
    if (subtotal <= 0 || coupon?.freeShipping) return 0
    return calcularFreteLocal(subtotal, effectiveUf)
  }, [subtotal, coupon, effectiveUf])

  const total = useMemo(() => Math.max(0, subtotal - discount + shipping), [subtotal, discount, shipping])

  const value = useMemo(
    () => ({
      items,
      count,
      subtotal,
      discount,
      shipping,
      total,
      coupon,
      shippingUf: effectiveUf,
      setShippingUf,
      addItem,
      updateQty,
      removeItem,
      clearCart,
      applyCoupon,
      removeCoupon,
    }),
    [
      items,
      count,
      subtotal,
      discount,
      shipping,
      total,
      coupon,
      effectiveUf,
      addItem,
      updateQty,
      removeItem,
      clearCart,
      applyCoupon,
      removeCoupon,
    ],
  )

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart precisa estar dentro de <CartProvider>')
  return ctx
}
