package com.adryan.projetobanco.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class TransferenciaPix {
    private Long id;
    private ContaBancaria contaOrigem;
    private String chavePixDestino;
    private TipoChavepix tipoChavePix;
    private BigDecimal valor;
    private LocalDateTime dataTransferencia;
}
