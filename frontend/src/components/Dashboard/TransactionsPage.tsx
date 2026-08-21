import { useEffect, useMemo, useState } from 'react';
import { BRASILIA_TIME_ZONE, formatCurrency, parseApiDate } from './dashboardUtils';
import { buscarExtrato } from '../../services/transacao/transacaoService';
import type { StatusTransacao } from '../../services/transacao/transacaoTypes';

type TransactionType = 'entrada' | 'saida';

type Transaction = {
  id: string;
  title: string;
  description: string;
  category: string;
  type: TransactionType;
  value: number;
  date: string;
  counterparty: string;
  document: string | null;
  status: StatusTransacao;
  transactionId: string;
  senderName: string | null;
  senderDocument: string | null;
  recipientName: string | null;
  recipientDocument: string | null;
  pixKey: string | null;
};

export function TransactionsPage({ contaId }: { contaId?: number }) {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loadError, setLoadError] = useState('');
  const [search, setSearch] = useState('');
  const [period, setPeriod] = useState('todos');
  const [category, setCategory] = useState('todas');
  const [type, setType] = useState('todos');
  const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null);

  useEffect(() => {
    if (!contaId) return;
    buscarExtrato(contaId).then((items) => {
      setTransactions(items.map((item) => {
        const entrada = item.tipo === 'DEPOSITO' || item.tipo === 'PIX_RECEBIDO';
        const pix = item.tipo.startsWith('PIX_');
        return {
          id: `${item.idTransacao}-${item.tipo}`,
          title: transactionTitle(item.tipo),
          description: item.descricao,
          category: pix ? 'PIX' : 'Conta',
          type: entrada ? 'entrada' : 'saida',
          value: entrada ? item.valor : -item.valor,
          date: item.dataTransacao,
          counterparty: transactionCounterparty(item.tipo, item.nomeRemetente, item.nomeDestinatario),
          document: item.tipo === 'PIX_RECEBIDO' ? item.documentoRemetente : item.documentoDestinatario,
          status: item.status,
          transactionId: item.idTransacao,
          senderName: item.nomeRemetente,
          senderDocument: item.documentoRemetente,
          recipientName: item.nomeDestinatario,
          recipientDocument: item.documentoDestinatario,
          pixKey: item.chavePixDestino,
        };
      }));
      setLoadError('');
    }).catch((error) => setLoadError(error instanceof Error ? error.message : 'Não foi possível carregar o extrato.'));
  }, [contaId]);

  const filteredTransactions = useMemo(() => {
    const searchValue = search.trim().toLowerCase();

    return transactions.filter((transaction) => {
      const matchesSearch =
        !searchValue ||
        transaction.title.toLowerCase().includes(searchValue) ||
        transaction.description.toLowerCase().includes(searchValue) ||
        transaction.counterparty.toLowerCase().includes(searchValue) ||
        transaction.category.toLowerCase().includes(searchValue);
      const matchesCategory = category === 'todas' || transaction.category === category;
      const matchesType = type === 'todos' || transaction.type === type;
      const matchesPeriod = period === 'todos' || transaction.date.startsWith(period);

      return matchesSearch && matchesCategory && matchesType && matchesPeriod;
    });
  }, [category, period, search, transactions, type]);

  const groupedTransactions = useMemo(() => groupTransactionsByMonth(filteredTransactions), [filteredTransactions]);

  return (
    <section className="transactions-page">
      <div className="breadcrumbs">Home › Transações</div>

      <article className="transactions-card">
        <div className="transactions-heading">
          <h1>Extrato</h1>
          <button className="export-button" type="button" disabled title="Requer endpoint de extrato">
            Exportar
          </button>
        </div>

        <div className="transactions-tabs" aria-label="Tipo de extrato">
          <button className="transactions-tab-active" type="button">
            Últimas transações
          </button>
          <button type="button" disabled title="Requer endpoints de agendamento">Agendamentos</button>
        </div>

        <div className="transactions-filters">
          <label className="search-field" htmlFor="transactionSearch">
            <span>Q</span>
            <input
              id="transactionSearch"
              type="search"
              placeholder="Pesquisar"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
          </label>

          <div className="filter-group">
            <select aria-label="Período" value={period} onChange={(event) => setPeriod(event.target.value)}>
              <option value="todos">Período</option>
              <option value="2026-07">Julho 2026</option>
              <option value="2026-06">Junho 2026</option>
            </select>

            <select aria-label="Categorias" value={category} onChange={(event) => setCategory(event.target.value)}>
              <option value="todas">Categorias</option>
              <option value="PIX">PIX</option>
              <option value="Conta">Conta</option>
            </select>

            <select aria-label="Tipos" value={type} onChange={(event) => setType(event.target.value)}>
              <option value="todos">Tipos</option>
              <option value="entrada">Entradas</option>
              <option value="saida">Saídas</option>
            </select>
          </div>
        </div>

        <div className="transactions-list">
          {loadError && <p className="empty-transactions">{loadError}</p>}
          {!loadError && groupedTransactions.length === 0 && <p className="empty-transactions">Nenhuma transação encontrada.</p>}

          {groupedTransactions.map((group) => (
            <section className="transaction-month" key={group.month}>
              <h2>{group.month}</h2>

              {group.items.map((transaction) => (
                <article
                  className="transaction-row transaction-row-clickable"
                  key={transaction.id}
                  role="button"
                  tabIndex={0}
                  onClick={() => setSelectedTransaction(transaction)}
                  onKeyDown={(event) => {
                    if (event.key === 'Enter' || event.key === ' ') {
                      event.preventDefault();
                      setSelectedTransaction(transaction);
                    }
                  }}
                >
                  <button className="transaction-menu" type="button" aria-label="Mais opções">
                    ...
                  </button>

                  <div className="transaction-info">
                    <strong>{transaction.title}</strong>
                    <span>{transaction.counterparty || transaction.description}</span>
                    <small>
                      {formatTransactionDate(transaction.date)} · {transaction.category} · {formatStatus(transaction.status)}
                    </small>
                    {transaction.document && <small>Documento: {maskDocument(transaction.document)}</small>}
                  </div>

                  <strong className={transaction.type === 'entrada' ? 'transaction-value-in' : 'transaction-value-out'}>
                    {formatCurrency(transaction.value)}
                  </strong>

                  <button
                    className="transaction-actions"
                    type="button"
                    aria-label={`Ver detalhes de ${transaction.title}`}
                    onClick={(event) => {
                      event.stopPropagation();
                      setSelectedTransaction(transaction);
                    }}
                  >⋮</button>
                </article>
              ))}
            </section>
          ))}
        </div>
      </article>

      {selectedTransaction && (
        <TransactionDetail
          transaction={selectedTransaction}
          onClose={() => setSelectedTransaction(null)}
        />
      )}
    </section>
  );
}

