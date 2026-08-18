package com.adryan.projetobanco.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.adryan.projetobanco.dto.ContaBancariaRequest;
import com.adryan.projetobanco.dto.ContaBancariaResponse;
import com.adryan.projetobanco.dto.TransferenciaPixRequest;
import com.adryan.projetobanco.model.ContaBancaria;
import com.adryan.projetobanco.repository.ContasRepository;
import com.adryan.projetobanco.repository.TransacaoRepository;
import com.adryan.projetobanco.persistence.ConnectionUtil;
import java.sql.Connection;

@Service
public class ContaBancariaService {

    private static final BigDecimal VALOR_MINIMO_DEPOSITO = new BigDecimal("1.00");

    private final ContasRepository contasRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaBancariaService(
            ContasRepository contasRepository,
            TransacaoRepository transacaoRepository) {
        this.contasRepository = contasRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public ContaBancariaResponse depositarPorClienteId(Long clienteId, BigDecimal valor) {
        try (Connection connection = ConnectionUtil.conectar()) {
            connection.setAutoCommit(false);
            try {
            validarValorDeposito(valor);

            ContaBancaria conta = contasRepository.buscarContaPorClienteId(connection, clienteId, true);
            validarConta(conta);

            BigDecimal novoSaldo = conta.getSaldo().add(valor);

            contasRepository.atualizarSaldo(connection, conta.getId(), novoSaldo);
            transacaoRepository.registrarTransacao(connection, conta.getId(), "DEPOSITO", valor,
                    "Deposito realizado na conta");
            connection.commit();
            return new ContaBancariaResponse("Deposito realizado com sucesso.", novoSaldo);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar deposito.", e);
        }
    }

    public ContaBancariaResponse sacarPorClienteId(Long clienteId, BigDecimal valor) {
        try (Connection connection = ConnectionUtil.conectar()) {
            connection.setAutoCommit(false);
            try {
            validarValorSaque(valor);
            ContaBancaria conta = contasRepository.buscarContaPorClienteId(connection, clienteId, true);
            validarConta(conta);
            validarSaldoSuficiente(conta, valor);

            BigDecimal novoSaldo = conta.getSaldo().subtract(valor);

            contasRepository.atualizarSaldo(connection, conta.getId(), novoSaldo);
            transacaoRepository.registrarTransacao(connection, conta.getId(), "SAQUE", valor, "Saque realizado na conta");

            connection.commit();
            return new ContaBancariaResponse("Saque realizado com sucesso.", novoSaldo);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar saque.", e);
        }
    }

    private void validarValorDeposito(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do deposito e obrigatorio.");
        }

        if (valor.compareTo(VALOR_MINIMO_DEPOSITO) < 0) {
            throw new IllegalArgumentException("O deposito minimo e de R$ 1,00.");
        }
    }

    private void validarValorSaque(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do saque e obrigatorio.");
        }

        if (valor.compareTo(VALOR_MINIMO_DEPOSITO) < 0) {
            throw new IllegalArgumentException("O saque minimo e de R$ 1,00.");
        }
    }

    private void validarSaldoSuficiente(ContaBancaria conta, BigDecimal valor) {
        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
    }

    private void validarConta(ContaBancaria conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta bancaria nao encontrada.");
        }

        if (!"ATIVA".equalsIgnoreCase(conta.getStatus())) {
            throw new IllegalArgumentException("Conta bancaria nao esta ativa.");
        }
    }

    
}
