package com.adryan.projetobanco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adryan.projetobanco.dto.ChavePixRequest;
import com.adryan.projetobanco.dto.ChavePixResponse;
import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.service.ChavePixService;

@RestController
@RequestMapping("/cadastroChave")
public class ChavePixController {

    @Autowired
    private ChavePixService chavePixService;

    @PostMapping
    public ChavePix cadastroChave(@RequestBody ChavePixRequest chavePixRequest) {
        return chavePixService.cadastrarChavePix(chavePixRequest);
    }
}
