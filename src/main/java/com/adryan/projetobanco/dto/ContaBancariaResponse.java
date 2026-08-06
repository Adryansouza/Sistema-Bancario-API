package com.adryan.projetobanco.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContaBancariaResponse {

    private String mensagem;
    private BigDecimal saldoAtual;
    
}
