package com.adryan.projetobanco.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ChavePix {

    private Long id;
    private Long contaId;
    private String valorChave;
    private TipoChavepix tipoChave;
    private LocalDateTime dataCriacao;

   
}
