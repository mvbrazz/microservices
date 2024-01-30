package com.projeto.microservices.metodos;

import java.util.Map;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.projeto.microservices.entidades.Endereco;
import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

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
    
}
