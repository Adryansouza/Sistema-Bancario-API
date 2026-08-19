import { apiGet } from '../http/apiClient';
import type { TransacaoResponse } from './transacaoTypes';

export function listarTransacoes(contaId: number) {
  return apiGet<TransacaoResponse[]>(`/conta/${contaId}/transacoes`);
}
