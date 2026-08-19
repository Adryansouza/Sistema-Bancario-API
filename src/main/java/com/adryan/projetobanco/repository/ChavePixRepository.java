package com.adryan.projetobanco.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.persistence.ConnectionUtil;
import com.adryan.projetobanco.model.TipoChavepix;

@Repository
public class ChavePixRepository {

    public List<ChavePix> buscarPorContaId(Long contaId) throws SQLException {
        String sql = """
                SELECT id, conta_id, tipo_chave, valor_chave, data_criacao
                FROM chaves_pix
                WHERE conta_id = ?
                ORDER BY data_criacao DESC
                """;

        try (Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contaId);
            try (ResultSet rs = statement.executeQuery()) {
                List<ChavePix> chaves = new ArrayList<>();
                while (rs.next()) {
                    chaves.add(mapearChave(rs));
                }
                return chaves;
            }
        }
    }

    public ChavePix cadastrarChavePix(ChavePix chavePix) throws SQLException {

        String sql = """
                INSERT INTO chaves_pix
                (conta_id, tipo_chave, valor_chave)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = ConnectionUtil.conectar();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, chavePix.getContaId());
            statement.setString(2, chavePix.getTipoChave().name());
            statement.setString(3, chavePix.getValorChave());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chavePix.setId(generatedKeys.getLong(1));
                    return chavePix;
                }
            }

            throw new SQLException("Erro ao obter id da chave PIX cadastrada.");
        }
    }

    public ChavePix buscarPorValorETipo(Connection connection, String valor, TipoChavepix tipo) throws SQLException {
        String sql = """
                SELECT id, conta_id, tipo_chave, valor_chave, data_criacao
                FROM chaves_pix
                WHERE valor_chave = ? AND tipo_chave = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, valor);
            statement.setString(2, tipo.name());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapearChave(rs);
            }
        }
    }

    private ChavePix mapearChave(ResultSet rs) throws SQLException {
        ChavePix chave = new ChavePix();
        chave.setId(rs.getLong("id"));
        chave.setContaId(rs.getLong("conta_id"));
        chave.setTipoChave(TipoChavepix.valueOf(rs.getString("tipo_chave")));
        chave.setValorChave(rs.getString("valor_chave"));
        chave.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        return chave;
    }
}
