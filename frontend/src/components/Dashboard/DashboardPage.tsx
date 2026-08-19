import { useState } from 'react';
import type { ClienteResponse } from '../../services/cliente/clienteTypes';
import { BankHeader, type DashboardSection } from './BankHeader';
import { ChavesPixPage } from './ChavesPixPage';
import { PixPage } from './PixPage';
import { PromoStack } from './PromoStack';
import { StatementCard } from './StatementCard';
import { TransactionsPage } from './TransactionsPage';
import { AccountDrawer } from './AccountDrawer';
import './Dashboard.css';

type DashboardPageProps = {
  cliente: ClienteResponse;
  onLogout: () => void;
};

export function DashboardPage({ cliente, onLogout }: DashboardPageProps) {
  const [activeSection, setActiveSection] = useState<DashboardSection>('inicio');
  const [clienteAtual, setClienteAtual] = useState(cliente);
  const [contaAberta, setContaAberta] = useState(false);
  const saldo = clienteAtual.conta?.saldo ?? 0;
  const contaId = clienteAtual.conta?.id;
  const atualizarSaldo = (novoSaldo: number) => setClienteAtual((atual) => ({ ...atual, conta: { ...atual.conta, saldo: novoSaldo } }));

  return (
    <main className="bank-home">
      <BankHeader
        cliente={clienteAtual}
        saldo={saldo}
        activeSection={activeSection}
        onSectionChange={setActiveSection}
        onOpenAccount={() => setContaAberta(true)}
        onLogout={onLogout}
      />

      <section className="dashboard-shell">
        {activeSection === 'transacoes' ? (
          <TransactionsPage contaId={contaId} />
        ) : activeSection === 'pix' ? (
          <PixPage saldo={saldo} contaId={contaId} onBalanceChange={atualizarSaldo} onOpenKeys={() => setActiveSection('chaves')} />
        ) : activeSection === 'chaves' ? (
          <ChavesPixPage contaId={contaId} onBack={() => setActiveSection('inicio')} />
        ) : (
          <div className="dashboard-grid">
            <StatementCard cliente={clienteAtual} saldo={saldo} contaId={contaId} />
            <PromoStack clienteId={clienteAtual.id} onOpenPix={() => setActiveSection('pix')} onOpenKeys={() => setActiveSection('chaves')} onBalanceChange={atualizarSaldo} />
          </div>
        )}
      </section>

      {contaAberta && (
        <AccountDrawer
          cliente={clienteAtual}
          onClose={() => setContaAberta(false)}
          onUpdate={(atualizado) => setClienteAtual((atual) => ({ ...atual, ...atualizado }))}
          onLogout={onLogout}
        />
      )}
    </main>
  );
}
