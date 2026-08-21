package com.adryan.projetobanco.service;

import java.sql.SQLException;
import java.util.List;

import com.adryan.projetobanco.dto.ExtratoResponse;
import org.springframework.stereotype.Service;

import com.adryan.projetobanco.dto.TransacaoResponse;
import com.adryan.projetobanco.repository.TransacaoRepository;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public List<TransacaoResponse> listarPorConta(Long contaId) {
        try {
            return transacaoRepository.buscarTransacoes(contaId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar o extrato.", e);
        }
    }


    public List<ExtratoResponse> extratoTransacao(Long contaId) {
        if (contaId == null || contaId <= 0) {
            throw new IllegalArgumentException("O ID da conta e invalido.");
        }

        try {
            return transacaoRepository.buscarExtratoPorConta(contaId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar o extrato.", e);
        }
    }
}
