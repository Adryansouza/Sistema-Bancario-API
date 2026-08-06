package com.adryan.projetobanco.persistence;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Esta classe pega as configuracoes do banco pelo Windows ou pelo arquivo .env local.
public class DatabaseConfig {
    private static final Map<String, String> DOTENV = carregarDotEnv();

    public static String getUrl() throws SQLException {
        return prepararUrlJdbc(getValor("DB_URL"));
    }

    public static String getUsuario() {
        return getValor("DB_USER");
    }

    public static String getSenha() {
        return getValor("DB_PASSWORD");
    }

    private static String getValor(String nome) {
        String valor = System.getenv(nome);

        if (valor != null && !valor.isBlank()) {
            return valor;
        }

        return DOTENV.get(nome);
    }

    private static Map<String, String> carregarDotEnv() {
        Map<String, String> valores = new HashMap<>();
        Path caminho = Path.of(".env");

        if (!Files.exists(caminho)) {
            return valores;
        }

        try {
            for (String linha : Files.readAllLines(caminho)) {
                if (linha.isBlank() || linha.startsWith("#") || !linha.contains("=")) {
                    continue;
                }

                String[] partes = linha.split("=", 2);
                valores.put(partes[0].trim(), partes[1].trim());
            }
        } catch (IOException e) {
            System.out.println("Nao foi possivel ler o arquivo .env.");
        }

        return valores;
    }

    private static String prepararUrlJdbc(String url) throws SQLException {
        if (url == null || url.isBlank()) {
            return null;
        }

        if (url.startsWith("jdbc:mysql://")) {
            return url;
        }

        if (url.startsWith("mysql://")) {
            try {
                URI uri = new URI(url);
                return "jdbc:mysql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
            } catch (URISyntaxException e) {
                throw new SQLException("URL do banco de dados invalida.", e);
            }
        }

        return url;
    }
}
