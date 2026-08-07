package com.adryan.projetobanco.service;

import org.springframework.stereotype.Service;

import com.adryan.projetobanco.dto.ChavePixRequest;
import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.model.TipoChavepix;
import com.adryan.projetobanco.repository.ChavePixRepository;

import java.sql.SQLException;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    public ChavePixService(ChavePixRepository chavePixRepository) {
        this.chavePixRepository = chavePixRepository;
    }

    public ChavePix cadastrarChavePix(ChavePixRequest chavePixRequest) {
        validarCadastroChavePix(chavePixRequest);

        try {
            ChavePix chavePix = new ChavePix();
            chavePix.setContaId(chavePixRequest.getConta_id());
            chavePix.setTipoChave(converterTipoChave(chavePixRequest.getTipo_chave()));
            chavePix.setValorChave(chavePixRequest.getValor_chave().trim());

            return chavePixRepository.cadastrarChavePix(chavePix);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar chave PIX.", e);
        }
    }

    private void validarCadastroChavePix(ChavePixRequest chavePixRequest) {
        if (chavePixRequest == null) {
            throw new IllegalArgumentException("Os campos nao podem estar vazios.");
        }

        validarContaId(chavePixRequest.getConta_id());
        validarTipoChave(chavePixRequest.getTipo_chave());
        validarValorChave(chavePixRequest.getValor_chave());
    }

    private void validarContaId(Long contaId) {
        if (contaId == null) {
            throw new IllegalArgumentException("A conta e obrigatoria.");
        }

        if (contaId <= 0) {
            throw new IllegalArgumentException("A conta informada e invalida.");
        }
    }

    private void validarTipoChave(String tipoChave) {
        if (tipoChave == null || tipoChave.isBlank()) {
            throw new IllegalArgumentException("O tipo da chave nao pode estar vazio.");
        }

        try {
            TipoChavepix.valueOf(tipoChave.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de chave PIX invalido. Use CPF, EMAIL ou TELEFONE.");
        }
    }

    private void validarValorChave(String valorChave) {
        if (valorChave == null || valorChave.isBlank()) {
            throw new IllegalArgumentException("O valor da chave nao pode estar vazio.");
        }
    }

    private TipoChavepix converterTipoChave(String tipoChave) {
        return TipoChavepix.valueOf(tipoChave.trim().toUpperCase());
    }
}
