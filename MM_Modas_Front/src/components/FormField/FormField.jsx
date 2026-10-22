// =========================================================
// FormField — componente reutilizável de campo de formulário
// ---------------------------------------------------------
// Elimina a duplicação da função auxiliar field() que antes
// era recriada dentro de Register.jsx e Checkout.jsx.
// =========================================================
function FormField({
  id,
  name,
  label,
  value,
  onChange,
  error,
  hint,
  wrapperClass = '',
  inputProps = {},
}) {
  const inputId = id ?? `field-${name}`

  return (
    <div className={`field ${wrapperClass}`.trim()}>
      <label htmlFor={inputId}>{label}</label>
      <input
        id={inputId}
        name={name}
        className={`input ${error ? 'input--error' : ''}`}
        value={value}
        onChange={onChange}
        aria-invalid={Boolean(error)}
        {...inputProps}
      />
      {hint && !error && <span className="field-hint">{hint}</span>}
      {error && <span className="field-error">{error}</span>}
    </div>
  )
}

export default FormField

