package com.adryan.projetobanco.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Transacao {

    private Long contaId;
    private String idTransacao;
    private TipoTransacao tipo;
    private StatusTransacao status;
    private BigDecimal valor;
    private String descricao;

    private String nomeDestinatario;
    private String documentoDestinatario;
    private String chavePixDestino;

    private String nomeRemetente;
    private String documentoRemetente;
}