package com.projeto.microservices.rotas;



import org.springframework.http.ResponseEntity;
import com.projeto.microservices.metodos.Metodos;
import com.projeto.microservices.entidades.Endereco;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// PDF
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import com.itextpdf.kernel.pdf.PdfReader;
import org.springframework.http.MediaType;
import java.time.format.DateTimeFormatter;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.springframework.http.HttpHeaders;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;


// Redis
import org.springframework.data.redis.core.RedisTemplate;

// Feriados
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.projeto.microservices.entidades.Lista;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping(value = "/")
public class Rotas {

    //Metodos
    @Autowired
    private Metodos metodo;


    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public Rotas(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
        
    // Atividade 1 (Endpoint HelloWorld)

    @GetMapping(value = "/hello")
    public ResponseEntity<String>getHello(){
        String hello = "Hello World";
        return ResponseEntity.ok(hello);
    }


    // Atividade 2 (CEP)

    @GetMapping(value = "endereco/{cep}")
    public ResponseEntity<Endereco>getEnd(@PathVariable String cep){
        Endereco end = metodo.getEndereco(cep);
        return ResponseEntity.ok(end);
    }

    // Atividade 3 (Token JWT)

    @GetMapping(value = "jwt/token")
    public ResponseEntity<String>token(){
        String token = metodo.getAuth();

        return ResponseEntity.ok(token);
    }

    @GetMapping(value = "jwt/valida/{token}")
    public ResponseEntity<String>validaToken(@PathVariable String token){
        String confirmado = metodo.validaAuth(token);

        return ResponseEntity.ok(confirmado);
    }

    // Atividade 4 - 1 (Gerar PDF)

    @GetMapping("/gerar-pdf")
    public ResponseEntity<byte[]> generatePDF() {
        byte[] pdfContents = metodo.generatePDF();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfContents.length);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documento.pdf");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfContents);
    }

    // Atividade 4 - 2 (Ler PDF)

