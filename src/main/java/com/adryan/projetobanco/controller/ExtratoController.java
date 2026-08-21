package com.adryan.projetobanco.controller;

import com.adryan.projetobanco.dto.ExtratoResponse;
import com.adryan.projetobanco.service.TransacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conta")
public class ExtratoController {

    private final TransacaoService transacaoService;


    public ExtratoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping("/{contaId}/extrato")
    public List<ExtratoResponse> extratoTransacao(@PathVariable Long contaId) {
        return transacaoService.extratoTransacao(contaId);
    }
}
