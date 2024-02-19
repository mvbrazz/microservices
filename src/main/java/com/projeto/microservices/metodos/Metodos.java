package com.projeto.microservices.metodos;

import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.projeto.microservices.entidades.Endereco;
import com.projeto.microservices.entidades.Lista;
import com.projeto.microservices.entidades.Relatorio;

import org.springframework.beans.factory.annotation.Autowired;

//JWT
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

// PDF
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.source.ByteArrayOutputStream;



@Service
public class Metodos {

    @Autowired
    private RestTemplate restTemplate;

    // Uso Api externa - VIA CEP

    public Endereco getEndereco(String cep){

        Map<String,String>variavel  = new HashMap<>();
        variavel.put("CEP",""+cep);

        Endereco end = restTemplate.getForObject("https://viacep.com.br/ws/{CEP}/json/",Endereco.class,variavel);
        return end;
    }

    // Criação do token JWT

    public String getAuth(){
        try {
            // Algoritmo com base na palava security
            Algorithm alg = Algorithm.HMAC256("security");
            // Criando token
            String token = JWT.create()
                .withIssuer("Auth-api")
                //.withSubject(api())
                .withExpiresAt(genExpirated())
                .sign(alg);

            return token;    

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro token", exception);
        }
        
    }

    // Validação do token JWT

    public String validaAuth(String token){
        try {
            // Algoritmo com base na palava security
            Algorithm alg = Algorithm.HMAC256("security");
             JWT.require(alg)
                .withIssuer("Auth-api")
                .build()
                .verify(token)
                .getSubject();
            return "Correto";

                

        } catch (JWTVerificationException exception) {
            return "Incorreto";
        }
        
    }

    private Instant genExpirated(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }


    // Gera PDF

    public byte[] generatePDF() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Conteúdo que você deseja no PDF
            document.add(new Paragraph("Teste Geração PDF"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    // Gera Lista de feriados PDF

    public byte[] generatePDFList(List<Lista> dados) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
    
            // Adiciona cada item da lista como um parágrafo no PDF
            for (Lista item : dados) {
                // Substitua "getCampoDesejado()" pelo método ou campo desejado da classe Lista
                document.add(new Paragraph(item.getChave()+": "+item.getValor()));
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return outputStream.toByteArray();
    }

    public String criarBlocoDeNotas(String caminhoArquivo, List<String> dados) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Escrever os dados no arquivo
            for(int i = 0 ; i< dados.size();i++){
                writer.write(dados.get(i)+"\n");     
            }
            return ("Bloco de notas criado com sucesso.");  
        } catch (IOException e) {
            return ("Erro ao criar o bloco de notas: " + e.getMessage());
        }
    }

