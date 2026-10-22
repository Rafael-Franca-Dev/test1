// =========================================================
// Ícones em SVG (24x24, traço de 2px)
// ---------------------------------------------------------
// Usam stroke="currentColor", então assumem a cor do texto
// do elemento pai. Uso: <BagIcon size={18} />
// =========================================================

// Componente base: define o "quadro" padrão de todos os ícones
function Icon({ children, size = 22, ...props }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      {...props}
    >
      {children}
    </svg>
  )
}

// Pessoa (botão de cadastro / minha conta)
export const UserIcon = (p) => (
  <Icon {...p}>
    <circle cx="12" cy="8" r="4" />
    <path d="M4 21c1.5-4 4.5-6 8-6s6.5 2 8 6" />
  </Icon>
)

// Sacola de compras (carrinho)
export const BagIcon = (p) => (
  <Icon {...p}>
    <path d="M5 8h14l-1 12H6L5 8Z" />
    <path d="M9 8V6a3 3 0 0 1 6 0v2" />
  </Icon>
)

// Menu hambúrguer (abre a navegação no celular)
export const MenuIcon = (p) => (
  <Icon {...p}>
    <path d="M4 7h16M4 12h16M4 17h10" />
  </Icon>
)

// "X" para fechar
export const CloseIcon = (p) => (
  <Icon {...p}>
    <path d="M6 6l12 12M18 6 6 18" />
  </Icon>
)

// Seta para a esquerda (botão "Voltar")
export const BackIcon = (p) => (
  <Icon {...p}>
    <path d="M19 12H5M11 6l-6 6 6 6" />
  </Icon>
)

// Seta para a direita (links "ver mais")
export const ArrowIcon = (p) => (
  <Icon {...p}>
    <path d="M5 12h14M13 6l6 6-6 6" />
  </Icon>
)

// Lupa (busca da loja)
export const SearchIcon = (p) => (
  <Icon {...p}>
    <circle cx="11" cy="11" r="7" />
    <path d="m20 20-3.5-3.5" />
  </Icon>
)

// Caminhão (frete)
export const TruckIcon = (p) => (
  <Icon {...p}>
    <path d="M3 6h11v10H3zM14 10h4l3 3v3h-7" />
    <circle cx="7" cy="18" r="2" />
    <circle cx="17" cy="18" r="2" />
  </Icon>
)

// Setas circulares (trocas)
export const RefreshIcon = (p) => (
  <Icon {...p}>
    <path d="M20 11a8 8 0 0 0-14.9-4M4 13a8 8 0 0 0 14.9 4" />
    <path d="M4 4v4h4M20 20v-4h-4" />
  </Icon>
)

// Folha (produção consciente)
export const LeafIcon = (p) => (
  <Icon {...p}>
    <path d="M5 19c0-8 6-14 15-14 0 9-6 15-14 15" />
    <path d="M5 19 13 11" />
  </Icon>
)

// Lixeira (remover item)
export const TrashIcon = (p) => (
  <Icon {...p}>
    <path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3" />
  </Icon>
)

// Instagram
export const InstagramIcon = (p) => (
  <Icon {...p}>
    <rect x="3" y="3" width="18" height="18" rx="5" />
    <circle cx="12" cy="12" r="4" />
    <circle cx="17.5" cy="6.5" r="0.8" fill="currentColor" />
  </Icon>
)

// TikTok
export const TiktokIcon = (p) => (
  <Icon {...p}>
    <path d="M14 3v11a4 4 0 1 1-4-4" />
    <path d="M14 3c1 3 3 4.5 6 4.5" />
  </Icon>
)

// WhatsApp
export const WhatsIcon = (p) => (
  <Icon {...p}>
    <path d="M4 20l1.3-4A8 8 0 1 1 8 18.7L4 20Z" />
    <path d="M9 9.5c.5 2.5 2.5 4.5 5 5l1-1.5-2-1-1 1c-1-.5-1.5-1-2-2l1-1-1-2L9 9.5Z" />
  </Icon>
)
