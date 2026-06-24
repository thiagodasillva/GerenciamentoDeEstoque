package com.thiagoRaimundo.controleEstoque.buscaVoz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GeminiAssistendService {

    @Value("${gemini.api.key}")
    private String key;
    @Value("${gemini.api.url}")
    private String apiURL;
    private final DatabaseSchemaProvider databaseSchemaProvider;
    private final WebClient webClient;

    public GeminiAssistendService(DatabaseSchemaProvider databaseSchemaProvider, WebClient webClient) {
        this.databaseSchemaProvider = databaseSchemaProvider;
        this.webClient = webClient;
    }

    public String gerarSql(String perguntaUsuario) {
        String schema = databaseSchemaProvider.getSchemaDescription();
        String prompt = String.format("""
            %s
            
            Com base no esquema acima, responda APENAS com o comando SQL (sem formatação markdown, sem explicações) para esta pergunta:
            "%s"
            """, schema, perguntaUsuario);

        // Chamada para o Gemini
        String resposta = webClient.post()
                .uri(apiURL + "?key=" + key)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "contents", List.of(Map.of(
                                "parts", List.of(Map.of("text", prompt))
                        ))
                ))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extrairTextoResposta)
                .block();

        return limparSql(resposta);
    }

    private String limparSql(String resposta) {
        return resposta.replaceAll("(?s)```sql\\s*(.*?)\\s*```", "$1")
                .replaceAll("(?s)```\\s*(.*?)\\s*```", "$1")
                .trim();
    }

    private String extrairTextoResposta(String json) {
        try{ // Parse simples (use Jackson) para extrair o texto do candidato
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();}
        catch (Exception e){
            throw new RuntimeException("Falha ao parsear resposta do Gemini", e);
        }



    }


}
