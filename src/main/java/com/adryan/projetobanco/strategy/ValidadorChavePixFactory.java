package com.adryan.projetobanco.strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.adryan.projetobanco.model.TipoChavepix;

@Component
public class ValidadorChavePixFactory {
    private final Map<TipoChavepix, ValidadorChavePix> validadores = new EnumMap<>(TipoChavepix.class);

    public ValidadorChavePixFactory(List<ValidadorChavePix> validadores) {
        validadores.forEach(v -> this.validadores.put(v.tipoSuportado(), v));
    }

    public ValidadorChavePix obter(TipoChavepix tipo) {
        ValidadorChavePix validador = validadores.get(tipo);
        if (validador == null) {
            throw new IllegalArgumentException("Tipo de chave PIX nao suportado.");
        }
        return validador;
    }
}
