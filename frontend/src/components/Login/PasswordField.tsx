type PasswordFieldProps = {
  id: string;
  name?: string;
  value: string;
  autoComplete?: string;
  visible: boolean;
  toggleLabel: string;
  onChange: (value: string) => void;
  onToggle: () => void;
};

export function PasswordField({
  id,
  name,
  value,
  autoComplete,
  visible,
  toggleLabel,
  onChange,
  onToggle,
}: PasswordFieldProps) {
  return (
    <div className="password-field">
      <input
        id={id}
        name={name}
        type={visible ? 'text' : 'password'}
        required
        inputMode="numeric"
        autoComplete={autoComplete}
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
      <button className="password-toggle" type="button" aria-label={toggleLabel} onClick={onToggle}>
        <span className={visible ? 'eye-icon eye-icon-hidden' : 'eye-icon'} aria-hidden="true" />
      </button>
    </div>
  );
}