    @GetMapping("/ler-pdf")
    public ResponseEntity<String> LerPDF(){
        // Caminho para o arquivo PDF que será lido
        String caminhoArquivo = "C://Users//mvbra//Downloads//documento (1).pdf";
        String textoPagina  = "";
        // Inicializa o leitor de PDF do iText
        try (PdfReader reader = new PdfReader(new File(caminhoArquivo));
             PdfDocument pdfDocument = new PdfDocument(reader)) {

            int numPaginas = pdfDocument.getNumberOfPages();

            // Itera através de todas as páginas e extrai o texto
            for (int i = 1; i <= numPaginas; i++) {
                textoPagina = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i));
                System.out.println("Conteúdo da página " + i + ":\n" + textoPagina);
            }
            return ResponseEntity.ok(textoPagina.toString());
        } catch (IOException e) {
            return ResponseEntity.ok("Ocorreu um erro ao ler o arquivo PDF");
        }
    }   
    
    // Atividade 5 - 1 (Armazenar dados Redis)

    @GetMapping(value = "/armazenar/{chave}/{valor}")
    public ResponseEntity<String>armazenarInfo(@PathVariable String chave,@PathVariable String valor){
        try {
            redisTemplate.opsForValue().set(chave, valor);     
            return ResponseEntity.ok("Inserido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.ok("Erro ao inserir");
        }
        
    }


    // Atividade 5 - 2 (Recuperar dados Redis)

    @GetMapping(value = "/recuperar/{chave}")
    public ResponseEntity<String>recuperarInfo(@PathVariable String chave){
        return ResponseEntity.ok(redisTemplate.opsForValue().get(chave));
    }


    // Atividade 6 (Swagger)  http://localhost:8080/swagger-ui/index.html

    // Atividade 7 - 1 - Feriados (Validar data)
    
    @GetMapping(value = "/validacaoData/{data}")
    public ResponseEntity<String>validaData(@PathVariable String data){

        // http://localhost:8080/validacaoData/{data}
        // http://localhost:8080/validacaoData/23-10-2024

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {

            Date date = dateFormat.parse(data);
            data = data.replace('-', '/');

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataEnviada = LocalDate.parse(data, formatter);
            LocalDate dataAtual = LocalDate.now();

            if (dataEnviada.isBefore(dataAtual)) {
                //return ResponseEntity.ok("A data enviada é anterior à data atual.");
                return ResponseEntity.ok("Data Inválida");
            } else if (dataEnviada.isAfter(dataAtual)) {
                //return ResponseEntity.ok("A data enviada é posterior à data atual.");
                return ResponseEntity.ok(data);
            } else {
                //return ResponseEntity.ok("A data enviada é a mesma que à data atual.");
                return ResponseEntity.ok("Data Inválida");
            }
            
        } catch (ParseException e) {
            return ResponseEntity.ok("Data Inválida");
        }

    }
  
    // Atividade 7 - 2 - Feriados (Incluir data)

    @GetMapping(value = "/feriado/incluir/{descricao}/{data}") 
    public ResponseEntity<String>incluirFeriado(@PathVariable String descricao,@PathVariable String data){
        
        // http://localhost:8080/feriado/incluir/{Descrição}/{Data do Feriado}
        // http://localhost:8080/feriado/incluir/Natal/25-12-2025

        String resposta = validaData(data).toString();
        String[] parts = resposta.split(",");
        resposta = parts[1];
        
        if(resposta.equals("Data Inválida")){
            return ResponseEntity.ok(resposta);
        }else{

            if(redisTemplate.opsForValue().get(descricao) == null){
                redisTemplate.opsForValue().set(descricao, resposta);     
                return ResponseEntity.ok("O feriado: "+ descricao+ " foi adicionado na data: " +resposta + " com sucesso!");  
            }else{
                return ResponseEntity.ok("Registro já existente!");
            }
        }
    
    }

    // Atividade 7 - 3 - Feriados (Consultar data)

    @GetMapping(value = "/feriado/recuperar/{descricao}")
    public ResponseEntity<String>recuperarFeriado(@PathVariable String descricao){

        // http://localhost:8080/feriado/recuperar/{descrição}
        // http://localhost:8080/feriado/recuperar/AnoNovo

        if(redisTemplate.opsForValue().get(descricao) == null){
            return ResponseEntity.ok("Registro não existente!");  
        }else{
            return ResponseEntity.ok(redisTemplate.opsForValue().get(descricao));
        }
        
    }

    // Atividade 7 - 4 - Feriados (Alterar dados feriado)

    @GetMapping(value = "/feriado/editar/{descricao}/{data}") 
    public ResponseEntity<String>editarFeriado(@PathVariable String descricao,@PathVariable String data){
        
        // http://localhost:8080/feriado/editar/{Descrição}/{Data do Feriado}
        // http://localhost:8080/feriado/editar/AnoNovo/25-10-2024

        String dataA = "";
        String resposta = validaData(data).toString();
        String[] parts = resposta.split(",");
        resposta = parts[1];
        
        if(resposta.equals("Data Inválida")){
            return ResponseEntity.ok(resposta);
        }else{

            if(redisTemplate.opsForValue().get(descricao) == null){
                return ResponseEntity.ok("Registro não existente!");  
            }else{
                dataA = redisTemplate.opsForValue().get(descricao); 
                if(dataA.equals(resposta)){
                    return ResponseEntity.ok("Esta data já consta no feriado em questão."); 
                }else{
                    redisTemplate.opsForValue().set(descricao, resposta);     
                    return ResponseEntity.ok(descricao+ " foi editado com sucesso.\nData anterior: " +dataA+  "\nData atualizada: "+resposta); 
                }
            }
        }
    
    }

    // Atividade 7 - 5 - Feriados (Excluir feriado)

    @GetMapping(value = "/feriado/excluir/{descricao}") 
    public ResponseEntity<String>excluirFeriado(@PathVariable String descricao){
        
        // http://localhost:8080/feriado/excluir/{Descrição}
        // http://localhost:8080/feriado/excluir/AnoNovo

        if(redisTemplate.opsForValue().get(descricao) == null){
            return ResponseEntity.ok("Registro não existente!");  
        }else{
            redisTemplate.delete(descricao);
            return ResponseEntity.ok("Feriado excluído com sucesso!");
        }
    
    }

    // Atividade 7 - 6 - Feriados (Listar feriados)

    @GetMapping(value = "/feriado/lista") 
    public ResponseEntity<List<Lista>> listaFeriados() {

        // http://localhost:8080/feriado/lista
        
        List<Lista> lista = new ArrayList<>();

        // Obtém todas as chaves
        for (String chave : redisTemplate.keys("*")) {
            // Obtém o valor associado à chave
            String valor = redisTemplate.opsForValue().get(chave);

            // Adiciona à lista
            lista.add(new Lista(chave, valor));
        }

        if(lista.isEmpty()){
            lista.add(new Lista("Lista", "vazia"));
            return ResponseEntity.ok(lista);
        }else{
            return ResponseEntity.ok(lista);
        }
    }

    // Atividade 7 - 7 - Feriados (Listar feriados PDF)

    @GetMapping("/feriado/listaPDF")
    public ResponseEntity<byte[]> generatePDFs() {

        // http://localhost:8080/feriado/listaPDF

        ResponseEntity<List<Lista>> resposta = listaFeriados();
        List<Lista> minhaLista = resposta.getBody();
        byte[] pdfContents = metodo.generatePDFList(minhaLista);
        

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfContents.length);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documento.pdf");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfContents);
    }

}

