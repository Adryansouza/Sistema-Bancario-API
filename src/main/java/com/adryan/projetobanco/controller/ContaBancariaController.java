package com.adryan.projetobanco.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import com.adryan.projetobanco.dto.ContaBancariaRequest;
import com.adryan.projetobanco.dto.ContaBancariaResponse;
import com.adryan.projetobanco.service.ContaBancariaService;
import com.adryan.projetobanco.dto.TransacaoResponse;
import com.adryan.projetobanco.service.TransacaoService;

@RestController
@RequestMapping("/conta")
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;
    private final TransacaoService transacaoService;

    public ContaBancariaController(ContaBancariaService contaBancariaService, TransacaoService transacaoService) {
        this.contaBancariaService = contaBancariaService;
        this.transacaoService = transacaoService;
    }

    @PostMapping("/deposito/{id}")
    public ContaBancariaResponse deposito(@PathVariable Long id,@RequestBody ContaBancariaRequest contaBancariaRequest) {

        return contaBancariaService.depositarPorClienteId(id,contaBancariaRequest.getValor());
    }


    @PostMapping("/saque/{id}")
    public ContaBancariaResponse saque(@PathVariable Long id, @RequestBody ContaBancariaRequest contaBancariaRequest){
        return contaBancariaService.sacarPorClienteId(id, contaBancariaRequest.getValor());
    }

    @GetMapping("/{contaId}/transacoes")
    public List<TransacaoResponse> listarTransacoes(@PathVariable Long contaId) {
        return transacaoService.listarPorConta(contaId);
    }

}
