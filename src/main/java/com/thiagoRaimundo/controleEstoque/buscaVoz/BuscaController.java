package com.thiagoRaimundo.controleEstoque.buscaVoz;

import com.thiagoRaimundo.controleEstoque.buscaVoz.transcription.TranscriptionClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/busca")
@CrossOrigin(origins = "*")
public class BuscaController {

    private QueryService queryService;
    private TranscriptionClient transcriptionClient;

    public BuscaController(QueryService queryService, TranscriptionClient transcriptionClient) {
        this.queryService = queryService;
        this.transcriptionClient = transcriptionClient;
    }

    @PostMapping("/pergunta")
    public ResponseEntity<?> perguntar(@RequestBody String mensagem){
        try{
            List<Map<String, Object>> resultado = queryService.executarPergunta(mensagem);
            return ResponseEntity.ok(resultado);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping(value = "/perguntar/audio", consumes = "multipart/form-data")

    public ResponseEntity<?> perguntarComAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            // 1. Transcrever áudio usando o serviço Python
            String pergunta = transcriptionClient.transcrever(  audioFile.getBytes(), audioFile.getOriginalFilename());
            List<Map<String, Object>> resultado = queryService.executarPergunta(pergunta);
            return ResponseEntity.ok(resultado);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erro ao ler o arquivo de áudio");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro no processamento: " + e.getMessage());
        }
    }

}
