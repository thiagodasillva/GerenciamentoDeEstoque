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
                String chaveOriginal = coluna.getKey();
                String chaveLower = chaveOriginal.toLowerCase();
                String chaveAmigavel;

                if (chaveLower.equals("coalesce")
                        || chaveLower.equals("case")
                        || chaveLower.equals("sum")
                        || chaveLower.equals("count")
                        || chaveLower.equals("round")
                        || chaveLower.equals("avg")
                        || chaveLower.contains("(")
                        || chaveLower.contains("?")) {
                    chaveAmigavel = "resultado";
                } else {
                    chaveAmigavel = chaveOriginal.replace("_", " ").trim();
                }

                // 2. FORMATAÇÃO DO VALOR Unidades de Medida
                Object valorOriginal = coluna.getValue();
                Object valorFormatado = valorOriginal;

                if (valorOriginal != null) {
                    String chaveAmigavelLower = chaveAmigavel.toLowerCase();

                    // Regra para DIAS
                    if (chaveAmigavelLower.contains("dia") || chaveAmigavelLower.contains("dias")) {
                        if (valorOriginal instanceof Number) {
                            int valorInteiro = (int) Math.round(((Number) valorOriginal).doubleValue());
                            valorFormatado = valorInteiro + " dias";
                        } else {
                            try {
                                // Tenta converter caso o valor venha como String
                                double valorDouble = Double.parseDouble(valorOriginal.toString());
                                int valorInteiro = (int) Math.round(valorDouble);
                                valorFormatado = valorInteiro + " dias";
                            } catch (NumberFormatException e) {
                                valorFormatado = valorOriginal.toString() + " dias";
                            }
                        }
                    }
                    // Regra para DINHEIRO
                    else if (chaveAmigavelLower.contains("valor")
                            || chaveAmigavelLower.contains("preco")
                            || chaveAmigavelLower.contains("sub total")
                            || chaveAmigavelLower.contains("soma total")) {

                        if (valorOriginal instanceof Number) {
                            double valorNumerico = ((Number) valorOriginal).doubleValue();
                            valorFormatado = String.format("R$ %.2f", valorNumerico).replace(".", ",");
                        }
                    }
                    // Regra para porcentagem
                    else if (chaveAmigavelLower.contains("percentual") || chaveAmigavelLower.contains("porcentagem")) {
                        valorFormatado = valorOriginal.toString() + " %";
                    }
                }

                novaLinha.put(chaveAmigavel, valorFormatado);
            }
            resultadoFormatado.add(novaLinha);
        }

        return resultadoFormatado;
    }


}
