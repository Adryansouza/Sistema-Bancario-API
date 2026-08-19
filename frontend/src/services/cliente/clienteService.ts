import { apiGet, apiPatch } from '../http/apiClient';
import type { ClienteResponse } from './clienteTypes';

export type AtualizarClienteRequest = {
  nome?: string;
  telefone?: string;
  endereco?: string;
  uf?: string;
  senha?: string;
};

export function buscarCliente(id: number) {
  return apiGet<ClienteResponse>(`/clientes/${id}`);
}

export function atualizarCliente(id: number, tipoCliente: string | undefined, request: AtualizarClienteRequest) {
  const tipo = tipoCliente === 'JURIDICA' ? 'pj' : 'pf';
  return apiPatch<ClienteResponse, AtualizarClienteRequest>(`/clientes/${tipo}/${id}`, request);
}
