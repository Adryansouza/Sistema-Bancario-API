package com.adryan.projetobanco.model;

public class PessoaFisica extends Cliente {

    private String cpf;
    private String age;
    private String address;
    private String uf;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String getTipoCliente() {
        return "FISICA";
    }

    public String getUf(){
        return uf;
    }

    

    
}
