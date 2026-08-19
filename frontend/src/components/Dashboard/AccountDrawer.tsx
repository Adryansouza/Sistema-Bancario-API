import { FormEvent, useState } from 'react';
import type { ClienteResponse } from '../../services/cliente/clienteTypes';
import { atualizarCliente } from '../../services/cliente/clienteService';
import type { RequestStatus } from '../../types/statusTypes';
import { getInitials } from './dashboardUtils';

type Props = {
  cliente: ClienteResponse;
  onClose: () => void;
  onUpdate: (cliente: ClienteResponse) => void;
  onLogout: () => void;
};

export function AccountDrawer({ cliente, onClose, onUpdate, onLogout }: Props) {
  const documento = cliente.documento ?? cliente.cpf ?? cliente.cnpj ?? 'Não informado';
  const [editando, setEditando] = useState(false);
  const [nome, setNome] = useState(cliente.nome ?? '');
  const [telefone, setTelefone] = useState(cliente.telefone ?? '');
  const [endereco, setEndereco] = useState(cliente.endereco ?? '');
  const [uf, setUf] = useState(cliente.uf ?? '');
  const [senha, setSenha] = useState('');
  const [status, setStatus] = useState<RequestStatus>('idle');
  const [message, setMessage] = useState('');

  async function salvar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!cliente.id) return;
    if (senha && !/^\d{8}$/.test(senha)) {
      setStatus('error');
      setMessage('A nova senha deve conter exatamente 8 números.');
      return;
    }
    setStatus('loading');
    setMessage('');
    try {
      const atualizado = await atualizarCliente(cliente.id, cliente.tipoCliente, {
        nome, telefone, endereco, uf, ...(senha ? { senha } : {}),
      });
      onUpdate(atualizado);
      setSenha('');
      setStatus('success');
      setMessage('Cadastro atualizado com sucesso.');
      setEditando(false);
    } catch (error) {
      setStatus('error');
      setMessage(error instanceof Error ? error.message : 'Não foi possível atualizar o cadastro.');
    }
  }

  return (
    <div className="account-drawer-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <aside className="account-drawer" role="dialog" aria-modal="true" aria-labelledby="account-drawer-title">
        <button className="account-drawer-close" type="button" aria-label="Fechar dados da conta" onClick={onClose}>×</button>

        <header className="account-drawer-header">
          <span className="account-drawer-avatar" aria-hidden="true">{getInitials(cliente.nome)}</span>
          <div>
            <h2 id="account-drawer-title">{cliente.nome ?? 'Cliente'}</h2>
            <p>Conta: {cliente.conta?.numeroConta ?? 'Não informada'}</p>
            <p>Agência: {cliente.conta?.agencia ?? 'Não informada'}</p>
          </div>
        </header>

        {editando ? (
          <section className="account-drawer-edit" aria-labelledby="edit-data-title">
            <div className="account-drawer-section-title">
              <h3 id="edit-data-title">Alterar cadastro</h3>
              <button type="button" onClick={() => { setEditando(false); setMessage(''); }}>Cancelar</button>
            </div>
            <form onSubmit={salvar}>
              <label>Documento<input value={documento} disabled /></label>
              <label>Nome<input required value={nome} onChange={(event) => setNome(event.target.value)} /></label>
              <label>Telefone<input required value={telefone} onChange={(event) => setTelefone(event.target.value)} /></label>
              <label>Endereço<input required value={endereco} onChange={(event) => setEndereco(event.target.value)} /></label>
              <label>UF<input required maxLength={2} value={uf} onChange={(event) => setUf(event.target.value.toUpperCase())} /></label>
              <label>Nova senha (opcional)<input type="password" inputMode="numeric" value={senha} onChange={(event) => setSenha(event.target.value)} placeholder="8 números" /></label>
              <button className="pix-continue-button" disabled={status === 'loading'}>{status === 'loading' ? 'Salvando...' : 'Salvar alterações'}</button>
              {message && <p className={`pix-key-message pix-key-message-${status}`} role="status">{message}</p>}
            </form>
          </section>
        ) : (<>
        <section className="account-drawer-details" aria-labelledby="personal-data-title">
          <h3 id="personal-data-title">Dados pessoais</h3>
          <dl>
            <div><dt>Documento</dt><dd>{documento}</dd></div>
            <div><dt>Telefone</dt><dd>{cliente.telefone ?? 'Não informado'}</dd></div>
            <div><dt>Endereço</dt><dd>{cliente.endereco ?? 'Não informado'}</dd></div>
            <div><dt>UF</dt><dd>{cliente.uf ?? 'Não informada'}</dd></div>
          </dl>
        </section>

        <section className="account-drawer-actions" aria-labelledby="account-actions-title">
          <h3 id="account-actions-title">Conta</h3>
          <button type="button" onClick={() => setEditando(true)}><span aria-hidden="true">✎</span><strong>Alterar cadastro</strong><small>Atualize seus dados e sua senha</small></button>
        </section>

        <section className="account-drawer-actions">
          <h3>Mais ações</h3>
          <button type="button" onClick={onLogout}><span aria-hidden="true">↪</span><strong>Sair</strong><small>Encerrar esta sessão</small></button>
        </section>
        </>)}
      </aside>
    </div>
  );
}
