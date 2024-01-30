package com.projeto.microservices.rotas;



import org.springframework.http.ResponseEntity;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.projeto.microservices.entidades.Endereco;
import com.projeto.microservices.metodos.Metodos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//PDF
import java.io.File;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;


import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping(value = "/")
public class Rotas {

    //Metodos
    @Autowired
    private Metodos metodo;
        
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


   

}

