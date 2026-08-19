package com.adryan.projetobanco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransacaoResponse {
    private Long id;
    private String tipo;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime dataTransacao;
}
