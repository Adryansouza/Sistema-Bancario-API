package com.adryan.projetobanco.factory;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;

public final class ClienteFactory {

    private ClienteFactory() {
    }

    public static Cliente criar(String tipoCliente) {
        if ("FISICA".equalsIgnoreCase(tipoCliente)) {
            return new PessoaFisica();
        }
        if ("JURIDICA".equalsIgnoreCase(tipoCliente)) {
            return new PessoaJuridica();
        }
        throw new IllegalArgumentException("Tipo de cliente invalido: " + tipoCliente);
    }
}
