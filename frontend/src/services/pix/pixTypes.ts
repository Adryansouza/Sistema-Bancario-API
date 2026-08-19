import type { TipoChavePix } from '../chavePix/chavePixTypes';

export type TransferenciaPixRequest = {
  contaId: number;
  chavePixDestino: string;
  tipoChavePix: TipoChavePix;
  valor: number;
  senha: string;
};

export type TransferenciaPixResponse = {
  mensagem: string;
  saldoAtual: number;
};
