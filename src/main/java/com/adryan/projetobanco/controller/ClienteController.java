package com.adryan.projetobanco.controller;

import com.adryan.projetobanco.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.PessoaFisica;
import com.adryan.projetobanco.model.PessoaJuridica;
import com.adryan.projetobanco.service.ClienteService;
import com.adryan.projetobanco.service.CadastroService;

@RestController
@RequestMapping("/clientes")//para criar um parametro no endpoint ex: http://localhost:8080/clientes/...
public class ClienteController {


    //cadastro inicio
    @Autowired
    private CadastroService cadastroService;


    @PostMapping("cadastro/pf")
    public void cadastroPf(@RequestBody PessoaFisica pessoaFisica) {
        cadastroService.cadastroPessoaFisica(pessoaFisica);
    }

    @PostMapping("cadastro/pj")
    public void cadastroPj(@RequestBody PessoaJuridica pessoaJuridica) {
        cadastroService.cadastroPessoaJuridica(pessoaJuridica);
    }
    //cadastro final


    //BUSCAR CLIENTE - pra ver meus dados
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/{id}")
    public Cliente buscarCliente(@PathVariable Long id) {
        return clienteService.buscarClientePorId(id);
    }

    //buscar cliente final






//    @PutMapping("/pf/{id}")
//    public void atualizarCadastroPf(
//            @PathVariable Long id,
//            @RequestBody PessoaFisica pessoaFisica) {
//
//        cadastroService.atualizarCadastro(id, pessoaFisica);
//    }
//
//    @PutMapping("/pj/{id}")
//    public void atualizarCadastroPj(
//            @PathVariable Long id,
//            @RequestBody PessoaJuridica pessoaJuridica) {
//
//        cadastroService.atualizarCadastro(id, pessoaJuridica);
//    }
}