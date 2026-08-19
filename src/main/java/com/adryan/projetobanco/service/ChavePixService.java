package com.adryan.projetobanco.service;

import org.springframework.stereotype.Service;

import com.adryan.projetobanco.dto.ChavePixRequest;
import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.model.TipoChavepix;
import com.adryan.projetobanco.repository.ChavePixRepository;
import com.adryan.projetobanco.repository.ContasRepository;
import com.adryan.projetobanco.strategy.ValidadorChavePixFactory;

import java.sql.SQLException;
import java.util.List;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;
    private final ContasRepository contasRepository;
    private final ValidadorChavePixFactory validadorFactory;

    public ChavePixService(ChavePixRepository chavePixRepository, ContasRepository contasRepository,
            ValidadorChavePixFactory validadorFactory) {
        this.chavePixRepository = chavePixRepository;
        this.contasRepository = contasRepository;
        this.validadorFactory = validadorFactory;
    }

    public ChavePix cadastrarChavePix(ChavePixRequest chavePixRequest) {
        validarCadastroChavePix(chavePixRequest);

        try {
            ChavePix chavePix = new ChavePix();
            if (contasRepository.buscarContaPorContaID(chavePixRequest.getConta_id()) == null) {
                throw new IllegalArgumentException("Conta bancaria nao encontrada.");
            }
            TipoChavepix tipo = converterTipoChave(chavePixRequest.getTipo_chave());
            chavePix.setContaId(chavePixRequest.getConta_id());
            chavePix.setTipoChave(tipo);
            chavePix.setValorChave(validadorFactory.obter(tipo)
                    .validarENormalizar(chavePixRequest.getValor_chave()));

            return chavePixRepository.cadastrarChavePix(chavePix);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar chave PIX.", e);
        }
    }

    public List<ChavePix> mostrarChaves(Long contaId) {
        validarContaId(contaId);
        try {
            if (contasRepository.buscarContaPorContaID(contaId) == null) {
                throw new IllegalArgumentException("Conta bancaria nao encontrada.");
            }
            return chavePixRepository.buscarPorContaId(contaId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar chaves PIX.", e);
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
