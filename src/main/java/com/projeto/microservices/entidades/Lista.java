package com.projeto.microservices.entidades;

public class Lista {
    private String chave;
    private String valor;

    public Lista(String chave, String valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public String getChave() {
        return chave;
    }

    public String getValor() {
        return valor;
    }
}