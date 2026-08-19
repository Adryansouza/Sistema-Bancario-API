import { apiPost } from '../http/apiClient';
import type { ClienteResponse } from '../cliente/clienteTypes';
import type { CadastroPessoaFisicaRequest, CadastroPessoaJuridicaRequest } from './cadastroTypes';

export function cadastrarPessoaFisica(request: CadastroPessoaFisicaRequest) {
  return apiPost<ClienteResponse, CadastroPessoaFisicaRequest>('/clientes/cadastro/pf', request);
}

export function cadastrarPessoaJuridica(request: CadastroPessoaJuridicaRequest) {
  return apiPost<ClienteResponse, CadastroPessoaJuridicaRequest>('/clientes/cadastro/pj', request);
}
