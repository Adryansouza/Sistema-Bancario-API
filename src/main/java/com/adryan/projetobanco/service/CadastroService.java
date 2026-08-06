package com.adryan.projetobanco.service;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.ContaBancaria;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.repository.ClienteRepository;
import com.adryan.projetobanco.repository.ContasRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Scanner;

@Service
public class CadastroService {

    Scanner scanner = new Scanner(System.in);
    private Cliente clienteCadastrado;
    private ClienteRepository clienteRepository = new ClienteRepository();
    private ContasRepository contasRepository = new ContasRepository();

    public Cliente getClienteCadastrado() {
        return clienteCadastrado;
    }

    public void cadastroPessoaFisica(PessoaFisica pf) {

        validarCpf(pf);
        validarTelefone(pf);
        validarSenha(pf);

        Long clienteId = null;

        try {
            clienteId = clienteRepository.adicionarCliente(pf);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar cliente no banco: " + e.getMessage());
        }

        ContaBancaria conta = null;

        try {
            conta = contasRepository.criarContaPadrao(clienteId, pf.getUf());
        } catch (Exception e) {
            System.err.println("Erro ao criar conta: " + e.getMessage());
        }

        pf.setConta(conta);
    }

    public void cadastroPessoaJuridica(PessoaJuridica pj) {

        validarCnpj(pj);
        validarTelefone(pj);
        validarSenha(pj);

        try {
            clienteRepository.adicionarCliente(pj);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar cliente no banco: " + e.getMessage());
        }
    }


    public void validarCpf(PessoaFisica pf) {
        String cpf = pf.getCpf().replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 digitos.");
        }

