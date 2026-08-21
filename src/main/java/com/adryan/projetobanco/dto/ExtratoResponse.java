package com.adryan.projetobanco.dto;

import com.adryan.projetobanco.model.StatusTransacao;
import com.adryan.projetobanco.model.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExtratoResponse {


    //sobre a transação
    private TipoTransacao tipo; // pix, deposito etc
    private StatusTransacao status;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;
    private String idTransacao;
    private String descricao;

    //recebedor
    private String nomeDestinatario;
    private String documentoDestinatario;
    private  String chavePixDestino;

    //pagador
    private String nomeRemetente;
    private String documentoRemetente;





}
