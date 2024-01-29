package com.projeto.microservices.rotas;



import org.springframework.http.ResponseEntity;
import com.projeto.microservices.entidades.Endereco;
import com.projeto.microservices.metodos.MetodosEndereco;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping(value = "/")
public class Rotas {

    @Autowired
    private MetodosEndereco metodo;

        
    // Atividade 1 (Endpoint HelloWorld)

    @GetMapping(value = "/hello")
    public ResponseEntity<String>getHello(){
        String hello = "Hello World";
        return ResponseEntity.ok(hello);
    }


    // Atividade 2 (CEP)
    
    @GetMapping(value = "/{cep}")
    public ResponseEntity<Endereco>getEnd(@PathVariable String cep){
        Endereco end = metodo.getEndereco(cep);
        return ResponseEntity.ok(end);
    }
   

}

