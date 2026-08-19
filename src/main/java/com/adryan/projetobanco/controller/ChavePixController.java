package com.adryan.projetobanco.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.adryan.projetobanco.dto.ChavePixRequest;
import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.service.ChavePixService;

@RestController
@RequestMapping("/cadastroChave")
public class ChavePixController {

    private final ChavePixService chavePixService;

    public ChavePixController(ChavePixService chavePixService) {
        this.chavePixService = chavePixService;
    }

    @PostMapping
    public ChavePix cadastroChave(@RequestBody ChavePixRequest chavePixRequest) {
        return chavePixService.cadastrarChavePix(chavePixRequest);
    }

    @GetMapping("/{contaId}")
    public List<ChavePix> mostrarChaves(@PathVariable Long contaId) {
        return chavePixService.mostrarChaves(contaId);
    }
}
