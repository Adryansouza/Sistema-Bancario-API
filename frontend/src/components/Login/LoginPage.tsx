import { FormEvent, useState } from 'react';
import { login } from '../../services/auth/loginService';
import type { ClienteResponse } from '../../services/cliente/clienteTypes';
import type { RequestStatus } from '../../types/statusTypes';
import { CadastroModal } from './CadastroModal';
import { PasswordField } from './PasswordField';
import { isSenhaValida } from './passwordUtils';
import './Login.css';

type LoginPageProps = {
  onLogin: (cliente: ClienteResponse) => void;
};

export function LoginPage({ onLogin }: LoginPageProps) {
  const [documento, setDocumento] = useState('');
  const [senha, setSenha] = useState('');
  const [mostrarSenhaLogin, setMostrarSenhaLogin] = useState(false);
  const [status, setStatus] = useState<RequestStatus>('idle');
  const [message, setMessage] = useState('');
  const [cadastroAberto, setCadastroAberto] = useState(false);

  const senhaLoginValida = isSenhaValida(senha);

  async function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus('loading');
    setMessage('');

    try {
      const cliente = await login({
        documento,
        senha,
      });

      onLogin(cliente);
      setStatus('success');
      setMessage(`Login realizado com sucesso${cliente.nome ? `, ${cliente.nome}` : ''}.`);
    } catch (error) {
      setStatus('error');
      setMessage(error instanceof Error ? error.message : 'Erro inesperado ao realizar login.');
    }
  }

  function handleCadastroSuccess(documentoCadastrado: string, senhaCadastrada: string) {
    setDocumento(documentoCadastrado);
    setSenha(senhaCadastrada);
    setStatus('success');
    setMessage('Conta criada com sucesso. Confira os dados e entre na sua conta.');
    setCadastroAberto(false);
  }

  return (
    <main className="login-page">
      <section className="login-panel" aria-label="Área de login">
        <form className="login-card" onSubmit={handleLogin}>
          <div className="brand" aria-label="Sistema Bancário">
            
            <div>
              <strong>BANCO HORIZONTE</strong>
              <span>seu banco para todos os usos.</span>
            </div>
          </div>

          <div className="field-group">
            <label htmlFor="documento">CPF ou CNPJ</label>
            <input
              id="documento"
              name="documento"
              type="text"
              inputMode="numeric"
              autoComplete="username"
              value={documento}
              onChange={(event) => setDocumento(event.target.value)}
            />
          </div>

          <div className="field-group">
            <label htmlFor="senha">Senha</label>
            <PasswordField
              id="senha"
              name="senha"
              value={senha}
              autoComplete="current-password"
              visible={mostrarSenhaLogin}
              toggleLabel={mostrarSenhaLogin ? 'Esconder senha' : 'Mostrar senha'}
              onChange={setSenha}
              onToggle={() => setMostrarSenhaLogin((mostrar) => !mostrar)}
            />
            <span className={`password-hint ${senhaLoginValida ? 'password-hint-valid' : ''}`}>Apenas 8 números</span>
          </div>

          <button className="login-button" type="submit" disabled={status === 'loading'}>
            {status === 'loading' ? 'Entrando...' : 'Entrar'}
          </button>

          <button className="create-account-link" type="button" onClick={() => setCadastroAberto(true)}>
            Criar conta
          </button>

          {message && (
            <p className={`login-message login-message-${status}`} role="status">
              {message}
            </p>
          )}
        </form>
      </section>

      <section className="hero-panel" aria-label="Imagem institucional">
        
      </section>

      {cadastroAberto && <CadastroModal onClose={() => setCadastroAberto(false)} onCadastroSuccess={handleCadastroSuccess} />}
    </main>
  );
}
