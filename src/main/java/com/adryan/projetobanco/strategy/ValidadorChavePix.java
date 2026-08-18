package com.adryan.projetobanco.strategy;

import com.adryan.projetobanco.model.TipoChavepix;

public interface ValidadorChavePix {
    TipoChavepix tipoSuportado();
    String validarENormalizar(String valor);
}
