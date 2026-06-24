package com.thiagoRaimundo.controleEstoque.buscaVoz;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.List;


@Component
public class SqlValidator {

    public void validateAndSanitize(String sql) {
        // 1. Parse da instrução usando JSqlParser

        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            Statement statement = parserManager.parse(new StringReader(sql));

            // 2. Permite apenas SELECT
            if (!(statement instanceof Select)) {
                throw new SecurityException("Apenas consultas SELECT são permitidas.");
            }

            // inspecionar as tabelas referenciadas (evitar acesso a tabelas sensíveis)
            TablesNamesFinder tablesFinder = new TablesNamesFinder();
            List<String> tabelas = tablesFinder.getTableList(statement);
            for (String tabela : tabelas) {
                if (!tabela.matches("tb_produto|tb_stock_movement|tb_stock|tb_category|tb_lote|tb_sale|tb_sale_item|tb_user")) {
                    throw new SecurityException("Acesso a tabela não autorizada: " + tabela);
                }
            }

            // 4. Opcional: limitar cláusulas perigosas (INTO OUTFILE, etc.)
            if (sql.toUpperCase().contains("INTO OUTFILE") ||
                    sql.toUpperCase().contains("INTO DUMPFILE")) {
                throw new SecurityException("Cláusula não permitida.");
            }

        } catch (JSQLParserException e) {
            throw new IllegalArgumentException("SQL inválido gerado pelo Gemini: " + e.getMessage());
        }
    }
}
