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
@RequestMapping("/clientes") // para criar um parametro no endpoint ex: http://localhost:8080/clientes/...
public class ClienteController {

    // cadastro inicio
    @Autowired
    private CadastroService cadastroService;

    @PostMapping("/cadastro/pf")
    public PessoaFisica cadastroPf(@RequestBody PessoaFisica pessoaFisica) {

        return cadastroService.cadastrarPessoaFisica(pessoaFisica);
    }

    @PostMapping("cadastro/pj")
    public PessoaJuridica cadastroPj(@RequestBody PessoaJuridica pessoaJuridica) {
        return cadastroService.cadastrarPessoaJuridica(pessoaJuridica);
    }
    // cadastro final

    // BUSCAR CLIENTE - pra ver meus dados
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/{id}")
    public Cliente buscarCliente(@PathVariable Long id) {
        return clienteService.buscarClientePorId(id);
    }
    // buscar cliente final

    // atualizar cadastro inicio

    @PatchMapping("/pf/{id}")
    public PessoaFisica atualizarCadastroPf(@PathVariable Long id,@RequestBody PessoaFisica pessoaFisica) {

        return cadastroService.atualizarCadastroPf(id, pessoaFisica);
    }

    @PatchMapping("/pj/{id}")
    public PessoaJuridica atualizarCadastroPj(@PathVariable Long id,@RequestBody PessoaJuridica pessoaJuridica) {

        return cadastroService.atualizarCadastroPj(id, pessoaJuridica);
    }

    // atualizar cadastro final

}
