export type TransacaoResponse = {
  id: number;
  tipo: 'DEPOSITO' | 'SAQUE' | 'PIX_ENVIADO' | 'PIX_RECEBIDO' | string;
  valor: number;
  descricao: string;
  dataTransacao: string;
};
