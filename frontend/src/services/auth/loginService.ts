import { apiPost } from '../http/apiClient';
import type { ClienteResponse } from '../cliente/clienteTypes';
import type { LoginRequest } from './loginTypes';

export function login(request: LoginRequest) {
  return apiPost<ClienteResponse, LoginRequest>('/login/pf', request);
}
