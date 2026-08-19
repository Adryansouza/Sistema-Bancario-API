export function isSenhaValida(value: string) {
  return /^\d{8}$/.test(value);
}
