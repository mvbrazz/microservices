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


import java.io.BufferedWriter;
// PDF
import java.io.File;

import java.io.FileOutputStream;
import java.io.FileWriter;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.projeto.microservices.entidades.Lista;
import com.projeto.microservices.entidades.Relatorio;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;



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

       
        data = data.replace('-', '/');

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataEnviada = LocalDate.parse(data, formatter);
        LocalDate dataAtual = LocalDate.now();

        if (dataEnviada.isBefore(dataAtual)) {
            //return ResponseEntity.ok("A data enviada é anterior à data atual.");
            return ResponseEntity.ok("Data Inválida");
        }else if (dataEnviada.isAfter(dataAtual)) {
            //return ResponseEntity.ok("A data enviada é posterior à data atual.");
            return ResponseEntity.ok(data);
        }else {
            //return ResponseEntity.ok("A data enviada é a mesma que à data atual.");
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

    // Atividade 9 - Layout

    @GetMapping(value = "/layout/{token}")
    public ResponseEntity<String>validaLayout(@PathVariable String token){ 

        Layout lay = new Layout();
        // Verificando se tem no minimo 31 caracteres
        if(token.length() >= 31){

            lay.setCNPJ(token.substring(0,9)); 
            lay.setFilialPublico(token.substring(9, 13));
            lay.setDigitoCNPJ(token.substring(13, 15)); 
            lay.setCNPJeconomico(token.substring(15,24));
            lay.setFilialEconomico(token.substring(24, 28));
            lay.setDigitoCNPJeconomico(token.substring(28, 30));
            lay.setIndicador(token.substring(30, 31)); 
             
            if(lay.getIndicador().equals("2")){ 
 
                // Se tiver 39, indicador 2 e sem datas
                if(token.length() == 39){

                    //123456789123412123456789123412212345678 
                    
                    lay.setCodigoProduto(token.substring(31, 39)); 
                    return ResponseEntity.ok(lay.toString()); 
                }
                else{
                    int aux = token.length();
                    return ResponseEntity.ok("Erro" + aux + ": "+token.substring(0, 39));  
                }
                
            } // Se tiver 71, indicador 1 e com datas verificadas
            else if(token.length() == 71){ 
                
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

            }else{
                return ResponseEntity.ok("Tamanho incorreto");
            }
        }else{ 
            return ResponseEntity.ok("Tamanho incorreto"); 
        }
    }

    @GetMapping(value = "/lerArquivoWeb")
    public ResponseEntity<String> lerBlocoWeb() {

        String caminhoDoArquivo = "C://Users//mvbra//Downloads//microservico//lerArquivoWeb//Arq.Importacao";

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

    // Arquivo Importação
    
    @GetMapping(value = "/importacao")
    public ResponseEntity<String> importacaoExcel() {

        String caminhoArquivo = "C://Users//mvbra//Downloads//microservico//importacao//dados.txt";
        String caminhoExcel = "C://Users//mvbra//Downloads//microservico//importacao//dados.xlsx";

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
                        return ResponseEntity.ok("Quantidade inválida\n Linhas: " + linhas.size() + "\n Linhas Informadas: " + linhas.get(i).charAt(linhas.get(i).length()-1));    
                    }
                }
                 
            }

            //Criando Lista de dados com as linhas

            List<DadosExcel> dado = new ArrayList<>();
            
            int contador = 0;
  
            //Adicionando os dados na lista

            for(contador = 0; contador < linhas.size();contador++){
                dado.add( new DadosExcel (linhas.get(contador).substring(0, 2),linhas.get(contador).substring(2)));                 
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

    // Balance Line

    @GetMapping(value = "/balanceLineFuncionario")
    public ResponseEntity<String> balanceFuncionario() {

        // Caminhos para ler e criar os documentos

        String PrimeiroArquivo = "C://Users//mvbra//Downloads//microservico//balanceLineFuncionario//Arquivo1.txt";
        String SegundoArquivo = "C://Users//mvbra//Downloads//microservico//balanceLineFuncionario//Arquivo2.txt";
        String TerceiroArquivo = "C://Users//mvbra//Downloads//microservico//balanceLineFuncionario//Arquivo3.txt";

        // Lista de funcionario
        List<String> funcionarios = new ArrayList<>();

        try {

            // Lendo arquivos com os dados

            List<String> arquivo1 = Files.readAllLines(Paths.get(PrimeiroArquivo));
            List<String> arquivo2 = Files.readAllLines(Paths.get(SegundoArquivo));
                                    
            for(int i = 0; i < arquivo1.size();i++){
                
                String[] partes = arquivo1.get(i).split("(?=[A-Z])");

                // Verificando se está faltando dados como nome ou endereço

                if(partes.length == 3){
                    funcionarios.add(arquivo1.get(i));
                }

            }

            for (int i = 0; i < arquivo2.size(); i++) {

                String[] partes = arquivo2.get(i).split("(?=[A-Z])");
            
                if (partes.length == 3) {

                    boolean duplicado = false;
            
                    // Verificando todos os funcionarios existentes na lista de funcionarios

                    for (int j = 0; j < funcionarios.size(); j++) {
                        if (arquivo2.get(i).substring(0, 7).equals(funcionarios.get(j).substring(0, 7))) {
                            duplicado = true;
                            break;
                        }
                    }
            
                    // Adicionar à lista apenas se não existir um funcionario com o mesmo codigo de funcionario

                    if (!duplicado) {
                        funcionarios.add(arquivo2.get(i));
                    }
                }
            }

            // Ordenando a lista de funcionarios utilizando bubble sort

            for (int i = 0; i < funcionarios.size(); i++) {

                for(int j = i+1;j< funcionarios.size();j++){

                    if(Integer.parseInt(funcionarios.get(i).substring(0, 7)) > Integer.parseInt(funcionarios.get(j).substring(0, 7))){
                        String auxiliar = funcionarios.get(i);
                        
                        funcionarios.set(i, funcionarios.get(j));
                        funcionarios.set(j, auxiliar);
                    }
                }
                
            }

            return ResponseEntity.ok(metodo.criarBlocoDeNotas(TerceiroArquivo, funcionarios)); 
               
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.ok("Não foi possivel criar o arquivo");
            
        }
    } 

    @GetMapping(value = "/balanceLineCliente")
    public ResponseEntity<String> balanceCliente() {

        // Caminhos para ler e criar os documentos

        String PrimeiroArquivo = "C://Users//mvbra//Downloads//microservico//balanceLineCliente//mestre.txt";
        String SegundoArquivo = "C://Users//mvbra//Downloads//microservico//balanceLineCliente//detalhe.txt";
        String TerceiroArquivo = "C://Users//mvbra//Downloads//microservico//balanceLineCliente//mestatu.txt";

        // Lista de funcionario
        List<String> cliente = new ArrayList<>();
        
        try {

            // Lendo arquivos com os dados

            List<String> arquivo1 = Files.readAllLines(Paths.get(PrimeiroArquivo));
            List<String> arquivo2 = Files.readAllLines(Paths.get(SegundoArquivo));
             
            // Adicionando mestre e verificando se tem 1 registro por cliente

            for(int i = 0; i < arquivo1.size();i++){

                int cont = 0;                            

                if(arquivo1.get(i).split("(?=[A-Z])").length == 4){

                    for(int j = 0; j < cliente.size();j++){
                        if(arquivo1.get(i).split("(?=[A-Z])")[0].equals(cliente.get(j).split("(?=[A-Z])")[0])){
                            cont++;
                        }
                    }
                    if(cont == 0){
                        cliente.add(arquivo1.get(i));
                    }
                }

            }

            // Dados detalhe
            
            List<String> frase = new ArrayList<>();
            
            for (int i = 0; i < arquivo2.size(); i++) {

                String comparador = arquivo2.get(i).substring(0,15);
                int cont = 0;
                
                for (int j = 0; j < arquivo2.size(); j++) {
                    if (arquivo2.get(j).substring(0,15).equals(comparador)) {
                        cont++;
                    }
                }

                if(cont < 10){ 
                    if (i < arquivo2.size() - 1 && !arquivo2.get(i).substring(0, 15).equals(arquivo2.get(i + 1).substring(0, 15)) || i == arquivo2.size()-1) {
                        if(arquivo2.get(i).split("(?=[A-Z])")[3].equals("D")){
                            frase.add(arquivo2.get(i).split("(?=[A-Z])")[0]+"S000"+(cont*2)+arquivo2.get(i).split("(?=[A-Z])")[2]+"");    
                        }else{
                            frase.add(arquivo2.get(i).split("(?=[A-Z])")[0]+"S000"+((cont*2)-1)+arquivo2.get(i).split("(?=[A-Z])")[2]+""); 
                        }
                    }

                }
                  
            }

            //Verificando se contém mestre valido
            
            int cont = 0;

            for (int i = 0; i < frase.size(); i++) {

                String elementoAtual = frase.get(i).split("(?=[A-Z])")[0];  

                for (int j = 0; j < arquivo1.size(); j++) {
                    if (elementoAtual.equals(arquivo1.get(j).split("(?=[A-Z])")[0])) {
                        cont++; 
                    }
                }

                
            }

            if (frase.size() != cont) {

                return ResponseEntity.ok("Conteúdo sem mestre");

            } else {

                // Criando o arquivo mestatu

                String conteudo = "----- Conteúdos atualizados -----\n\n";
   
                for (int i = 0; i < cliente.size(); i++) {
                    String[] partesCliente = cliente.get(i).split("(?=[A-Z])");

                    for (int j = 0; j < frase.size(); j++) {

                        String[] partesFrase = frase.get(j).split("(?=[A-Z])");

                        if (partesFrase[0].split("(?=[A-Z])")[0].equals(partesCliente[0].split("(?=[A-Z])")[0])) {
                            conteudo += partesCliente[0] + partesCliente[1] + partesFrase[1] + partesFrase[2] +"\n";
                        }
                        
                    }
                }

                conteudo += "\n\n----- Conteúdos antigos -----\n\n";

                for (int i = 0; i < arquivo1.size(); i++) {
                   
                    conteudo += arquivo1.get(i) + "\n";
                        
                }

                try {
                    // Cria um objeto BufferedWriter para escrever no arquivo
                    BufferedWriter writer = new BufferedWriter(new FileWriter(TerceiroArquivo));
    
                    // Escreve o conteúdo no arquivo
                    writer.write(conteudo);
    
                    // Fecha o BufferedWriter para garantir que todos os dados sejam gravados
                    writer.close();
    
                } catch (IOException e) {
                    System.err.println("Erro ao criar o arquivo mestatu: " + e.getMessage());
                }

                // Atualizando o campo SDANT 

                ArrayList<String> ListaConteudo2 = new ArrayList<>();

                for (int i = 0; i < arquivo1.size(); i++) {
                    String[] partesArquivo1 = arquivo1.get(i).split("(?=[A-Z])");   
                    for (int j = 0; j < frase.size(); j++) {
                        String[] partesFrase = frase.get(j).split("(?=[A-Z])");
    
                        if (partesFrase[0].split("(?=[A-Z])")[0].equals(partesArquivo1[0].split("(?=[A-Z])")[0])) {
                            ListaConteudo2.add( partesArquivo1[0] + partesArquivo1[1] + partesFrase[1] + partesFrase[2]);
                        }
                    }
                }

                // Verificando se já contém 

                for (String elemento : arquivo1) {
                    String[] partes = elemento.split("(?=[A-Z])");
                    boolean encontrado = false;
                
                    for (String item : ListaConteudo2) {
                        String[] partes2 = item.split("(?=[A-Z])");
                        if (partes2[0].equals(partes[0])) {
                            encontrado = true;
                            break;
                        }
                    }
                
                    if (!encontrado) {
                        ListaConteudo2.add(elemento);
                    }
                }

                // Ordenando vetor
                
                Collections.sort(ListaConteudo2);

                String conteudo2 = "";

                for (String elemento : ListaConteudo2) {
                    conteudo2+= elemento+"\n";
                }

                // Arquivo mestre atualizado

                try {
                    // Cria um objeto BufferedWriter para escrever no arquivo
                    BufferedWriter writer = new BufferedWriter(new FileWriter(PrimeiroArquivo));
    
                    // Escreve o conteúdo no arquivo
                    writer.write(conteudo2);
    
                    // Fecha o BufferedWriter para garantir que todos os dados sejam gravados
                    writer.close();
    
                } catch (IOException e) {
                    System.err.println("Erro ao criar o arquivo mestre: " + e.getMessage());
                }

                return ResponseEntity.ok("Operação feita com sucesso");
            }

            
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.ok("Erro ao ler os arquivos."); 
            
        }
    } 

    // Relatório de Quebra
    
    @GetMapping(value = "/relatorio")
    public ResponseEntity<String> relatorio() {

        String CadFunc = "C://Users//mvbra//Downloads//microservico//relatorioQuebra//CadFunc.txt";
        String Relato = "C://Users//mvbra//Downloads//microservico//relatorioQuebra//Relato.txt";
        // Lista de funcionario
        
        List<Relatorio>  Rel = new ArrayList<>();

        try {

            // Lendo arquivos com os dados

            List<String> funcionario = Files.readAllLines(Paths.get(CadFunc));
            for(int i = 0; i< funcionario.size();i++){

                String SalarioB = funcionario.get(i).substring(17).split("(?<=\\D)(?=\\d)", 2)[1].substring(0,5);
                String desc = funcionario.get(i).substring(17).split("(?<=\\D)(?=\\d)", 2)[1].substring(5,9);

                Relatorio r = new Relatorio(funcionario.get(i).substring(0,3),funcionario.get(i).substring(3,7),
                                            funcionario.get(i).substring(7,10),funcionario.get(i).substring(10,17),
                                            funcionario.get(i).substring(17).split("(?<=\\D)(?=\\d)", 2)[0],SalarioB,
                                            funcionario.get(i).substring(17).split("(?<=\\D)(?=\\d)", 2)[1].substring(5,9),
                                            (Integer.parseInt(SalarioB)-Integer.parseInt(desc))
                );

                Rel.add(r);  
            }

            for (int i = 0; i < Rel.size() - 1; i++) {
                for (int j = 0; j < Rel.size() - i - 1; j++) {
                    if (Rel.get(j).getRegionalAsInt() > Rel.get(j + 1).getRegionalAsInt() ||
                        (Rel.get(j).getRegionalAsInt() == Rel.get(j + 1).getRegionalAsInt() && 
                         Rel.get(j).getAgenciaAsInt() > Rel.get(j + 1).getAgenciaAsInt()) ||
                        (Rel.get(j).getRegionalAsInt() == Rel.get(j + 1).getRegionalAsInt() && 
                         Rel.get(j).getAgenciaAsInt() == Rel.get(j + 1).getAgenciaAsInt() &&
                         Rel.get(j).getSecaoAsInt() > Rel.get(j + 1).getSecaoAsInt()) ||
                        (Rel.get(j).getRegionalAsInt() == Rel.get(j + 1).getRegionalAsInt() && 
                         Rel.get(j).getAgenciaAsInt() == Rel.get(j + 1).getAgenciaAsInt() &&
                         Rel.get(j).getSecaoAsInt() == Rel.get(j + 1).getSecaoAsInt() &&
                         Rel.get(j).getFuncionarioAsInt() > Rel.get(j + 1).getFuncionarioAsInt())) {
                        // troca Rel.get(j) e Rel.get(j+1)
                        Relatorio temp = Rel.get(j);
                        Rel.set(j, Rel.get(j + 1));
                        Rel.set(j + 1, temp);
                    }
                }
            }
            
            return ResponseEntity.ok(metodo.Relatorio(Relato, Rel));

        } catch (Exception e) {
            return ResponseEntity.ok("Erro"); 
        }
    }

}