function TransactionDetail({ transaction, onClose }: { transaction: Transaction; onClose: () => void }) {
  const entrada = transaction.type === 'entrada';
  const isPix = transaction.category === 'PIX';
  const partyTitle = transaction.type === 'entrada' ? 'Quem enviou' : 'Quem recebeu';
  const partyName = transaction.type === 'entrada' ? transaction.senderName : transaction.recipientName;
  const partyDocument = transaction.type === 'entrada' ? transaction.senderDocument : transaction.recipientDocument;

  return (
    <div className="transaction-modal-backdrop" role="presentation" onMouseDown={onClose}>
      <section
        className="transaction-detail-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="transaction-detail-title"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <header className="transaction-detail-header">
          <button type="button" onClick={onClose} aria-label="Voltar">←</button>
          <h2 id="transaction-detail-title">{transaction.title}</h2>
          <span aria-hidden="true">?</span>
        </header>

        <div className="transaction-detail-value">
          <span className="transaction-detail-direction" aria-hidden="true">{entrada ? '↓' : '↑'}</span>
          <strong className={entrada ? 'transaction-value-in' : 'transaction-value-out'}>
            {formatCurrency(Math.abs(transaction.value))}
          </strong>
          {partyName && <small>{partyName}</small>}
          <div className={`transaction-detail-status transaction-detail-status-${transaction.status.toLowerCase()}`}>
            {formatStatus(transaction.status)}
          </div>
        </div>

        <section className="transaction-detail-section">
          <h3>Sobre a transação</h3>
          <dl className="transaction-detail-list">
            <DetailRow label="Data da transação" value={formatTransactionDay(transaction.date)} />
            <DetailRow label="Horário" value={formatTransactionTime(transaction.date)} />
            <DetailRow label="ID da transação" value={transaction.transactionId} copyable />
            <DetailRow label="Descrição" value={transaction.description} />
          </dl>
        </section>

        {isPix && (
          <section className="transaction-detail-section transaction-detail-party">
            <h3>{partyTitle}</h3>
            {partyName || partyDocument || transaction.pixKey ? (
              <dl className="transaction-detail-list">
                <DetailRow label="Nome" value={partyName} />
                <DetailRow label="CPF/CNPJ" value={partyDocument ? maskDocument(partyDocument) : null} />
                <DetailRow label="Chave PIX" value={transaction.pixKey} copyable />
              </dl>
            ) : (
              <p className="transaction-detail-unavailable">
                Dados indisponíveis para esta transação antiga.
              </p>
            )}
          </section>
        )}

        <button className="transaction-detail-close" type="button" onClick={onClose}>Fechar</button>
      </section>
    </div>
  );
}

