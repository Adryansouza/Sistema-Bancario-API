import { apiPost } from '../http/apiClient';
import type { TransferenciaPixRequest, TransferenciaPixResponse } from './pixTypes';

export function transferirPix(request: TransferenciaPixRequest) {
  return apiPost<TransferenciaPixResponse, TransferenciaPixRequest>('/pix', request);
}
