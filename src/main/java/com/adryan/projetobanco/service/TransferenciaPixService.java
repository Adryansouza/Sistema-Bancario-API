package com.adryan.projetobanco.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.adryan.projetobanco.dto.TransferenciaPixRequest;
import com.adryan.projetobanco.dto.TransferenciaPixResponse;
import com.adryan.projetobanco.model.TipoChavepix;

@Service
public class TransferenciaPixService {

    private static final BigDecimal VALOR_MINIMO_TRANSFERENCIA = new BigDecimal("1.00");

    public TransferenciaPixResponse transferenciaPix(TransferenciaPixRequest request) {
        
        validarTransferenciaPix(request);

        return new TransferenciaPixResponse("Transferencia Pix validada com sucesso.", BigDecimal.ZERO);
    }

    private void validarTransferenciaPix(TransferenciaPixRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os campos nao podem estar vazios.");
        }

        if (request.getContaId() == null || request.getContaId() <= 0) {
            throw new IllegalArgumentException("A conta de origem e obrigatoria.");
        }

        if (request.getChavePixDestino() == null || request.getChavePixDestino().isBlank()) {
            throw new IllegalArgumentException("A chave Pix destino e obrigatoria.");
        }

        if (request.getTipoChavePix() == null || request.getTipoChavePix().isBlank()) {
            throw new IllegalArgumentException("O tipo da chave Pix e obrigatorio.");
        }

        try {
            TipoChavepix.valueOf(request.getTipoChavePix().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de chave Pix invalido. Use CPF, EMAIL ou TELEFONE.");
        }

        if (request.getValor() == null) {
            throw new IllegalArgumentException("O valor da transferencia e obrigatorio.");
        }

        if (request.getValor().compareTo(VALOR_MINIMO_TRANSFERENCIA) < 0) {
            throw new IllegalArgumentException("A transferencia minima e de R$ 1,00.");
        }

        if (request.getSenha() == null || request.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha e obrigatoria.");
        }
    }
}