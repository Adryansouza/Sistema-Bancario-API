package com.adryan.projetobanco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import com.adryan.projetobanco.factory.ClienteFactory;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.model.TipoChavepix;
import com.adryan.projetobanco.strategy.ValidadorChavePixFactory;
import com.adryan.projetobanco.strategy.ValidadorCpfPix;
import com.adryan.projetobanco.strategy.ValidadorEmailPix;
import com.adryan.projetobanco.strategy.ValidadorTelefonePix;

class DesignPatternsTests {

    private final ValidadorChavePixFactory validadores = new ValidadorChavePixFactory(List.of(
            new ValidadorCpfPix(), new ValidadorEmailPix(), new ValidadorTelefonePix()));

    @Test
    void factoryCriaOClienteCorreto() {
        assertInstanceOf(PessoaFisica.class, ClienteFactory.criar("FISICA"));
        assertInstanceOf(PessoaJuridica.class, ClienteFactory.criar("JURIDICA"));
    }

    @Test
    void strategyNormalizaCadaTipoDeChave() {
        assertEquals("12345678901", validadores.obter(TipoChavepix.CPF)
                .validarENormalizar("123.456.789-01"));
        assertEquals("usuario@email.com", validadores.obter(TipoChavepix.EMAIL)
                .validarENormalizar(" Usuario@Email.COM "));
        assertEquals("11999998888", validadores.obter(TipoChavepix.TELEFONE)
                .validarENormalizar("(11) 99999-8888"));
    }

    @Test
    void strategyRejeitaChaveInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> validadores.obter(TipoChavepix.EMAIL).validarENormalizar("email-invalido"));
    }
}
