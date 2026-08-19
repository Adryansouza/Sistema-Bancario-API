export type ContaBancariaRequest = {
  valor: number;
};

export type ContaBancariaResponse = {
  mensagem: string;
  saldoAtual: number;
};
