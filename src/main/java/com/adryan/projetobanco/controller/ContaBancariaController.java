package com.adryan.projetobanco.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adryan.projetobanco.dto.ContaBancariaRequest;
import com.adryan.projetobanco.dto.ContaBancariaResponse;
import com.adryan.projetobanco.service.ContaBancariaService;

@RestController
@RequestMapping("/conta")
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    public ContaBancariaController(ContaBancariaService contaBancariaService) {
        this.contaBancariaService = contaBancariaService;
    }

    @PostMapping("/deposito/{id}")
    public ContaBancariaResponse deposito(@PathVariable Long id,@RequestBody ContaBancariaRequest contaBancariaRequest) {

        return contaBancariaService.depositarPorClienteId(id,contaBancariaRequest.getValor());
    }


    @PostMapping("/saque/{id}")
    public ContaBancariaResponse saque(@PathVariable Long id, @RequestBody ContaBancariaRequest contaBancariaRequest){
        return contaBancariaService.sacarPorClienteId(id, contaBancariaRequest.getValor());
    }

}
