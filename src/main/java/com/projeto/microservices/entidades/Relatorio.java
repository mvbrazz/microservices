package com.projeto.microservices.entidades;

public class Relatorio {
    
    private String regional;
    private String agencia;
    private String secao;
    private String funcionario;
    private String nome;
    private String salBruto;
    private String desconto;
    private Integer salLiquido;

    public Relatorio() {

    }

    public Relatorio(String regional, String agencia, String secao, String funcionario, String nome, String salBruto,
            String desconto, Integer salLiquido) {
        this.regional = regional;
        this.agencia = agencia;
        this.secao = secao;
        this.funcionario = funcionario;
        this.nome = nome;
        this.salBruto = salBruto;
        this.desconto = desconto;
        this.salLiquido = salLiquido;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getSecao() {
        return secao;
    }

    public void setSecao(String secao) {
        this.secao = secao;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSalBruto() {
        return salBruto;
    }

    public void setSalBruto(String salBruto) {
        this.salBruto = salBruto;
    }

    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        this.desconto = desconto;
    }

    public Integer getSalLiquido() {
        return salLiquido;
    }

    public void setSalLiquido(Integer salLiquido) {
        this.salLiquido = salLiquido;
    }

    public int getSecaoAsInt() {
        try {
            return Integer.parseInt(secao);
        } catch (NumberFormatException e) {
            // Lidar com a situação em que regional não é um número válido
            return 0; // Ou qualquer valor padrão que faça sentido para o seu caso
        }
    }

    public int getAgenciaAsInt() {
        try {
            return Integer.parseInt(agencia);
        } catch (NumberFormatException e) {
            // Lidar com a situação em que regional não é um número válido
            return 0; // Ou qualquer valor padrão que faça sentido para o seu caso
        }
    }

    public int getRegionalAsInt() {
        try {
            return Integer.parseInt(regional);
        } catch (NumberFormatException e) {
            // Lidar com a situação em que regional não é um número válido
            return 0; // Ou qualquer valor padrão que faça sentido para o seu caso
        }
    }

    public int getFuncionarioAsInt() {
        try {
            return Integer.parseInt(funcionario);
        } catch (NumberFormatException e) {
            // Lidar com a situação em que regional não é um número válido
            return 0; // Ou qualquer valor padrão que faça sentido para o seu caso
        }
    }
   
   

    @Override
    public String toString() {
        return "Relatorio [regional=" + regional + ", agencia=" + agencia + ", secao=" + secao + ", funcionario="
                + funcionario + ", nome=" + nome + ", salBruto=" + salBruto + ", desconto=" + desconto + ", salLiquido="
                + salLiquido + "]\n";
    }

    
}
