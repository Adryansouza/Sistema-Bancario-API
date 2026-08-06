package com.adryan.projetobanco.repository;

import com.adryan.projetobanco.model.ContaBancaria;
import com.adryan.projetobanco.persistence.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

@Repository
public class ContasRepository {

    private final Random random = new Random();

    public ContaBancaria criarContaPadrao(Long clienteId, String uf) throws SQLException {
        String numeroConta = gerarNumeroConta();
        String agencia = gerarAgencia(uf);
        BigDecimal saldo = BigDecimal.ZERO;
        String status = "ATIVA";
        LocalDateTime dataCriacao = LocalDateTime.now();

        return adicionarClienteConta(clienteId, numeroConta, agencia, saldo, status, dataCriacao);
    }

    public ContaBancaria adicionarClienteConta(
            Long clienteId,
            String numeroConta,
            String agencia,
            BigDecimal saldo,
            String status,
            LocalDateTime dataCriacao) throws SQLException {

        String sql = """
                INSERT INTO contas
                (cliente_id, numero_conta, agencia, saldo, status, data_criacao)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        for (int tentativa = 0; tentativa < 5; tentativa++) {
            try (
                    Connection connection = ConnectionUtil.conectar();
                    PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, clienteId);
                statement.setString(2, numeroConta);
                statement.setString(3, agencia);
                statement.setBigDecimal(4, saldo);
                statement.setString(5, status);
                statement.setTimestamp(6, Timestamp.valueOf(dataCriacao));

                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ContaBancaria conta = new ContaBancaria();
                        conta.setId(generatedKeys.getLong(1));
                        conta.setClienteId(clienteId);
                        conta.setNumeroConta(numeroConta);
                        conta.setAgencia(agencia);
                        conta.setSaldo(saldo);
                        conta.setStatus(status);
                        conta.setDataCriacao(dataCriacao);
                        return conta;
                    }
                }

                throw new SQLException("Erro ao obter id da conta cadastrada.");
            } catch (SQLIntegrityConstraintViolationException e) {
                numeroConta = gerarNumeroConta();
            }
        }

        throw new SQLException("Nao foi possivel gerar um numero de conta unico.");
    }

    public ContaBancaria buscarContaPorClienteId(Long clienteId) throws SQLException {
        String sql = """
                SELECT id, cliente_id, numero_conta, agencia, saldo, status, data_criacao
                FROM contas
                WHERE cliente_id = ?
                """;

        try (
                Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clienteId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    ContaBancaria conta = new ContaBancaria();
                    conta.setId(resultSet.getLong("id"));
                    conta.setClienteId(resultSet.getLong("cliente_id"));
                    conta.setNumeroConta(resultSet.getString("numero_conta"));
                    conta.setAgencia(resultSet.getString("agencia"));
                    conta.setSaldo(resultSet.getBigDecimal("saldo"));
                    conta.setStatus(resultSet.getString("status"));
                    conta.setDataCriacao(resultSet.getTimestamp("data_criacao").toLocalDateTime());
                    return conta;
                }
            }
        }

        return null;
    }

    public void atualizarSaldo(Long contaId, BigDecimal saldo) throws SQLException {
        String sql = """
                UPDATE contas
                SET saldo = ?
                WHERE id = ?
                """;

        try (
                Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, saldo);
            statement.setLong(2, contaId);
            statement.executeUpdate();
        }
    }

    private String gerarNumeroConta() {
        int numero = 10000000 + random.nextInt(90000000);
        int digito = random.nextInt(10);
        return numero + "-" + digito;
    }

    private String gerarAgencia(String uf) {
        String ufFormatada = uf == null ? "BR" : uf.replaceAll("[^A-Za-z]", "").toUpperCase();

        if (ufFormatada.length() != 2) {
            ufFormatada = "BR";
        }

        int numeroAgencia = 1000 + random.nextInt(9000);
        return numeroAgencia + "-" + ufFormatada;
    }
}
