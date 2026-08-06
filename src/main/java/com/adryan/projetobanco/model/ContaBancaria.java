package com.adryan.projetobanco.model;

import com.adryan.projetobanco.repository.ContasRepository;
import com.adryan.projetobanco.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ContaBancaria {

    Scanner scanner = new Scanner(System.in);

    private Long id;
    private Long clienteId;
    private String numeroConta;
    private String agencia;
    private BigDecimal saldo = BigDecimal.ZERO;
    private String status;
    private LocalDateTime dataCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void DepositarSaldo() {
        if (!contaPodeMovimentar()) {
            return;
        }

        System.out.println("Digite o valor a ser depositado: ");

        try {
            BigDecimal valorDeposito = new BigDecimal(scanner.nextLine());
            BigDecimal valorMinimo = new BigDecimal("1.00");

            if (valorDeposito.compareTo(valorMinimo) < 0) {
                System.out.println("Digite um valor valido maior ou igual a R$1,00");
                return;
            }

            this.saldo = this.saldo.add(valorDeposito);

            ContasRepository contasRepository = new ContasRepository();
            TransacaoRepository transacaoRepository = new TransacaoRepository();

            contasRepository.atualizarSaldo(this.id, this.saldo);
            transacaoRepository.registrarTransacao(this.id, "DEPOSITO", valorDeposito, "Deposito");

            System.out.println("Deposito concluido, seu saldo agora e de: " + saldo);
            System.out.println("Data e hora do deposito: " + formatarDataHora(LocalDateTime.now()));
        } catch (NumberFormatException e) {
            System.out.println("Digite um numero valido");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar deposito no banco de dados.");
            System.out.println("Detalhes do erro: " + e.getMessage());
        }
    }

    public void consultarSaldo() {

        System.out.println("Seu saldo atual e de: R$ " + this.saldo);
    }

    public void SacarSaldo() {
        if (!contaPodeMovimentar()) {
            return;
        }

        System.out.println("Seu saldo atual e de: " + saldo);
        System.out.println("--------------------------------");
        System.out.println("Digite o valor para saque:");

        try {
            BigDecimal valorSaque = new BigDecimal(scanner.nextLine());

            if (valorSaque.compareTo(BigDecimal.ZERO) <= 0 || valorSaque.compareTo(this.saldo) > 0) {
                System.out.println("Saldo insuficiente ou valor invalido.");
                return;
            }

            this.saldo = this.saldo.subtract(valorSaque);

            ContasRepository contasRepository = new ContasRepository();
            TransacaoRepository transacaoRepository = new TransacaoRepository();

            contasRepository.atualizarSaldo(this.id, this.saldo);
            transacaoRepository.registrarTransacao(
                    this.id,
                    "SAQUE",
                    valorSaque,
                    "Saque realizado na conta");

            System.out.println("Saque concluido com sucesso, seu saldo atual e de R$ " + saldo);
            System.out.println("Data e hora do saque: " + formatarDataHora(LocalDateTime.now()));
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido, digite um valor valido");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar saque no banco de dados.");
            System.out.println("Detalhes do erro: " + e.getMessage());
        }
    }

    public void consultarExtrato() {
        if (this.id == null) {
            System.out.println("Conta bancaria nao encontrada.");
            return;
        }

        try {
            TransacaoRepository transacaoRepository = new TransacaoRepository();
            transacaoRepository.buscarTransacoes(this.id);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar extrato.");
            System.out.println("Detalhes do erro: " + e.getMessage());
        }
    }

    private boolean contaPodeMovimentar() {
        if (id == null) {
            System.out.println("Conta bancaria nao encontrada.");
            return false;
        }

        if (!"ATIVA".equalsIgnoreCase(status)) {
            System.out.println("Conta bancaria nao esta ativa.");
            return false;
        }

        return true;
    }

    private String formatarDataHora(LocalDateTime dataHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatter);
    }
}
