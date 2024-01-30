package com.projeto.microservices.entidades;

public class Layout {

    private String CNPJ; 
    private String filialPublico; 
    private String digitoCNPJ; 
    private String CNPJeconomico ; 
    private String filialEconomico; 
    private String digitoCNPJeconomico; 
    private String indicador; 
    private String codigoProduto; 
    private String dataInicio;    
    private String dataFim;
    private String dataAss; 
    private String dataLici;

    public Layout(){
    
    }

    public Layout(String cNPJ, String filialPublico, String digitoCNPJ, String cNPJeconomico, String filialEconomico,
            String digitoCNPJeconomico, String indicador, String codigoProduto, String dataInicio, String dataFim,
            String dataAss, String dataLici) {
        CNPJ = cNPJ;
        this.filialPublico = filialPublico;
        this.digitoCNPJ = digitoCNPJ;
        CNPJeconomico = cNPJeconomico;
        this.filialEconomico = filialEconomico;
        this.digitoCNPJeconomico = digitoCNPJeconomico;
        this.indicador = indicador;
        this.codigoProduto = codigoProduto;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataAss = dataAss;
        this.dataLici = dataLici;
    }



    public String getCNPJ() {
        return CNPJ;
    }
    public void setCNPJ(String cNPJ) {
        CNPJ = cNPJ;
    }
    public String getFilialPublico() {
        return filialPublico;
    }
    public void setFilialPublico(String filialPublico) {
        this.filialPublico = filialPublico;
    }
    public String getDigitoCNPJ() {
        return digitoCNPJ;
    }
    public void setDigitoCNPJ(String digitoCNPJ) {
        this.digitoCNPJ = digitoCNPJ;
    }
    public String getCNPJeconomico() {
        return CNPJeconomico;
    }
    public void setCNPJeconomico(String cNPJeconomico) {
        CNPJeconomico = cNPJeconomico;
    }
    public String getFilialEconomico() {
        return filialEconomico;
    }
    public void setFilialEconomico(String filialEconomico) {
        this.filialEconomico = filialEconomico;
    }
    public String getDigitoCNPJeconomico() {
        return digitoCNPJeconomico;
    }
    public void setDigitoCNPJeconomico(String digitoCNPJeconomico) {
        this.digitoCNPJeconomico = digitoCNPJeconomico;
    }
    public String getIndicador() {
        return indicador;
    }
    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }
    public String getCodigoProduto() {
        return codigoProduto;
    }
    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }
    public String getDataInicio() {
        return dataInicio;
    }
    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }
    public String getDataFim() {
        return dataFim;
    }
    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }
    public String getDataAss() {
        return dataAss;
    }
    public void setDataAss(String dataAss) {
        this.dataAss = dataAss;
    }
    public String getDataLici() {
        return dataLici;
    }
    public void setDataLici(String dataLici) {
        this.dataLici = dataLici;
    }

    @Override
    public String toString() {
        return "Layout : \nCNPJ=" + CNPJ + "\nfilialPublico=" + filialPublico + "\ndigitoCNPJ=" + digitoCNPJ
                + "\nCNPJeconomico=" + CNPJeconomico + "\nfilialEconomico=" + filialEconomico + "\ndigitoCNPJeconomico="
                + digitoCNPJeconomico + "\nindicador=" + indicador + "\ndataInicio=" + dataInicio + "\ndataFim=" + dataFim 
                + "\ndataAss=" + dataAss + "\ndataLici=" + dataLici + "\ncodigoProduto=" + codigoProduto;
    }

    

    
}
