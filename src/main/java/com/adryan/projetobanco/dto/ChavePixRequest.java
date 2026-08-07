package com.adryan.projetobanco.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChavePixRequest {

    private Long conta_id;
    private String tipo_chave;
    private String valor_chave;
    

}
