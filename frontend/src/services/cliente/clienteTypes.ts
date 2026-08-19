export type ClienteResponse = {
  id?: number;
  nome?: string;
  documento?: string;
  telefone?: string;
  cep?: string;
  endereco?: string;
  numero?: string;
  uf?: string;
  cpf?: string;
  cnpj?: string;
  tipoCliente?: string;
  conta?: ContaBancaria;
};

export type ContaBancaria = {
  id?: number;
  clienteId?: number;
  numeroConta?: string;
  agencia?: string;
  saldo?: number;
  status?: string;
  dataCriacao?: string;
};