        pf.setDocumento(cpf);
    }

    public void validarCnpj(PessoaJuridica pj) {
        boolean cnpjValido = false;

        while (!cnpjValido) {
            System.out.println("Digite seu CNPJ (apenas numeros):");
            String cnpj = scanner.nextLine();

            if (!isCnpjValido(cnpj)) {
                System.out.println("CNPJ invalido ou com formato incorreto. Tente novamente.");
            } else {
                cnpjValido = true;
                pj.setDocumento(cnpj.replaceAll("[^0-9]", ""));
                System.out.println("CNPJ validado com sucesso!");
            }
        }
    }

    public void validarTelefone(Cliente cliente) {
        String telefone = cliente.getTelefone();

        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone nao pode ficar em branco.");
        }

        cliente.setTelefone(telefone.replaceAll("[^0-9]", ""));
    }

    public static boolean isCnpjValido(String cnpj) {

        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14 ||
                cnpj.equals("00000000000000") || cnpj.equals("11111111111111") ||
                cnpj.equals("22222222222222") || cnpj.equals("33333333333333") ||
                cnpj.equals("44444444444444") || cnpj.equals("55555555555555") ||
                cnpj.equals("66666666666666") || cnpj.equals("77777777777777") ||
                cnpj.equals("88888888888888") || cnpj.equals("99999999999999")) {
            return false;
        }

        try {

            int soma = 0;
            int peso = 5;
            for (int i = 0; i < 12; i++) {
                int num = (int) (cnpj.charAt(i) - 48);
                soma += (num * peso);
                peso--;
                if (peso < 2)
                    peso = 9;
            }

            int resto = soma % 11;
            char digito13 = (resto < 2) ? '0' : (char) ((11 - resto) + 48);

            soma = 0;
            peso = 6;
            for (int i = 0; i < 13; i++) {
                int num = (int) (cnpj.charAt(i) - 48);
                soma += (num * peso);
                peso--;
                if (peso < 2)
                    peso = 9;
            }

            resto = soma % 11;
            char digito14 = (resto < 2) ? '0' : (char) ((11 - resto) + 48);

            return (digito13 == cnpj.charAt(12)) && (digito14 == cnpj.charAt(13));

        } catch (Exception e) {
            return false;
        }
    }

    public void validarSenha(Cliente cliente) {
        String senha = cliente.getSenha();

        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha nao pode ficar em branco.");
        }

        if (!senha.matches("\\d{8}")) {
            throw new IllegalArgumentException("Senha deve conter exatamente 8 numeros.");
        }
    }

    public void atualizarCadastro(Cliente cliente) {

        ClienteService clienteService = new ClienteService();



        //clienteService.mostrarDadosCliente(cliente);


        System.out.print("""
                O que deseja alterar?
                1 - Nome
                2 - Telefone
                3 - Endereço
                4 - Alterar tudo
                5 - Senha
                6 - Voltar
                Escolha uma opção: """);

        int opcaoEscolhida = scanner.nextInt();
        scanner.nextLine();

        try {

            if (opcaoEscolhida < 1 || opcaoEscolhida > 5) {
                System.out.println("Digite uma opção válida");
            } else {

                switch (opcaoEscolhida) {
                    case 1:
                        boolean nomeValido = false;
                        while (!nomeValido) {
                            System.out.println("Digite seu nome: ");
                            String nomeDigitado = scanner.nextLine();

                            if (nomeDigitado.isBlank()) {
                                System.out.println("Erro: O nome nao pode ficar em branco! Tente novamente.");
                            } else {
                                cliente.setNome(nomeDigitado);
                                clienteRepository.atualizarCliente(cliente);
                                nomeValido = true;
                            }
                        }
                        break;

                    case 2:
                        validarTelefone(cliente);
                        clienteRepository.atualizarCliente(cliente);
                        break;

                    case 3:
                        boolean enderecoValido = false;
                        while (!enderecoValido) {
                            System.out.println("Digite seu endereco: ");
                            String enderecoDigitado = scanner.nextLine();

                            if (enderecoDigitado.isBlank()) {
                                System.out.println("Erro: O endereco nao pode ficar em branco!");
                            } else {
                                cliente.setEndereco(enderecoDigitado);
                                clienteRepository.atualizarCliente(cliente);
                                enderecoValido = true;
                            }
                        }
                        break;

                    case 4:
                        String documentoAtual = cliente.getDocumento();

                        Cliente clienteDoBanco = clienteRepository.buscarClientePorId(Long.valueOf(documentoAtual));

                        if (clienteDoBanco == null) {
                            System.out.println("Cliente nao encontrado.");
                            return;
                        }

                        if (clienteDoBanco instanceof PessoaFisica) {
                            PessoaFisica pf = (PessoaFisica) clienteDoBanco;

                            System.out.println("Atualizando cadastro de pessoa fisica.");
                            atualizarDadosComuns(pf);
                            clienteRepository.atualizarCliente(pf);
                            copiarDadosAtualizados(cliente, pf);

                            System.out.println("Cadastro atualizado com sucesso!");

                        } else if (clienteDoBanco instanceof PessoaJuridica) {
                            PessoaJuridica pj = (PessoaJuridica) clienteDoBanco;

                            System.out.println("Atualizando cadastro de pessoa juridica.");
                            atualizarDadosComuns(pj);
                            clienteRepository.atualizarCliente(pj);
                            copiarDadosAtualizados(cliente, pj);

                            System.out.println("Cadastro atualizado com sucesso!");

                        } else {
                            System.out.println("Tipo de cliente invalido.");
                        }
                        break;

                    case 5:
                        String documentoLimpo = cliente.getDocumento();
                        Cliente clienteEncontrado = clienteRepository.buscarClientePorId(Long.valueOf(documentoLimpo));

                        System.out.println("Digite sua senha atual: ");
                        String senhaAtual = scanner.next();

                        if (!senhaAtual.equals(clienteEncontrado.getSenha())) {
                            System.out.println("Senha atual incorreta.");
                            break;
                        }
                        System.out.println("Digite a nova senha: ");
                        String novaSenha = scanner.next();

                        System.out.println("Digite a nova senha novamente: ");
                        String confirmarNovaSenha = scanner.next();

                        if (!novaSenha.equals(confirmarNovaSenha)) {
                            System.out.println("As senhas nao sao iguais.");
                            break;
                        }

                        if (!novaSenha.matches("\\d{8}")) {
                            System.out.println("A senha deve conter exatamente 8 numeros.");
                            break;
                        }

                        clienteRepository.atualizarSenha(documentoLimpo, novaSenha);
                        cliente.setSenha(novaSenha);

                        System.out.println("Senha alterada com sucesso!");

                        break;
                    case 6:

                        break;
                }

            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar cadastro: " + e.getMessage());
        }

    }

    private void atualizarDadosComuns(Cliente cliente) {
        cliente.setNome(
                lerCampoObrigatorio("Digite seu nome: ", "Erro: O nome nao pode ficar em branco! Tente novamente."));
        cliente.setTelefone(lerCampoObrigatorio("Digite seu telefone: ",
                "Erro: O telefone nao pode ficar em branco! Tente novamente."));
        cliente.setEndereco(lerCampoObrigatorio("Digite seu endereco: ", "Erro: O endereco nao pode ficar em branco!"));
    }

    private String lerCampoObrigatorio(String mensagem, String mensagemErro) {
        while (true) {
            System.out.println(mensagem);
            String valorDigitado = scanner.nextLine();

            if (valorDigitado.isBlank()) {
                System.out.println(mensagemErro);
            } else {
                return valorDigitado;
            }
        }
    }

    private void copiarDadosAtualizados(Cliente clienteAtual, Cliente clienteAtualizado) {
        clienteAtual.setNome(clienteAtualizado.getNome());
        clienteAtual.setTelefone(clienteAtualizado.getTelefone());
        clienteAtual.setEndereco(clienteAtualizado.getEndereco());
    }

}
