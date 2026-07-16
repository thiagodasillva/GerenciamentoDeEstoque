package com.thiagoRaimundo.controleEstoque.buscaVoz;

import com.thiagoRaimundo.controleEstoque.exceptions.BuscaForaDeEscopoException;
import com.thiagoRaimundo.controleEstoque.exceptions.ValidacaoQueryException;
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
                DIRETRIZ CRÍTICA DE ESCOPO:
                Avalie se a pergunta do usuário faz sentido e se está relacionada estritamente ao controle de estoque, produtos, categorias, lotes, vendas ou movimentações do esquema acima.
                Se a pergunta for incoerente, for uma saudação sem pergunta subsequente, ou se referir a assuntos totalmente alheios (ex: culinária, política, piadas, clima), você deve responder RIGOROSAMENTE apenas com a palavra: FORA_DE_ESCOPO
                Caso contrário, responda APENAS com o comando SQL válido (sem formatação markdown, sem explicações).
                               
                Pergunta do Usuário:
                "%s"
                """, schema, perguntaUsuario);

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


        String sqlGerado = limparSql(resposta);

        if ("FORA_DE_ESCOPO".equalsIgnoreCase(sqlGerado) || sqlGerado.isEmpty()){
            throw new BuscaForaDeEscopoException("Desculpe, ó consigo responder perguntas ligadas ao controle de estoque.");
        }

        return sqlGerado;
    }

    private String limparSql(String resposta) {
        return resposta.replaceAll("(?s)```sql\\s*(.*?)\\s*```", "$1")
                .replaceAll("(?s)```\\s*(.*?)\\s*```", "$1")
                .trim();
    }

    private String extrairTextoResposta(String json) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            JsonNode candidates = root.path("candidates");
            if(candidates.isMissingNode() || candidates.isEmpty()){
                throw new ValidacaoQueryException("A IA não conseguiu processas sua pergunta por motivos de segurança ou moderação");
            }

            return candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();}
        catch (ValidacaoQueryException e){
            throw e;
        }
        catch (Exception e){
            throw new RuntimeException("Falha ao ler os dados da inteligencia artificial", e);
        }



    }


}
