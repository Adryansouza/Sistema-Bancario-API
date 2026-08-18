package com.adryan.projetobanco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Esta classe guarda os dados que todo cliente tem, tanto pessoa fisica quanto juridica.
@Getter
@Setter
@NoArgsConstructor
public abstract class Cliente {

    private Long id;
    private String nome;
    private String documento;
    private String telefone;
    private String endereco;
    @JsonIgnore
    private String senha;
    private String uf;

    private ContaBancaria conta = new ContaBancaria();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public ContaBancaria getConta() {
        return conta;
    }

    public void setConta(ContaBancaria conta) {
        this.conta = conta;
    }

    public abstract String getTipoCliente();

    public void setTipoCliente(String tipoCliente) {
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
