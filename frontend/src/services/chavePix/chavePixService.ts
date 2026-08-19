import { apiGet, apiPost } from '../http/apiClient';
import type { ChavePixRequest, ChavePixResponse } from './chavePixTypes';

export function cadastrarChavePix(request: ChavePixRequest) {
  return apiPost<ChavePixResponse, ChavePixRequest>('/cadastroChave', request);
}

export function listarChavesPix(contaId: number) {
  return apiGet<ChavePixResponse[]>(`/cadastroChave/${contaId}`);
}
