import { FormEvent, useEffect, useState } from 'react';
import pixIcon from '../../assets/icons/pix.png';
import keyIcon from '../../assets/icons/key.png';
import transferIcon from '../../assets/icons/transfer.png';
import { BRASILIA_TIME_ZONE, formatCurrency, parseApiDate } from './dashboardUtils';
import { transferirPix } from '../../services/pix/pixService';
import type { TipoChavePix } from '../../services/chavePix/chavePixTypes';
import type { RequestStatus } from '../../types/statusTypes';
import { buscarExtrato } from '../../services/transacao/transacaoService';
import type { ExtratoResponse } from '../../services/transacao/transacaoTypes';

type PixPageProps = {
  saldo: number;
  contaId?: number;
  onBalanceChange: (saldo: number) => void;
  onOpenKeys: () => void;
};

export function PixPage({ saldo, contaId, onBalanceChange, onOpenKeys }: PixPageProps) {
  const [view, setView] = useState<'home' | 'send'>('home');
  const [chave, setChave] = useState('');
  const [tipoChave, setTipoChave] = useState<TipoChavePix>('CPF');
  const [valor, setValor] = useState('');
  const [senha, setSenha] = useState('');
  const [status, setStatus] = useState<RequestStatus>('idle');
  const [message, setMessage] = useState('');
  const [recentes, setRecentes] = useState<ExtratoResponse[]>([]);
  const [recentesStatus, setRecentesStatus] = useState<RequestStatus>('idle');
  const [recentesErro, setRecentesErro] = useState('');

  useEffect(() => {
    if (!contaId) return;
    carregarRecentes(contaId, setRecentes, setRecentesStatus, setRecentesErro);
  }, [contaId]);

  async function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const valorNumerico = Number(valor.replace(',', '.'));
    if (!contaId || !chave.trim() || !Number.isFinite(valorNumerico) || valorNumerico < 1 || !/^\d{8}$/.test(senha)) {
      setStatus('error');
      setMessage('Preencha a chave, um valor válido e sua senha de 8 números.');
      return;
    }
    setStatus('loading'); setMessage('');
    try {
      const response = await transferirPix({ contaId, chavePixDestino: chave.trim(), tipoChavePix: tipoChave, valor: valorNumerico, senha });
      onBalanceChange(response.saldoAtual);
      carregarRecentes(contaId, setRecentes, setRecentesStatus, setRecentesErro);
      setChave(''); setValor(''); setSenha(''); setStatus('success'); setMessage(response.mensagem);
    } catch (error) {
      setStatus('error'); setMessage(error instanceof Error ? error.message : 'Não foi possível enviar o PIX.');
    }
  }

  if (view === 'send') {
    return (
      <div className="pix-page pix-send-page">
        <button className="pix-back-button" type="button" onClick={() => setView('home')}>
          &lt; Voltar para PIX
        </button>

        <header className="pix-hero pix-send-hero">
          <h1>Enviar PIX</h1>
          <p>Para quem você quer enviar?</p>
        </header>

        <form className="pix-send-form" onSubmit={enviar}>
          <label htmlFor="pix-key-type">Tipo da chave</label>
          <select id="pix-key-type" value={tipoChave} onChange={(e) => setTipoChave(e.target.value as TipoChavePix)}>
            <option value="CPF">CPF</option><option value="EMAIL">E-mail</option><option value="TELEFONE">Telefone</option>
          </select>
          <div className="pix-send-label-row">
            <label htmlFor="pix-key">Chave PIX</label>
            <button type="button" onClick={async () => { try { setChave(await navigator.clipboard.readText()); } catch { setMessage('Não foi possível acessar a área de transferência.'); } }}>Colar chave</button>
          </div>
          <input id="pix-key" required value={chave} onChange={(e) => setChave(e.target.value)} type="text" placeholder="CPF, telefone ou e-mail" />
          <span>Digite uma chave PIX cadastrada para localizar o destinatário.</span>
          <label htmlFor="pix-value">Valor</label>
          <input id="pix-value" required type="number" min="1" step="0.01" value={valor} onChange={(e) => setValor(e.target.value)} placeholder="0,00" />
          <label htmlFor="pix-password">Senha</label>
          <input id="pix-password" required type="password" inputMode="numeric" value={senha} onChange={(e) => setSenha(e.target.value)} placeholder="8 números" />
          <button className="pix-continue-button" disabled={status === 'loading'}>{status === 'loading' ? 'Enviando...' : 'Enviar PIX'}</button>
          {message && <p className={`pix-key-message pix-key-message-${status}`} role="status">{message}</p>}
        </form>
      </div>
    );
  }

  return (
    <div className="pix-page">
      <header className="pix-hero">
        <h1>PIX</h1>
        <p>Envie, receba ou pague usando PIX.</p>
        <strong>Saldo disponível: {formatCurrency(saldo)}</strong>
      </header>

      <section className="pix-primary-actions" aria-label="Ações principais do PIX">
        <button className="pix-action-card pix-action-card-active" type="button" onClick={() => setView('send')}>
          <img src={transferIcon} alt="" aria-hidden="true" />
          <span>
            <strong>Enviar PIX</strong>
            <small>Transfira usando CPF, CNPJ, telefone, e-mail ou chave aleatória.</small>
          </span>
          <b aria-hidden="true">&gt;</b>
        </button>

        <button className="pix-action-card" type="button" disabled title="Requer endpoint para consultar dados de recebimento">
          <img src={pixIcon} alt="" aria-hidden="true" />
          <span>
            <strong>Receber PIX</strong>
            <small>Gere os dados necessários para receber um pagamento.</small>
          </span>
          <b aria-hidden="true">&gt;</b>
        </button>
      </section>

      <section className="pix-options" aria-labelledby="pix-options-title">
        <h2 id="pix-options-title">Outras opções</h2>
        <div className="pix-option-grid">
          <button type="button" disabled title="Requer endpoint de QR Code">
            <span className="pix-option-icon pix-option-qr">QR</span>
            <strong>QR Code</strong>
          </button>
          <button type="button" disabled title="Requer endpoint PIX Copia e Cola">
            <span className="pix-option-icon">
              <img src={transferIcon} alt="" aria-hidden="true" />
            </span>
            <strong>PIX Copia e Cola</strong>
          </button>
          <button type="button" onClick={onOpenKeys}>
            <span className="pix-option-icon">
              <img src={keyIcon} alt="" aria-hidden="true" />
            </span>
            <strong>Minhas chaves</strong>
          </button>
        </div>
      </section>

      <section className="pix-recents">
        <h2>Recentes</h2>
        <div className="pix-recents-card" aria-live="polite">
          {recentesStatus === 'loading' && <p className="pix-recents-message">Carregando movimentações...</p>}
          {recentesStatus === 'error' && <p className="pix-recents-message pix-recents-error">{recentesErro}</p>}
          {recentesStatus !== 'loading' && recentesStatus !== 'error' && recentes.length === 0 && (
            <p className="pix-recents-message">Nenhuma movimentação PIX encontrada.</p>
          )}
          {recentes.map((item) => {
            const entrada = item.tipo === 'PIX_RECEBIDO';
            const pessoa = entrada ? item.nomeRemetente : item.nomeDestinatario;
            return (
              <article className="pix-recent-item" key={`${item.idTransacao}-${item.tipo}`}>
                <span className="pix-recent-avatar" aria-hidden="true">{entrada ? '↓' : '↑'}</span>
                <div className="pix-recent-info">
                  <strong>{pessoa ?? (entrada ? 'PIX recebido' : 'PIX enviado')}</strong>
                  <small>{formatPixDate(item.dataTransacao)} · {entrada ? 'Recebido' : 'Enviado'}</small>
                </div>
                <strong className={entrada ? 'transaction-value-in' : 'transaction-value-out'}>
                  {entrada ? '+' : '-'}{formatCurrency(item.valor)}
                </strong>
              </article>
            );
          })}
        </div>
      </section>
    </div>
  );
}

function carregarRecentes(
  contaId: number,
  setRecentes: (items: ExtratoResponse[]) => void,
  setStatus: (status: RequestStatus) => void,
  setErro: (message: string) => void,
) {
  setStatus('loading');
  setErro('');
  buscarExtrato(contaId)
    .then((items) => {
      setRecentes(items.filter((item) => item.tipo.startsWith('PIX_')).slice(0, 5));
      setStatus('success');
    })
    .catch((error) => {
      setRecentes([]);
      setErro(error instanceof Error ? error.message : 'Não foi possível carregar as movimentações.');
      setStatus('error');
    });
}

function formatPixDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit', timeZone: BRASILIA_TIME_ZONE,
  }).format(parseApiDate(value));
}
