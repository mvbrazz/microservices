package com.projeto.microservices.rotas;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/")
public class Rotas {
    
    // Atividade 1 (Endpoint HelloWorld)

    @GetMapping(value = "/hello")
    public ResponseEntity<String>getHello(){
        String hello = "Hello World";
        return ResponseEntity.ok(hello);
    }

    
   

}

