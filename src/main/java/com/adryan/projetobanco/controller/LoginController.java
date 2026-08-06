package com.adryan.projetobanco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.adryan.projetobanco.dto.LoginRequest;

import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.service.ClienteService;


@RestController

@RequestMapping("/login")
public class LoginController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/pf")
    public Cliente login(@RequestBody LoginRequest loginRequest) {
        
        return clienteService.login(loginRequest);
    }
}    