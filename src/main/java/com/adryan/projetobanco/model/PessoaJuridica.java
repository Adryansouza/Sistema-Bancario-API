package com.adryan.projetobanco.model;

public class PessoaJuridica extends Cliente {

    private String cnpj;
    private String razaoSocial;
    private String uf;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    @Override
    public String getTipoCliente() {
        return "JURIDICA";
    }

    public void setUf(String uf){
        this.uf = uf;
    }

    public String getUf(){
        return uf;
    }
}
