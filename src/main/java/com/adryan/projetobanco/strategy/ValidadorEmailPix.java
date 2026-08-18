package com.adryan.projetobanco.strategy;

import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.adryan.projetobanco.model.TipoChavepix;

@Component
public class ValidadorEmailPix implements ValidadorChavePix {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public TipoChavepix tipoSuportado() { return TipoChavepix.EMAIL; }

    public String validarENormalizar(String valor) {
        String email = valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("A chave de e-mail e invalida.");
        }
        return email;
    }
}
