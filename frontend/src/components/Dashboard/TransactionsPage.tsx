import { useEffect, useMemo, useState } from 'react';
import { formatCurrency } from './dashboardUtils';
import { listarTransacoes } from '../../services/transacao/transacaoService';

type TransactionType = 'entrada' | 'saida';

type Transaction = {
  id: number;
  title: string;
  description: string;
  category: string;
  type: TransactionType;
  value: number;
  date: string;
};

export function TransactionsPage({ contaId }: { contaId?: number }) {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loadError, setLoadError] = useState('');
  const [search, setSearch] = useState('');
  const [period, setPeriod] = useState('todos');
  const [category, setCategory] = useState('todas');
  const [type, setType] = useState('todos');

  useEffect(() => {
    if (!contaId) return;
    listarTransacoes(contaId).then((items) => {
      setTransactions(items.map((item) => {
        const entrada = item.tipo === 'DEPOSITO' || item.tipo === 'PIX_RECEBIDO';
        const pix = item.tipo.startsWith('PIX_');
        return {
          id: item.id,
          title: transactionTitle(item.tipo),
          description: item.descricao,
          category: pix ? 'PIX' : 'Conta',
          type: entrada ? 'entrada' : 'saida',
          value: entrada ? item.valor : -item.valor,
          date: item.dataTransacao,
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
                <article className="transaction-row" key={transaction.id}>
                  <button className="transaction-menu" type="button" aria-label="Mais opções">
                    ...
                  </button>

                  <div className="transaction-info">
                    <strong>{transaction.title}</strong>
                    <span>{transaction.description}</span>
                    <small>
                      {formatTransactionDate(transaction.date)} · {transaction.category}
                    </small>
                  </div>

                  <strong className={transaction.type === 'entrada' ? 'transaction-value-in' : 'transaction-value-out'}>
                    {formatCurrency(transaction.value)}
                  </strong>

                  <button className="transaction-actions" type="button" aria-label="Ações da transação">
                    ⋮
                  </button>
                </article>
              ))}
            </section>
          ))}
        </div>
      </article>
    </section>
  );
}

function transactionTitle(tipo: string) {
  const titles: Record<string, string> = { DEPOSITO: 'Depósito', SAQUE: 'Saque', PIX_ENVIADO: 'PIX enviado', PIX_RECEBIDO: 'PIX recebido' };
  return titles[tipo] ?? tipo;
}

function groupTransactionsByMonth(items: Transaction[]) {
  const groups = new Map<string, Transaction[]>();

  items.forEach((item) => {
    const month = new Intl.DateTimeFormat('pt-BR', { month: 'long' }).format(new Date(item.date));
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
  }).format(new Date(value));
}
