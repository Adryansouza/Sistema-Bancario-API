package com.adryan.projetobanco.repository;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.persistence.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Esta classe conversa diretamente com o MySQL para salvar, buscar, atualizar e deletar clientes.
@Repository
public class ClienteRepository {

        public Long adicionarCliente(Cliente cliente) throws SQLException {

                String sql = """
                                INSERT INTO cliente
                                (tipo_cliente, nome, documento, telefone, endereco, senha)
                                VALUES (?, ?, ?, ?, ?, ?)
                                """;

                try (
                                Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql,
                                                Statement.RETURN_GENERATED_KEYS)) {

                        statement.setString(1, cliente.getTipoCliente());
                        statement.setString(2, cliente.getNome());
                        statement.setString(3, cliente.getDocumento());
                        statement.setString(4, cliente.getTelefone());
                        statement.setString(5, cliente.getEndereco());
                        statement.setString(6, cliente.getSenha());

                        statement.executeUpdate();

                        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                        Long clienteId = generatedKeys.getLong(1);
                                        cliente.setId(clienteId);
                                        return clienteId;
                                }
                        }
                }

                throw new SQLException("Erro ao obter id do cliente cadastrado.");
        }

        public Cliente buscarClientePorId(Long id) throws SQLException {

                String sql = """
                                SELECT id, tipo_cliente, nome, documento, telefone, endereco, senha
                                FROM cliente
                                WHERE id = ?
                                """;

                try (
                                Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setLong(1,id);

                        try (ResultSet resultSet = statement.executeQuery()) {

                                if (resultSet.next()) {

                                        String tipoCliente = resultSet.getString("tipo_cliente");

                                        Cliente clienteEncontrado;

                                        if ("FISICA".equalsIgnoreCase(tipoCliente)) {

                                                PessoaFisica pessoaFisica = new PessoaFisica();

                                                pessoaFisica.setCpf(
                                                                resultSet.getString("documento"));

                                                clienteEncontrado = pessoaFisica;

                                        } else if ("JURIDICA".equalsIgnoreCase(tipoCliente)) {

                                                PessoaJuridica pessoaJuridica = new PessoaJuridica();

                                                pessoaJuridica.setCnpj(
                                                                resultSet.getString("documento"));

                                                clienteEncontrado = pessoaJuridica;

                                        }

                                        else {

                                                throw new SQLException(
                                                                "Tipo de cliente inválido: "
                                                                                + tipoCliente);
                                        }

                                        clienteEncontrado.setNome(
                                                        resultSet.getString("nome"));

                                        clienteEncontrado.setId(
                                                        resultSet.getLong("id"));

                                        clienteEncontrado.setDocumento(
                                                        resultSet.getString("documento"));

                                        clienteEncontrado.setTelefone(
                                                        resultSet.getString("telefone"));

                                        clienteEncontrado.setEndereco(
                                                        resultSet.getString("endereco"));

                                        clienteEncontrado.setSenha(
                                                        resultSet.getString("senha"));

                                        ContasRepository contasRepository = new ContasRepository();
                                        clienteEncontrado.setConta(
                                                        contasRepository.buscarContaPorClienteId(
                                                                        clienteEncontrado.getId()));

                                        return clienteEncontrado;
                                }
                        }
                }

                return null;
        }

        public void atualizarCliente(Cliente cliente) throws SQLException {
                String sql = """
                                UPDATE cliente
                                SET nome = ?, telefone = ?, endereco = ?
                                WHERE documento = ?
                                """;

                try (Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql);) {
                        statement.setString(1, cliente.getNome());
                        statement.setString(2, cliente.getTelefone());
                        statement.setString(3, cliente.getEndereco());
                        statement.setString(4, cliente.getDocumento());

                        statement.executeUpdate();
                } catch (Exception e) {
                        System.out.println("Erro ao atualizar cliente no banco de dados.");
                        System.out.println("Detalhes do erro: " + e.getMessage());
                }
        }

        public void atualizarSenha(String documento, String novaSenha) throws SQLException {
                String sql = """
                                UPDATE cliente
                                SET senha = ?
                                WHERE documento = ?
                                """;

                try (
                                Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, novaSenha);
                        statement.setString(2, documento);

                        statement.executeUpdate();
                }
        }

        public void deletarCliente() {

        }
}
