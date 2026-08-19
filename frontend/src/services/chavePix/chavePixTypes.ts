export type TipoChavePix = 'CPF' | 'EMAIL' | 'TELEFONE';

export type ChavePixRequest = {
  conta_id: number;
  tipo_chave: TipoChavePix;
  valor_chave: string;
};

export type ChavePixResponse = {
  id: number;
  contaId: number;
  tipoChave: TipoChavePix;
  valorChave: string;
  dataCriacao: string;
};