    public String Relatorio(String caminhoArquivo, List<Relatorio> dados){

        boolean cabecalho = true;
        int pagina = 1;
        int secao = 0;
        int agencia = 0;
        int regiao = 0;
        int salario = 0;
        int reg = 0;
        int linhas = 5;
        int regTotal = 0;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {

            for(int i = 0; i < dados.size();i++){

                if(cabecalho){   // Cabeçalho

                    writer.write("" + LocalDate.now() + "     RELAÇÃO DO PAGAMENTO DOS FUNCIONÁRIOS - REG " + dados.get(i).getRegional() + "  PAG "+pagina+"\n\n" +
                    "       AGENCIA - " + dados.get(i).getAgencia() + "             SEÇÃO - " + dados.get(i).getSecao() + "\n" +
                    "   FUNCIONARIO         NOME           SALDO LIQUIDO\n\n" +
                    "    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "           " + dados.get(i).getSalLiquido() + "\n");
                    cabecalho = false;
                    secao = dados.get(i).getSecaoAsInt();
                    agencia = dados.get(i).getAgenciaAsInt();
                    salario = dados.get(i).getSalLiquido();
                    regiao = dados.get(i).getRegionalAsInt();
                    reg = dados.get(i).getSalLiquido();
                    
                }else{
                    // Região diferente
                    if(regiao != dados.get(i).getRegionalAsInt()){

                        pagina++;
                        
                        writer.write("\n TOTAL DA REGIONAL "+regiao+"          -       "+reg+   "\n");
                        regTotal += reg;
                        // Atualizar variáveis para a próxima agência
                        reg = 0;
                        agencia = dados.get(i).getAgenciaAsInt();
                        secao = dados.get(i).getSecaoAsInt();
                        regiao = dados.get(i).getRegionalAsInt();
                        salario = 0; // Resetar o total do salário para a nova agência

                        // Começar o cabeçalho da nova agência
                        writer.write("\n" + LocalDate.now() + "     RELAÇÃO DO PAGAMENTO DOS FUNCIONÁRIOS * - REG " + dados.get(i).getRegional() + "  PAG "+pagina+"\n\n" +
                        "       AGENCIA - " + dados.get(i).getAgencia() + "             SEÇÃO - " + dados.get(i).getSecao() + "\n" +
                        "   FUNCIONARIO         NOME           SALDO LIQUIDO\n\n" +
                        "    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "           " + dados.get(i).getSalLiquido() + "\n");

                        salario += dados.get(i).getSalLiquido(); // Adicionar o salário do primeiro funcionário da nova agência

                        cabecalho = false; // Atualizar a flag de cabeçalho
 

                    }// Se ainda for a mesma seção e agencia
                    else if(secao == dados.get(i).getSecaoAsInt() && agencia == dados.get(i).getAgenciaAsInt()){
                        linhas++;

                        // Se for o ultimo dado
                        if (i == dados.size()-1) {
                            salario += dados.get(i).getSalLiquido();
                            reg += dados.get(i).getSalLiquido();
                            regTotal += reg;

                            writer.write("    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "         " + dados.get(i).getSalLiquido() + "\n"); 
                            writer.write("\n TOTAL DA AGENCIA  "+agencia+"          -       "+salario+   "\n");
                            writer.write("\n TOTAL DA REGIONAL "+regiao+"          -       "+reg+   "\n");
                            writer.write("\n TOTAL GERAL DO BANCO          -       "+regTotal+   "\n");

                        }// Se tiver + de 9 linhas , quebra a página
                        else if(linhas > 9){ 
                            pagina++;
                            writer.write("\n" + LocalDate.now() + "     RELAÇÃO DO PAGAMENTO DOS FUNCIONÁRIOS # - REG " + dados.get(i).getRegional() + "  PAG "+pagina+"\n\n" +
                            "       AGENCIA - " + dados.get(i).getAgencia() + "             SEÇÃO - " + dados.get(i).getSecao() + "\n" +
                            "   FUNCIONARIO         NOME           SALDO LIQUIDO\n\n" +
                            "    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "           " + dados.get(i).getSalLiquido() + "\n");
                            salario += dados.get(i).getSalLiquido();
                            reg += dados.get(i).getSalLiquido(); 
                            linhas = 5;  
                        }// Se for a mesma agencia e seção, escreve os dados normalmente
                        else{
                            writer.write("    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "         " + dados.get(i).getSalLiquido() + "\n"); 
                            salario += dados.get(i).getSalLiquido();
                            reg += dados.get(i).getSalLiquido();
                        }
                        
                    }// Se mudar a agencia, mostrar o total acumulado
                    else if(agencia != dados.get(i).getAgenciaAsInt()){
                        linhas = 4;
                        pagina++;
                         // Escrever o total do salário da agência anterior
                        writer.write("\n TOTAL DA AGENCIA  "+agencia+"          -       "+salario+   "\n");

                        // Atualizar variáveis para a próxima agência
                        agencia = dados.get(i).getAgenciaAsInt();
                        secao = dados.get(i).getSecaoAsInt();
                        salario = 0; // Resetar o total do salário para a nova agência

                        // Começar o cabeçalho da nova agência
                        writer.write("\n" + LocalDate.now() + "     RELAÇÃO DO PAGAMENTO DOS FUNCIONÁRIOS / - REG " + dados.get(i).getRegional() + "  PAG "+pagina+"\n\n" +
                        "       AGENCIA - " + dados.get(i).getAgencia() + "             SEÇÃO - " + dados.get(i).getSecao() + "\n" +
                        "   FUNCIONARIO         NOME           SALDO LIQUIDO\n\n" +
                        "    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "           " + dados.get(i).getSalLiquido() + "\n");

                        salario += dados.get(i).getSalLiquido(); // Adicionar o salário do primeiro funcionário da nova agência
                        reg += dados.get(i).getSalLiquido();    // Adicionar o salário do primeiro funcionário na regional
                        cabecalho = false; // Atualizar a flag de cabeçalho
                        linhas++;
                    }
                    else if(secao !=dados.get(i).getSecaoAsInt()){ // Mudou seção
                        linhas = 4;
                        pagina++;
                        writer.write("\n" + LocalDate.now() + "     RELAÇÃO DO PAGAMENTO DOS FUNCIONÁRIOS + - REG " + dados.get(i).getRegional() + "  PAG "+pagina+"\n\n" +
                        "       AGENCIA - " + dados.get(i).getAgencia() + "             SEÇÃO - " + dados.get(i).getSecao() + "\n" +
                        "   FUNCIONARIO         NOME           SALDO LIQUIDO\n\n" +
                        "    " + dados.get(i).getFuncionario() + "        " + dados.get(i).getNome() + "           " + dados.get(i).getSalLiquido() + "\n");
                        cabecalho = false;
                        secao = dados.get(i).getSecaoAsInt();
                        agencia = dados.get(i).getAgenciaAsInt(); 
                        reg += dados.get(i).getSalLiquido();
                        linhas++;
                    }

                }                
    
            }

            // Fecha o documento
            writer.close();

            return ("Relatório criado com sucesso.");

        }
        catch (IOException e) {
            return("Erro ao criar o arquivo Relatório: " + e.getMessage());
        }
    
    }
    
    
}
