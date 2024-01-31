package com.projeto.microservices.rotas;



import org.springframework.http.ResponseEntity;
import com.projeto.microservices.metodos.Metodos;
import com.projeto.microservices.entidades.DadosExcel;
import com.projeto.microservices.entidades.Endereco;
import com.projeto.microservices.entidades.Layout;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
// PDF
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.projeto.microservices.entidades.Lista;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(value = "/")
public class Rotas {

    //Metodos
    @Autowired
    private Metodos metodo;


    private final RedisTemplate<String, String> redisTemplate;

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

    @GetMapping(value = "/gerar-pdf")
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

    @GetMapping(value = "/ler-pdf")
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

    @GetMapping(value = "/feriado/listaPDF")
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

    @GetMapping(value = "/layout/{token}")
    public ResponseEntity<String>validaLayout(@PathVariable String token){ 

        Layout lay = new Layout();

        if(token.length() >= 31){

            lay.setCNPJ(token.substring(0,9)); 
            lay.setFilialPublico(token.substring(9, 13));
            lay.setDigitoCNPJ(token.substring(13, 15)); 
            lay.setCNPJeconomico(token.substring(15,24));
            lay.setFilialEconomico(token.substring(24, 28));
            lay.setDigitoCNPJeconomico(token.substring(28, 30));
            lay.setIndicador(token.substring(30, 31)); 
             
            if(lay.getIndicador().equals("2")){ 
 
                if(token.length() == 39){

                    //123456789123412123456789123412212345678 
                    
                    lay.setCodigoProduto(token.substring(31, 39)); 
                    return ResponseEntity.ok(lay.toString()); 
                }
                else{
                    int aux = token.length();
                    return ResponseEntity.ok("Erro" + aux + ": "+token.substring(0, 39));  
                }
                
            }else{ 
                
                //12345678912341212345678912341212310202423102025231020262310202712345678

                lay.setDataInicio(token.substring(31, 33)+"-"+ token.substring(33, 35)+"-"+ token.substring(35, 39)); 
        
                lay.setDataFim(token.substring(39, 41)+"-"+ token.substring(41, 43)+"-"+ token.substring(43, 47)); 
             
                lay.setDataAss(token.substring(47, 49)+"-"+ token.substring(49, 51)+"-"+ token.substring(51, 55)); 
               
                lay.setDataLici(token.substring(55, 57)+"-"+ token.substring(57, 59)+"-"+ token.substring(59, 63)); 
               
                String vet [] = {validaData(lay.getDataInicio()).getBody().split(",")[0],validaData(lay.getDataFim()).getBody().split(",")[0],
                    validaData(lay.getDataAss()).getBody().split(",")[0],validaData(lay.getDataLici()).getBody().split(",")[0]};
                
                int invalid = 0;    

                for (String str : vet) {
                    if (str.equals("Data Inválida")) {
                        invalid++;
                    } 
                }
 
                if(invalid == 0){
                    lay.setCodigoProduto(token.substring(63, 71));
                    return ResponseEntity.ok(lay.toString());     
                }else{
                    return ResponseEntity.ok("Invalida");    
                }

            }
        }else{ 
            return ResponseEntity.ok("Tamanho incorreto"); 
        }
    }



    @GetMapping(value = "/lerArquivoWeb")
    public ResponseEntity<String> lerBlocoWeb() {

        String caminhoDoArquivo = "C://Users//mvbra//Downloads//Arq.Importacao";

        Path path = Paths.get(caminhoDoArquivo);

        try {
            byte[] dadosDoArquivo = Files.readAllBytes(path);

            // Lógica para processar os dados do arquivo binário aqui
            // Exemplo: Transformar os bytes em uma String
            String resultado = new String(dadosDoArquivo);

            // Retornar o resultado na resposta da requisição
            return ResponseEntity.ok(resultado);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao ler o arquivo binário");
        }
    }

    @GetMapping(value = "/importacao")
    public ResponseEntity<String> importacaoExcel() {

        String caminhoArquivo = "C://Users//mvbra//Downloads//teste.txt";
        String caminhoExcel = "C://Users//mvbra//Downloads//exemplo_simples.xlsx";

        try {

            List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo));
            
            
           
            Boolean validado = true;

            // Validando data

            for(int i = 0; i < linhas.size();i++){
                if(linhas.get(i).substring(0, 2).equals("01")){

                    validado = validaData(linhas.get(i).substring(2)).getBody().equals("Data Inválida");

                    if (validado == true){
                        return ResponseEntity.ok("Data inválida");    
                    }
                }  
            }
            
            // Validando codigos

            for(int i = 0; i < linhas.size();i++){
                if(linhas.get(i).substring(0,2).equals("10")){

                    if (linhas.get(i).length() != 12){
                        return ResponseEntity.ok("Codigo inválido");    
                    }
                }
                 
            }

            // Validando ultima linha

            for(int i = 0; i < linhas.size();i++){
                if(linhas.get(i).substring(0, 2).equals("99")){

                    if (Character.getNumericValue(linhas.get(i).charAt(linhas.get(i).length()-1)) != linhas.size()){
                        return ResponseEntity.ok("Quantidade inválida | Linhas: " + linhas.size() + "\n Caracteres: " + linhas.get(i).charAt(linhas.get(i).length()-1));    
                    }
                }
                 
            }

            //Criando Lista de dados com as linhas

            List<DadosExcel> dado = new ArrayList<>();
            
            int a = 0;
            int b = 0;


            //Adicionando os dados na lista

            for(a = 0; a < linhas.size();a++){
                dado.add( new DadosExcel (linhas.get(a).substring(0, 2),linhas.get(a).substring(2)));                 
            }

            try(Workbook workbook = new XSSFWorkbook()){

            
                //Nome na planilha 

                Sheet sheet = workbook.createSheet("Planilha Dados2");

                // Cabeçalho do Excel
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Tipo", "Dados"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                // Dados
                int rowNum = 1;
                for (DadosExcel dados : dado) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dados.getTipo());
                    row.createCell(1).setCellValue(dados.getDados());
                }

                // Salvar o arquivo
                try (FileOutputStream fileOut = new FileOutputStream(caminhoExcel)) {
                    workbook.write(fileOut);
                    System.out.println("Arquivo Excel criado com sucesso!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            

            return ResponseEntity.ok("Criado com sucesso!");     


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("Não foi possivel a criação do Excel");
        }
    } 
}


