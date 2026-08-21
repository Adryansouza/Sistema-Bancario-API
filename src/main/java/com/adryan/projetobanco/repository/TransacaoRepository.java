package com.adryan.projetobanco.repository;

import com.adryan.projetobanco.dto.ExtratoResponse;
import com.adryan.projetobanco.model.StatusTransacao;
import com.adryan.projetobanco.model.TipoTransacao;
import com.adryan.projetobanco.model.Transacao;
import com.adryan.projetobanco.persistence.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.adryan.projetobanco.dto.TransacaoResponse;

@Repository
public class TransacaoRepository {

    // Mantém compatibilidade com depósito e saque enquanto esses serviços ainda
    // utilizam a assinatura antiga.
    public void registrarTransacao(Connection connection, Long contaId, String tipo,
            BigDecimal valor, String descricao) throws SQLException {
        String sql = """
                INSERT INTO transacoes (
                    conta_id,
                    id_transacao,
                    tipo,
                    status,
                    valor,
                    descricao
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);
            statement.setString(2, UUID.randomUUID().toString());
            statement.setString(3, tipo);
            statement.setString(4, StatusTransacao.CONCLUIDA.name());
            statement.setBigDecimal(5, valor);
            statement.setString(6, descricao);
            statement.executeUpdate();
        }
    }

    public void registrarTransacao(Connection connection, Transacao transacao) throws SQLException {

        String sql = """
            INSERT INTO transacoes (
                conta_id,
                id_transacao,
                tipo,
                status,
                valor,
                descricao,
                nome_destinatario,
                documento_destinatario,
                chave_pix_destino,
                nome_remetente,
                documento_remetente
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, transacao.getContaId());
            statement.setString(2, transacao.getIdTransacao());
            statement.setString(3, transacao.getTipo().name());
            statement.setString(4, transacao.getStatus().name());
            statement.setBigDecimal(5, transacao.getValor());
            statement.setString(6, transacao.getDescricao());

            statement.setString(7, transacao.getNomeDestinatario());
            statement.setString(8, transacao.getDocumentoDestinatario());
            statement.setString(9, transacao.getChavePixDestino());

            statement.setString(10, transacao.getNomeRemetente());
            statement.setString(11, transacao.getDocumentoRemetente());

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

    public List<ExtratoResponse> buscarExtratoPorConta(Long contaId) throws SQLException {
        String sql = """
                SELECT
                    id_transacao,
                    tipo,
                    status,
                    valor,
                    data_transacao,
                    descricao,
                    nome_destinatario,
                    documento_destinatario,
                    chave_pix_destino,
                    nome_remetente,
                    documento_remetente
                FROM transacoes
                WHERE conta_id = ?
                ORDER BY data_transacao DESC, id DESC
                """;

        try (Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<ExtratoResponse> extrato = new ArrayList<>();

                while (resultSet.next()) {
                    extrato.add(new ExtratoResponse(
                            TipoTransacao.valueOf(resultSet.getString("tipo")),
                            StatusTransacao.valueOf(resultSet.getString("status")),
                            resultSet.getBigDecimal("valor"),
                            resultSet.getTimestamp("data_transacao").toLocalDateTime(),
                            resultSet.getString("id_transacao"),
                            resultSet.getString("descricao"),
                            resultSet.getString("nome_destinatario"),
                            resultSet.getString("documento_destinatario"),
                            resultSet.getString("chave_pix_destino"),
                            resultSet.getString("nome_remetente"),
                            resultSet.getString("documento_remetente")));
                }

                return extrato;
            }
        }
    }
}
