import { apiGet } from '../http/apiClient';
import type { ExtratoResponse, TransacaoResponse } from './transacaoTypes';

export function listarTransacoes(contaId: number) {
  return apiGet<TransacaoResponse[]>(`/conta/${contaId}/transacoes`);
}

export function buscarExtrato(contaId: number) {
  return apiGet<ExtratoResponse[]>(`/conta/${contaId}/extrato`);
}
