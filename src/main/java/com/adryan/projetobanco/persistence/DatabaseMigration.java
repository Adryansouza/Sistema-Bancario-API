package com.adryan.projetobanco.persistence;

import org.flywaydb.core.Flyway;

// Esta classe roda as migrations do Flyway para criar ou atualizar as tabelas do banco.
public class DatabaseMigration {
    public static void main(String[] args) {
        String url;

        try {
            url = DatabaseConfig.getUrl();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        String usuario = DatabaseConfig.getUsuario();
        String senha = DatabaseConfig.getSenha();

        if (url == null || usuario == null || senha == null) {
            System.out.println("Configure as variaveis de ambiente DB_URL, DB_USER e DB_PASSWORD.");
            return;
        }

        Flyway flyway = Flyway.configure()
                .dataSource(url, usuario, senha)
                .load();

        flyway.migrate();
    }
}
