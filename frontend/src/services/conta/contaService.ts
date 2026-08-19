import { apiPost } from '../http/apiClient';
import type { ContaBancariaRequest, ContaBancariaResponse } from './contaTypes';

export function depositar(clienteId: number, request: ContaBancariaRequest) {
  return apiPost<ContaBancariaResponse, ContaBancariaRequest>(`/conta/deposito/${clienteId}`, request);
}

export function sacar(clienteId: number, request: ContaBancariaRequest) {
  return apiPost<ContaBancariaResponse, ContaBancariaRequest>(`/conta/saque/${clienteId}`, request);
}
