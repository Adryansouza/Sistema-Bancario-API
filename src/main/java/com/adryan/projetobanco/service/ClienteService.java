package com.adryan.projetobanco.service;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ClienteService {




    public Cliente procurarCliente(String documentoUsuario) {
        String documentoLimpo = documentoUsuario.replaceAll("[^0-9]", "");

        if (documentoLimpo.isBlank()) {
            System.out.println("Digite um CPF/CNPJ valido.");
            return null;
        }

        try {
            Cliente clienteEncontrado = clienteRepository.buscarClientePorId(Long.valueOf(documentoLimpo));

            if (clienteEncontrado == null) {
                System.out.println("CPF/CNPJ nao cadastrado no banco.");
                return null;
            }

            boolean senhaEncontrada = false;

            while (!senhaEncontrada) {
                System.out.println("Digite sua senha:");

            }

            System.out.println("Login realizado com sucesso.");
            return clienteEncontrado;

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente no banco de dados.");
            System.out.println("Detalhes do erro: " + e.getMessage());
            return null;
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
