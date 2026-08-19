export type CadastroPessoaFisicaRequest = {
  nome: string;
  cpf: string;
  telefone: string;
  cep: string;
  endereco: string;
  numero: string;
  uf: string;
  senha: string;
};

export type CadastroPessoaJuridicaRequest = {
  nome: string;
  cnpj: string;
  razaoSocial: string;
  telefone: string;
  cep: string;
  endereco: string;
  numero: string;
  uf: string;
  senha: string;
};
