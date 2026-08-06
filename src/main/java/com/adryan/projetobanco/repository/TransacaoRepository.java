package com.adryan.projetobanco.repository;

import com.adryan.projetobanco.persistence.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

@Repository
public class TransacaoRepository {

    public void registrarTransacao(Long contaId, String tipo, BigDecimal valor, String descricao) throws SQLException {

        String sql = """
                INSERT INTO transacoes
                (conta_id, tipo, valor, descricao)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);
            statement.setString(2, tipo);
            statement.setBigDecimal(3, valor);
            statement.setString(4, descricao);

            statement.executeUpdate();
        }
    }

    public void buscarTransacoes(Long contaId) throws SQLException {
        String sql = """
                SELECT tipo, valor, data_transacao
                FROM transacoes
                WHERE conta_id = ?
                ORDER BY data_transacao DESC
                """;

        try (
                Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean encontrouTransacao = false;

                System.out.println("--------- EXTRATO ---------");

                while (resultSet.next()) {
                    encontrouTransacao = true;

                    System.out.println("Tipo: " + resultSet.getString("tipo"));
                    System.out.println("Valor: R$ " + resultSet.getBigDecimal("valor"));
                    System.out.println("Data: " + resultSet.getTimestamp("data_transacao"));
                    System.out.println("---------------------------");
                }

                if (!encontrouTransacao) {
                    System.out.println("Nenhuma transacao encontrada.");
                }
            }
        }

    }
}
