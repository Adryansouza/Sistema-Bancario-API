package com.adryan.projetobanco.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.persistence.ConnectionUtil;

@Repository
public class ChavePixRepository {

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
}