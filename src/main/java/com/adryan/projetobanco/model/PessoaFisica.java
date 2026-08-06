package com.adryan.projetobanco.model;

public class PessoaFisica extends Cliente {

    private String cpf;
    private String uf;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

   
    

    @Override
    public String getTipoCliente() {
        return "FISICA";
    }
    
    public void setUf(String uf){
        this.uf = uf;
    }

    public String getUf(){
        return uf;
    }

    

    
}
