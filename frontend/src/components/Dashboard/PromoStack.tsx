import { FormEvent, useState } from 'react';
import pixIcon from '../../assets/icons/pix.png';
import transferIcon from '../../assets/icons/transfer.png';
import keyIcon from '../../assets/icons/key.png';
import { depositar, sacar } from '../../services/conta/contaService';
import type { RequestStatus } from '../../types/statusTypes';

type Props = { clienteId?: number; onOpenPix: () => void; onOpenKeys: () => void; onBalanceChange: (saldo: number) => void };

export function PromoStack({ clienteId, onOpenPix, onOpenKeys, onBalanceChange }: Props) {
  const [operacao, setOperacao] = useState<'deposito' | 'saque' | null>(null);
  const [valor, setValor] = useState('');
  const [status, setStatus] = useState<RequestStatus>('idle');
  const [message, setMessage] = useState('');

  async function confirmar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const valorNumerico = Number(valor.replace(',', '.'));
    if (!clienteId || !operacao || !Number.isFinite(valorNumerico) || valorNumerico < 1) {
      setStatus('error'); setMessage('Informe um valor válido a partir de R$ 1,00.'); return;
    }
    setStatus('loading'); setMessage('');
    try {
      const resposta = operacao === 'deposito' ? await depositar(clienteId, { valor: valorNumerico }) : await sacar(clienteId, { valor: valorNumerico });
      onBalanceChange(resposta.saldoAtual); setStatus('success'); setMessage(resposta.mensagem); setValor('');
    } catch (error) {
      setStatus('error'); setMessage(error instanceof Error ? error.message : 'Não foi possível concluir a operação.');
    }
  }

  function abrir(tipo: 'deposito' | 'saque') {
    setOperacao(tipo); setValor(''); setStatus('idle'); setMessage('');
  }

  return (
    <aside className="promo-stack" aria-label="Informações da conta">
      <article className="promo-card promo-orange quick-actions-card">
        <span className="promo-label">Ações rápidas</span>
        <h2>PIX · Transferir · Depositar · Sacar · Chave PIX</h2>
        <div className="quick-actions-grid">
          <button type="button" onClick={onOpenPix}><img src={pixIcon} alt="" /><small>PIX</small></button>
          <button type="button" onClick={onOpenPix}><img src={transferIcon} alt="" /><small>Transferir</small></button>
          <button type="button" onClick={() => abrir('deposito')}><b>↓</b><small>Depositar</small></button>
          <button type="button" onClick={() => abrir('saque')}><b>↑</b><small>Sacar</small></button>
          <button type="button" onClick={onOpenKeys}><img src={keyIcon} alt="" /><small>Chave PIX</small></button>
        </div>
      </article>

      {operacao && <div className="quick-operation-backdrop" onMouseDown={(event) => event.target === event.currentTarget && setOperacao(null)}>
        <section className="quick-operation-modal" role="dialog" aria-modal="true">
          <button className="quick-operation-close" type="button" onClick={() => setOperacao(null)}>×</button>
          <h2>{operacao === 'deposito' ? 'Depositar' : 'Sacar'}</h2>
          <form onSubmit={confirmar}>
            <label htmlFor="quick-value">Valor</label>
            <input id="quick-value" autoFocus required type="number" min="1" step="0.01" value={valor} onChange={(event) => setValor(event.target.value)} placeholder="0,00" />
            <button className="pix-continue-button" disabled={status === 'loading'}>{status === 'loading' ? 'Processando...' : 'Confirmar'}</button>
            {message && <p className={`pix-key-message pix-key-message-${status}`}>{message}</p>}
          </form>
        </section>
      </div>}

      <article className="promo-card promo-dark">
          <span>Do seu jeito</span>
          <h2>Sua vida financeira em um só lugar.</h2>
        <p>Tenha praticidade para acompanhar e movimentar sua conta.</p>
      </article>
    </aside>
  );
}
