package com.adryan.projetobanco.repository;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.factory.ClienteFactory;
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

        private final ContasRepository contasRepository;

        public ClienteRepository(ContasRepository contasRepository) {
                this.contasRepository = contasRepository;
        }

        public Long adicionarCliente(Cliente cliente) throws SQLException {

                try (Connection connection = ConnectionUtil.conectar()) {
                        return adicionarCliente(connection, cliente);
                }
        }

        public Long adicionarCliente(Connection connection, Cliente cliente) throws SQLException {

                String sql = """
                                INSERT INTO cliente
                                (tipo_cliente, nome, documento, telefone, endereco, senha, uf)
                                VALUES (?, ?, ?, ?, ?, ?, ?)
                                """;

                try (PreparedStatement statement = connection.prepareStatement(sql,
                                                Statement.RETURN_GENERATED_KEYS)) {

                        statement.setString(1, cliente.getTipoCliente());
                        statement.setString(2, cliente.getNome());
                        statement.setString(3, cliente.getDocumento());
                        statement.setString(4, cliente.getTelefone());
                        statement.setString(5, cliente.getEndereco());
                        statement.setString(6, cliente.getSenha());
                        statement.setString(7, cliente.getUf());

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

        public Cliente buscarClientePorDocumento(String documentoLimpo) throws SQLException {

                String sql = """
                                SELECT id, tipo_cliente, nome, documento, telefone, endereco, senha, uf
                                FROM cliente
                                WHERE documento = ?
                                """;

                try (
                                Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, documentoLimpo);

                        try (ResultSet resultSet = statement.executeQuery()) {

                                if (resultSet.next()) {

                                        String tipoCliente = resultSet.getString("tipo_cliente");

                                        Cliente clienteEncontrado = criarCliente(tipoCliente, resultSet.getString("documento"));

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
                                        clienteEncontrado.setUf(
                                                        resultSet.getString("Uf"));

                                        clienteEncontrado.setConta(
                                                        contasRepository.buscarContaPorClienteId(
                                                                        clienteEncontrado.getId()));

                                        return clienteEncontrado;
                                }
                        }
                }

                return null;
        }

        public Cliente buscarClientePorId(Long id) throws SQLException {

                String sql = """
                                SELECT id, tipo_cliente, nome, documento, telefone, endereco, senha, uf
                                FROM cliente
                                WHERE id = ?
                                """;

                try (
                                Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setLong(1, id);

                        try (ResultSet resultSet = statement.executeQuery()) {

                                if (resultSet.next()) {

                                        String tipoCliente = resultSet.getString("tipo_cliente");

                                        Cliente clienteEncontrado = criarCliente(tipoCliente, resultSet.getString("documento"));

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
                                        clienteEncontrado.setUf(
                                                        resultSet.getString("Uf"));

                                        clienteEncontrado.setSenha(
                                                        resultSet.getString("senha"));

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
                                SET nome = ?, documento = ?, telefone = ?, endereco = ?, senha = ?, uf = ?
                                WHERE id = ?
                                """;

                try (Connection connection = ConnectionUtil.conectar();
                                PreparedStatement statement = connection.prepareStatement(sql);) {
                        statement.setString(1, cliente.getNome());
                        statement.setString(2, cliente.getDocumento());
                        statement.setString(3, cliente.getTelefone());
                        statement.setString(4, cliente.getEndereco());
                        statement.setString(5, cliente.getSenha());
                        statement.setString(6, cliente.getUf());
                        statement.setLong(7, cliente.getId());

                        statement.executeUpdate();
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

        private Cliente criarCliente(String tipoCliente, String documento) throws SQLException {
                try {
                        Cliente cliente = ClienteFactory.criar(tipoCliente);
                        if (cliente instanceof PessoaFisica pf) {
                                pf.setCpf(documento);
                        } else if (cliente instanceof PessoaJuridica pj) {
                                pj.setCnpj(documento);
                        }
                        return cliente;
                } catch (IllegalArgumentException e) {
                        throw new SQLException(e.getMessage(), e);
                }
        }
}
