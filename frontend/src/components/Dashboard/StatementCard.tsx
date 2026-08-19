import { useEffect, useState } from 'react';
import type { ClienteResponse } from '../../services/cliente/clienteTypes';
import { listarTransacoes } from '../../services/transacao/transacaoService';
import type { TransacaoResponse } from '../../services/transacao/transacaoTypes';
import { formatCurrency } from './dashboardUtils';

type StatementCardProps = {
  cliente: ClienteResponse;
  saldo: number;
  contaId?: number;
};

export function StatementCard({ cliente, saldo, contaId }: StatementCardProps) {
  const [transacoes, setTransacoes] = useState<TransacaoResponse[]>([]);

  useEffect(() => {
    if (!contaId) return;
    listarTransacoes(contaId).then(setTransacoes).catch(() => setTransacoes([]));
  }, [contaId]);

  const transferencias = transacoes.filter((item) => item.tipo.startsWith('PIX_')).slice(0, 4);
  const mes = new Intl.DateTimeFormat('pt-BR', { month: 'long' }).format(new Date());
  return (
    <article className="statement-card">
      <div className="card-title-row">
        <h1>Extrato</h1>
        <button className="arrow-button" type="button" aria-label="Extrato indisponível" disabled title="Requer endpoint de extrato">
          ›
        </button>
      </div>

      <div className="balance-row">
        <div>
          <span>Saldo em conta</span>
          <strong>{formatCurrency(saldo)}</strong>
        </div>
        <div>
          <span>Status</span>
          <strong>{cliente.conta?.status ?? 'Conta ativa'}</strong>
        </div>
      </div>

      <div className="pill-row">
        <button type="button" disabled title="Requer endpoint de extrato">Entradas</button>
        <button type="button" disabled title="Requer endpoint de extrato">Saídas</button>
      </div>

      <div className="account-details">
        <h2>Dados da conta</h2>
        <p>Agência: {cliente.conta?.agencia ?? 'Não informado'}</p>
        <p>Conta: {cliente.conta?.numeroConta ?? contaId ?? 'Não informado'}</p>
        <p>Cliente: {cliente.nome}</p>
      </div>

      <section className="statement-history" aria-labelledby="statement-history-title">
        <h2 id="statement-history-title">Histórico de transferências</h2>
        <div className="statement-month">{mes.charAt(0).toUpperCase() + mes.slice(1)}</div>
        {transferencias.length === 0 ? (
          <div className="statement-empty-row"><span className="statement-empty-icon">↔</span><div><strong>Nenhuma transferência neste período</strong><small>Suas movimentações aparecerão aqui.</small></div></div>
        ) : transferencias.map((item) => (
          <div className="statement-transaction-row" key={item.id}>
            <span className="statement-transaction-menu">•••</span>
            <div><strong>{item.tipo === 'PIX_ENVIADO' ? 'PIX enviado' : 'PIX recebido'}</strong><small>{item.descricao}</small><small>{new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(item.dataTransacao))}</small></div>
            <strong className={item.tipo === 'PIX_ENVIADO' ? 'transaction-value-out' : 'transaction-value-in'}>{item.tipo === 'PIX_ENVIADO' ? '-' : '+'}{formatCurrency(item.valor)}</strong>
          </div>
        ))}
      </section>
    </article>
  );
}
