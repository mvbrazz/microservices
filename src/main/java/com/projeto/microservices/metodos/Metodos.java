package com.projeto.microservices.metodos;

import java.util.Map;
import java.time.Instant;
import java.util.HashMap;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.projeto.microservices.entidades.Endereco;
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

    
    
}
