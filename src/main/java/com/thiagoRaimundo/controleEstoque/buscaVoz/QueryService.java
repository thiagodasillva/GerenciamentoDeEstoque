package com.thiagoRaimundo.controleEstoque.buscaVoz;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    private final GeminiAssistendService geminyAssistendService;
    private final SqlValidator sqlValidator;
    private final JdbcTemplate jdbcTemplate;

    public QueryService(GeminiAssistendService geminyAssistendService, SqlValidator sqlValidator, JdbcTemplate jdbcTemplate) {
        this.geminyAssistendService = geminyAssistendService;
        this.sqlValidator = sqlValidator;
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Map<String, Object>> executarPergunta(String pergunta) {
        System.out.println(pergunta);
        String sql = geminyAssistendService.gerarSql(pergunta);
        System.out.println("SQL gerado: " + sql);
        sqlValidator.validateAndSanitize(sql);

        List<Map<String, Object>> resultadoBruto = jdbcTemplate.queryForList(sql);
        return formatarChavesParaExibicao(resultadoBruto);
    }


    private List<Map<String, Object>> formatarChavesParaExibicao(List<Map<String, Object>> resultadoBruto) {
        List<Map<String, Object>> resultadoFormatado = new ArrayList<>();

        for (Map<String, Object> linha : resultadoBruto) {
            Map<String, Object> novaLinha = new LinkedHashMap<>();

            for (Map.Entry<String, Object> coluna : linha.entrySet()) {
                String chaveAmigavel = coluna.getKey()
                        .replace("_", " ")
                        .trim();

                novaLinha.put(chaveAmigavel, coluna.getValue());
            }
            resultadoFormatado.add(novaLinha);
        }

        return resultadoFormatado;
    }


}
