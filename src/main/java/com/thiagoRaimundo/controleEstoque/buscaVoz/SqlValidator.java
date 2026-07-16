package com.thiagoRaimundo.controleEstoque.buscaVoz;

import com.thiagoRaimundo.controleEstoque.exceptions.ValidacaoQueryException;
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
        // Parse da instrução usando JSqlParser

        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            Statement statement = parserManager.parse(new StringReader(sql));

            if (!(statement instanceof Select)) {
                throw new ValidacaoQueryException("Apenas consultas SELECT são permitidas.");
            }

            // inspecionar as tabelas referenciadas (evitar acesso a tabelas sensíveis)
            TablesNamesFinder tablesFinder = new TablesNamesFinder();
            List<String> tabelas = tablesFinder.getTableList(statement);
            for (String tabela : tabelas) {
                if (!tabela.matches("tb_produto|tb_stock_movement|tb_stock|tb_category|tb_lote|tb_sale|tb_sale_item|tb_user")) {
                    throw new SecurityException("Acesso a tabela não autorizada: " + tabela);
                }
            }

            // limitar cláusulas perigosas (INTO OUTFILE, etc.)
            if (sql.toUpperCase().contains("INTO OUTFILE") ||
                    sql.toUpperCase().contains("INTO DUMPFILE")) {
                throw new SecurityException("Cláusula não permitida.");
            }

        } catch (JSQLParserException e) {
            throw new ValidacaoQueryException("SQL inválido gerado pelo Gemini: " + e.getMessage());
        }
    }
}
