package com.projeto.microservices.entidades;

public class DadosExcel {
    private String tipo;
    private String dados;
    
    public DadosExcel() {
    }

    public DadosExcel(String tipo, String dados) {
        this.tipo = tipo;
        this.dados = dados;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDados() {
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
    }

}