function DetailRow({ label, value, copyable = false }: { label: string; value: string | null; copyable?: boolean }) {
  if (!value) return null;
  return (
    <div>
      <dt>{label}</dt>
      <dd>{value}</dd>
      {copyable && (
        <button type="button" onClick={() => navigator.clipboard.writeText(value)} aria-label={`Copiar ${label}`}>
          Copiar
        </button>
      )}
    </div>
  );
}

function transactionTitle(tipo: string) {
  const titles: Record<string, string> = { DEPOSITO: 'Depósito', SAQUE: 'Saque', PIX_ENVIADO: 'Pix Enviado', PIX_RECEBIDO: 'Pix Recebido' };
  return titles[tipo] ?? tipo;
}

function transactionCounterparty(tipo: string, remetente: string | null, destinatario: string | null) {
  if (tipo === 'PIX_RECEBIDO') return remetente ?? '';
  if (tipo === 'PIX_ENVIADO') return destinatario ?? '';
  return '';
}

function formatStatus(status: StatusTransacao) {
  const statuses: Record<StatusTransacao, string> = {
    CONCLUIDA: 'Concluída', PENDENTE: 'Pendente', CANCELADA: 'Cancelada', ESTORNADA: 'Estornada',
  };
  return statuses[status];
}

function maskDocument(document: string) {
  const digits = document.replace(/\D/g, '');
  if (digits.length <= 4) return document;
  return `${'*'.repeat(digits.length - 4)}${digits.slice(-4)}`;
}

function groupTransactionsByMonth(items: Transaction[]) {
  const groups = new Map<string, Transaction[]>();

  items.forEach((item) => {
    const month = new Intl.DateTimeFormat('pt-BR', { month: 'long', timeZone: BRASILIA_TIME_ZONE }).format(parseApiDate(item.date));
    const normalizedMonth = month.charAt(0).toUpperCase() + month.slice(1);
    groups.set(normalizedMonth, [...(groups.get(normalizedMonth) ?? []), item]);
  });

  return Array.from(groups.entries()).map(([month, monthItems]) => ({
    month,
    items: monthItems,
  }));
}

function formatTransactionDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', {
    weekday: 'long',
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    timeZone: BRASILIA_TIME_ZONE,
  }).format(parseApiDate(value));
}

function formatTransactionDay(value: string) {
  return new Intl.DateTimeFormat('pt-BR', {
    weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric', timeZone: BRASILIA_TIME_ZONE,
  }).format(parseApiDate(value));
}

function formatTransactionTime(value: string) {
  return new Intl.DateTimeFormat('pt-BR', {
    hour: '2-digit', minute: '2-digit', timeZone: BRASILIA_TIME_ZONE,
  }).format(parseApiDate(value));
}
