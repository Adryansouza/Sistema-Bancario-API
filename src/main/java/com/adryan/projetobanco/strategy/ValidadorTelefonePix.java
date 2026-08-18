package com.adryan.projetobanco.strategy;

import org.springframework.stereotype.Component;
import com.adryan.projetobanco.model.TipoChavepix;

@Component
public class ValidadorTelefonePix implements ValidadorChavePix {
    public TipoChavepix tipoSuportado() { return TipoChavepix.TELEFONE; }

    public String validarENormalizar(String valor) {
        String telefone = valor == null ? "" : valor.replaceAll("[^0-9]", "");
        if (telefone.length() != 11) {
            throw new IllegalArgumentException("A chave telefone deve conter DDD e 9 digitos.");
        }
        return telefone;
    }
}
