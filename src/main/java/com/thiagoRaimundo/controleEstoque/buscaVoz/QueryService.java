package com.thiagoRaimundo.controleEstoque.buscaVoz;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
        return jdbcTemplate.queryForList(sql);
    }


}
