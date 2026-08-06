package com.adryan.projetobanco.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Esta classe abre a conexao entre o Java e o banco de dados MySQL.
public class ConnectionUtil {

    public static Connection conectar() throws SQLException {
        String url = DatabaseConfig.getUrl();
        String usuario = DatabaseConfig.getUsuario();
        String senha = DatabaseConfig.getSenha();

        if (url == null || usuario == null || senha == null) {
            throw new SQLException("Configure as variaveis de ambiente DB_URL, DB_USER e DB_PASSWORD.");
        }

        return DriverManager.getConnection(url, usuario, senha);
    }
}


