package com.adryan.projetobanco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adryan.projetobanco.dto.TransferenciaPixRequest;
import com.adryan.projetobanco.dto.TransferenciaPixResponse;
import com.adryan.projetobanco.model.TransferenciaPix;
import com.adryan.projetobanco.service.TransferenciaPixService;

@RestController
@RequestMapping("/pix")

public class TransferenciaPixController {

        private final TransferenciaPixService transferenciaPixService;

        public TransferenciaPixController(TransferenciaPixService transferenciaPixService) {
            this.transferenciaPixService = transferenciaPixService;
        }

        @PostMapping()
        public TransferenciaPixResponse transferenciaPix (@RequestBody TransferenciaPixRequest transferenciaPixRequest){

            return transferenciaPixService.transferenciaPix(transferenciaPixRequest);

        }

}
