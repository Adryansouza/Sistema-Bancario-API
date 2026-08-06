package com.adryan.projetobanco.service;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.adryan.projetobanco.dto.LoginRequest;

import java.sql.SQLException;

@Service
public class ClienteService {




    public Cliente login(LoginRequest loginRequest) {

    String documentoLimpo = loginRequest
            .getDocumento()
            .replaceAll("[^0-9]", "");

    String senhaDigitada = loginRequest.getSenha();

    if (documentoLimpo.isBlank()) {
        throw new IllegalArgumentException(
                "Digite um CPF ou CNPJ válido."
        );
    }

    if (senhaDigitada == null || senhaDigitada.isBlank()) {
        throw new IllegalArgumentException(
                "A senha é obrigatória."
        );
    }

    try {

        Cliente clienteEncontrado =
                clienteRepository.buscarClientePorDocumento(documentoLimpo);

        if (clienteEncontrado == null) {
            throw new IllegalArgumentException(
                    "CPF ou CNPJ não cadastrado."
            );
        }

        if (!senhaDigitada.equals(clienteEncontrado.getSenha())) {
            throw new IllegalArgumentException(
                    "Senha incorreta."
            );
        }

        return clienteEncontrado;

    } catch (SQLException e) {
        throw new RuntimeException(
                "Erro ao buscar cliente no banco de dados.",
                e
        );
    }
}

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente buscarClientePorId(Long id) {

        try {
            Cliente cliente = clienteRepository.buscarClientePorId(id);

            if (cliente == null) {
                throw new RuntimeException("Cliente não encontrado.");
            }

            return cliente;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente.", e);
        }
    }



}
