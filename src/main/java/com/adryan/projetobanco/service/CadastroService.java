package com.adryan.projetobanco.service;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.ContaBancaria;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.repository.ClienteRepository;
import com.adryan.projetobanco.repository.ContasRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class CadastroService {

    private ClienteRepository clienteRepository = new ClienteRepository();
    private ContasRepository contasRepository = new ContasRepository();

    public PessoaFisica cadastrarPessoaFisica(PessoaFisica pf) {

        validarNome(pf);
        validarCpf(pf);
        validarTelefone(pf);
        validarEndereco(pf);
        validarUf(pf);
        validarSenha(pf);

        try {

            Long clienteId = clienteRepository.adicionarCliente(pf);

            ContaBancaria conta = contasRepository.criarContaPadrao(clienteId, pf.getUf());

            pf.setConta(conta);

            return pf;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar pessoa física.", e);
        }
    }

    public PessoaJuridica cadastrarPessoaJuridica(PessoaJuridica pj) {

        validarNome(pj);
        validarCnpj(pj);
        validarTelefone(pj);
        validarEndereco(pj);
        validarSenha(pj);

        try {

            Long clienteId = clienteRepository.adicionarCliente(pj);

            ContaBancaria conta = contasRepository.criarContaPadrao(clienteId, pj.getUf());

            pj.setConta(conta);

            return pj;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar pessoa jurídica.", e);
        }
    }

    public PessoaFisica atualizarCadastroPf(Long id, PessoaFisica dadosNovos) {

        try {
            Cliente clienteDoBanco = clienteRepository.buscarClientePorId(id);

            if (clienteDoBanco == null) {
                throw new IllegalArgumentException("Cliente não encontrado.");
            }

            if (!(clienteDoBanco instanceof PessoaFisica)) {
                throw new IllegalArgumentException("O cliente informado não é pessoa física.");
            }

            PessoaFisica pfAtual = (PessoaFisica) clienteDoBanco;

            if (dadosNovos.getNome() != null) {
                pfAtual.setNome(dadosNovos.getNome());
            }

            if (dadosNovos.getCpf() != null) {
                pfAtual.setCpf(dadosNovos.getCpf());
            }

            if (dadosNovos.getTelefone() != null) {
                pfAtual.setTelefone(dadosNovos.getTelefone());
            }

            if (dadosNovos.getEndereco() != null) {
                pfAtual.setEndereco(dadosNovos.getEndereco());
            }

            if (dadosNovos.getUf() != null) {
                pfAtual.setUf(dadosNovos.getUf());
            }

            if (dadosNovos.getSenha() != null) {
                pfAtual.setSenha(dadosNovos.getSenha());
            }

            validarNome(pfAtual);
            validarCpf(pfAtual);
            validarTelefone(pfAtual);
            validarEndereco(pfAtual);
            validarUf(pfAtual);
            validarSenha(pfAtual);

            clienteRepository.atualizarCliente(pfAtual);

            return (PessoaFisica) clienteRepository.buscarClientePorId(id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pessoa física.", e);
        }
    }

    public PessoaJuridica atualizarCadastroPj(
            Long id,
            PessoaJuridica dadosNovos) {

        try {
            Cliente clienteDoBanco = clienteRepository.buscarClientePorId(id);

            if (clienteDoBanco == null) {
                throw new IllegalArgumentException(
                        "Cliente não encontrado.");
            }

            if (!(clienteDoBanco instanceof PessoaJuridica)) {
                throw new IllegalArgumentException(
                        "O cliente informado não é pessoa jurídica.");
            }

            PessoaJuridica pjAtual = (PessoaJuridica) clienteDoBanco;

            if (dadosNovos.getNome() != null) {
                pjAtual.setNome(dadosNovos.getNome());
            }

            if (dadosNovos.getCnpj() != null) {
                pjAtual.setCnpj(dadosNovos.getCnpj());
            }

            if (dadosNovos.getTelefone() != null) {
                pjAtual.setTelefone(dadosNovos.getTelefone());
            }

            if (dadosNovos.getEndereco() != null) {
                pjAtual.setEndereco(dadosNovos.getEndereco());
            }

            if (dadosNovos.getSenha() != null) {
                pjAtual.setSenha(dadosNovos.getSenha());
            }

            validarNome(pjAtual);
            validarCnpj(pjAtual);
            validarTelefone(pjAtual);
            validarEndereco(pjAtual);
            validarSenha(pjAtual);

            clienteRepository.atualizarCliente(pjAtual);

            return (PessoaJuridica) clienteRepository.buscarClientePorId(id);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao atualizar pessoa jurídica.",
                    e);
        }
    }

    // parte de validações

    public void validarNome(Cliente cliente) {

        String nome = cliente.getNome();

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ficar em branco.");
        }

        if (nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome deve possuir no mínimo 3 caracteres.");
        }

        cliente.setNome(nome.trim());
    }

    public void validarTelefone(Cliente cliente) {

        String telefone = cliente.getTelefone();

        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode ficar em branco.");
        }

        telefone = telefone.replaceAll("[^0-9]", "");

        if (telefone.length() != 11) {
            throw new IllegalArgumentException("Telefone deve possuir 11 dígitos.");
        }

        cliente.setTelefone(telefone);
    }

    public void validarEndereco(Cliente cliente) {

        String endereco = cliente.getEndereco();

        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço não pode ficar em branco.");
        }

        if (endereco.trim().length() < 5) {
            throw new IllegalArgumentException("Endereço inválido.");
        }

        cliente.setEndereco(endereco.trim());
    }

    public void validarSenha(Cliente cliente) {

        String senha = cliente.getSenha();

        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ficar em branco.");
        }

        if (!senha.matches("\\d{8}")) {
            throw new IllegalArgumentException("A senha deve conter exatamente 8 números.");
        }
    }

    // árte de validações Pessoa fisica

    public void validarCpf(PessoaFisica pf) {

        String cpf = pf.getCpf();

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve possuir 11 dígitos.");
        }

        pf.setCpf(cpf);
        pf.setDocumento(cpf);
    }

    public void validarUf(PessoaFisica pf) {

        String uf = pf.getUf();

        if (uf == null || uf.isBlank()) {
            throw new IllegalArgumentException("UF é obrigatória.");
        }

        uf = uf.trim().toUpperCase();

        if (uf.length() != 2) {
            throw new IllegalArgumentException("UF inválida.");
        }

        pf.setUf(uf);
    }

    // parte de validações pessoa juridica

    public void validarCnpj(PessoaJuridica pj) {

        String cnpj = pj.getCnpj();

        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ é obrigatório.");
        }

        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (!isCnpjValido(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }

        pj.setCnpj(cnpj);
        pj.setDocumento(cnpj);
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
}
