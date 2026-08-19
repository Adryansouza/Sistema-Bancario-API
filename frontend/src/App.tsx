import { useState } from 'react';
import { DashboardPage } from './components/Dashboard/DashboardPage';
import { LoginPage } from './components/Login/LoginPage';
import type { ClienteResponse } from './services/cliente/clienteTypes';

export function App() {
  const [cliente, setCliente] = useState<ClienteResponse | null>(null);

  if (cliente) {
    return <DashboardPage cliente={cliente} onLogout={() => setCliente(null)} />;
  }

  return <LoginPage onLogin={setCliente} />;
}
