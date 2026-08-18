package com.adryan.projetobanco.strategy;

import org.springframework.stereotype.Component;
import com.adryan.projetobanco.model.TipoChavepix;

@Component
public class ValidadorCpfPix implements ValidadorChavePix {
    public TipoChavepix tipoSuportado() { return TipoChavepix.CPF; }

    public String validarENormalizar(String valor) {
        String cpf = valor == null ? "" : valor.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("A chave CPF deve conter 11 digitos.");
        }
        return cpf;
    }
}
