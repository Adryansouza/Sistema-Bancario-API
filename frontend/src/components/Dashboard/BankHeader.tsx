import type { ClienteResponse } from '../../services/cliente/clienteTypes';
import homeIcon from '../../assets/icons/home.png';
import keyIcon from '../../assets/icons/key.png';
import pixIcon from '../../assets/icons/pix.png';
import transferIcon from '../../assets/icons/transfer.png';
import { formatCurrency, getInitials } from './dashboardUtils';

type NavIconType = 'home' | 'transfer' | 'pix' | 'key';
export type DashboardSection = 'inicio' | 'transacoes' | 'pix' | 'chaves';

type BankHeaderProps = {
  cliente: ClienteResponse;
  saldo: number;
  activeSection: DashboardSection;
  onSectionChange: (section: DashboardSection) => void;
  onOpenAccount: () => void;
  onLogout: () => void;
};

export function BankHeader({ cliente, saldo, activeSection, onSectionChange, onOpenAccount, onLogout }: BankHeaderProps) {
  return (
    <header className="bank-header">
      <div className="bank-header-inner">
        <div className="dashboard-brand">
         
          <strong>Banco Horizonte</strong>
        </div>

        <div className="header-tools" aria-label="Resumo da conta">
          <button className="icon-button" type="button" aria-label="Abrir transações" onClick={() => onSectionChange('transacoes')}>
            Q
          </button>
          <div className="header-balance">
            <span>Saldo</span>
            <strong>{formatCurrency(saldo)}</strong>
          </div>
          <button className="logout-button" type="button" onClick={onLogout}>
            Sair
          </button>
          <button className="icon-button" type="button" aria-label="Abrir dados da conta" onClick={onOpenAccount}>
            {getInitials(cliente.nome)}
          </button>
        </div>
      </div>

      <nav className="bank-nav" aria-label="Menu principal">
        <button
          className={`nav-item ${activeSection === 'inicio' ? 'nav-item-active' : ''}`}
          type="button"
          onClick={() => onSectionChange('inicio')}
        >
          <NavIcon type="home" />
          Início
        </button>
        <button
          className={`nav-item ${activeSection === 'transacoes' ? 'nav-item-active' : ''}`}
          type="button"
          onClick={() => onSectionChange('transacoes')}
        >
          <NavIcon type="transfer" />
          Transações
        </button>
        <button
          className={`nav-item ${activeSection === 'pix' ? 'nav-item-active' : ''}`}
          type="button"
          onClick={() => onSectionChange('pix')}
        >
          <NavIcon type="pix" />
          PIX
        </button>
        <button
          className={`nav-item ${activeSection === 'chaves' ? 'nav-item-active' : ''}`}
          type="button"
          onClick={() => onSectionChange('chaves')}
        >
          <NavIcon type="key" />
          Chaves PIX
        </button>
      </nav>
    </header>
  );
}

function NavIcon({ type }: { type: NavIconType }) {
  const icons = {
    home: homeIcon,
    transfer: transferIcon,
    pix: pixIcon,
    key: keyIcon,
  };

  return <img className={`nav-icon nav-icon-${type}`} src={icons[type]} alt="" aria-hidden="true" />;
}
