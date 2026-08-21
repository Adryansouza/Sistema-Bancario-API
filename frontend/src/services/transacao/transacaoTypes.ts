export type TransacaoResponse = {
  id: number;
  tipo: 'DEPOSITO' | 'SAQUE' | 'PIX_ENVIADO' | 'PIX_RECEBIDO' | string;
  valor: number;
  descricao: string;
  dataTransacao: string;
};

export type TipoTransacao = 'DEPOSITO' | 'SAQUE' | 'PIX_ENVIADO' | 'PIX_RECEBIDO';
export type StatusTransacao = 'CONCLUIDA' | 'PENDENTE' | 'CANCELADA' | 'ESTORNADA';

export type ExtratoResponse = {
  tipo: TipoTransacao;
  status: StatusTransacao;
  valor: number;
  dataTransacao: string;
  idTransacao: string;
  descricao: string;
  nomeDestinatario: string | null;
  documentoDestinatario: string | null;
  chavePixDestino: string | null;
  nomeRemetente: string | null;
  documentoRemetente: string | null;
};
