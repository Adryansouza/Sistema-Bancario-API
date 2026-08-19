export type CadastroTipo = 'CPF' | 'CNPJ';

export type CadastroForm = {
  nome: string;
  cpf: string;
  cnpj: string;
  razaoSocial: string;
  telefone: string;
  cep: string;
  endereco: string;
  numero: string;
  uf: string;
  senha: string;
  confirmarSenha: string;
};

export const cadastroInicial: CadastroForm = {
  nome: '',
  cpf: '',
  cnpj: '',
  razaoSocial: '',
  telefone: '',
  cep: '',
  endereco: '',
  numero: '',
  uf: '',
  senha: '',
  confirmarSenha: '',
};
