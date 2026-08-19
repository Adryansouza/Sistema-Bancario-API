package com.adryan.projetobanco.repository;

import com.adryan.projetobanco.persistence.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.adryan.projetobanco.dto.TransacaoResponse;

@Repository
public class TransacaoRepository {

    public void registrarTransacao(Long contaId, String tipo, BigDecimal valor, String descricao) throws SQLException {
        try (Connection connection = ConnectionUtil.conectar()) {
            registrarTransacao(connection, contaId, tipo, valor, descricao);
        }
    }

    public void registrarTransacao(Connection connection, Long contaId, String tipo, BigDecimal valor,
            String descricao) throws SQLException {

        String sql = """
                INSERT INTO transacoes
                (conta_id, tipo, valor, descricao)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);
            statement.setString(2, tipo);
            statement.setBigDecimal(3, valor);
            statement.setString(4, descricao);

            statement.executeUpdate();
        }
    }

    public List<TransacaoResponse> buscarTransacoes(Long contaId) throws SQLException {
        String sql = """
                SELECT id, tipo, valor, descricao, data_transacao
                FROM transacoes
                WHERE conta_id = ?
                ORDER BY data_transacao DESC
                """;

        try (
                Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<TransacaoResponse> transacoes = new ArrayList<>();

                while (resultSet.next()) {
                    transacoes.add(new TransacaoResponse(
                            resultSet.getLong("id"),
                            resultSet.getString("tipo"),
                            resultSet.getBigDecimal("valor"),
                            resultSet.getString("descricao"),
                            resultSet.getTimestamp("data_transacao").toLocalDateTime()));
                }
                return transacoes;
            }
        }
    }
}
