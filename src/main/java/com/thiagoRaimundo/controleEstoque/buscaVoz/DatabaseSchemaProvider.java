package com.thiagoRaimundo.controleEstoque.buscaVoz;

import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaProvider {
    public String getSchemaDescription(){
        return """
                O banco de dados de estoque possui as seguintes tabelas:
                            
                Tabela 'tb_produto':
                            - product_id (BIGINT, PRIMARY KEY)
                            - name (VARCHAR(255))
                            - product_description (VARCHAR(255))
                            - status (BOOLEAN)
                            - category_id (BIGINT)
                           
                            FOREIGN KEY (category_id) REFERENCES tb_category(id) ON DELETE SET NULL
                            
                Tabela 'tb_stock':
                            - id (BIGINT, PRIMARY KEY)
                            - product_id (BIGINT)
                            - quantidade_minima (INT)
                            - quantidade_atual (INT)
                            - quantidade_maxima (INT)
                            - status BOOLEAN
                            - deleted_at DATETIME(6)  -- data de deleção lógica
                            
                            FOREIGN KEY (product_id) REFERENCES tb_produto(product_id) ON DELETE CASCADE
                            
                Tabela 'tb_category':
                            - id (BIGINT, PRIMARY KEY)
                            - name VARCHAR(100) NOT NULL UNIQUE
                            - description VARCHAR(255)
                            - status BOOLEAN DEFAULT TRUE
                            
                Tabela 'tb_lote':
                            - id (BIGINT, PRIMARY KEY)
                            - quant_produtos INT
                            - codigo VARCHAR(255) UNIQUE
                            - validate DATE
                            - status BOOLEAN DEFAULT TRUE
                            - product_id BIGINT
                            
                            FOREIGN KEY (product_id) REFERENCES tb_produto(product_id) ON DELETE CASCADE
                            
                Tabela 'tb_user':
                            - id (BIGINT, PRIMARY KEY)
                            - name VARCHAR(100)
                            - email VARCHAR(100)
                            - password VARCHAR(8)
                            - tipo_user TINYINT NOT NULL CHECK (tipo_user IN (0, 1)),  -- 0=ADMIN, 1=OPERADOR
                            - status BOOLEAN DEFAULT TRUE
                            
                Tabela 'tb_stock_movement':
                            - id (BIGINT, PRIMARY KEY)
                            - tipo ENUM('AJUSTE_NEGATIVO','AJUSTE_POSITIVO','COMPRA','DEVOLUCAO','VENDA')
                            - quantidade INT  -- quantidade de produtos na operação
                            - observacao VARCHAR(255)
                            - data_hora DATETIME(6)
                            - status BOOLEAN DEFAULT TRUE
                            - product_id (BIGINT)
                            - user_id (BIGINT)
                            - lote_id BIGINT
                            
                            FOREIGN KEY (product_id) REFERENCES tb_produto(product_id) ON DELETE CASCADE,
                            FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE
                            FOREIGN KEY (lote_id) REFERENCES tb_lote(id) ON DELETE CASCADE NULL

                            
                Tabela 'tb_sale':
                            - id (BIGINT, PRIMARY KEY)
                            - data_venda DATETIME(6)
                            - valor_total DECIMAL(38,2)
                            - user_id BIGINT
                            - status BOOLEAN DEFAULT TRUE
                            - dalete_at DATETIME(6)
                            - delete_by VARCHAR(255)
                            
                            FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE
                      
                Tabela 'tb_sale_item':
                            - id (BIGINT, PRIMARY KEY)
                            - valor_venda DECIMAL(38,2)
                            - sub_total DECIMAL(38,2)
                            - quantidade INT
                            - product_id BIGINT
                            - sale_id BIGINT
                            
                            FOREIGN KEY (product_id) REFERENCES tb_produto(product_id) ON DELETE CASCADE,
                            FOREIGN KEY (sale_id) REFERENCES tb_sale(id) ON DELETE CASCADE
                            
                Regras:
                - Use apenas SELECT para consultas.s
                - Não use DELETE, DROP, UPDATE, INSERT.
                - Sempre retorne apenas a query SQL, sem explicações.
                - Para buscas de texto (nomes de produtos), utilize LOWER(nome_da_coluna) LIKE '%termo%' para ignorar maiúsculas/minúsculas e buscar palavras parciais.
                - Prefira joins explícitos (INNER JOIN, LEFT JOIN) em vez de joins implícitos.
                - NUNCA deixe expressões, cálculos ou funções do SQL (como COALESCE, CASE WHEN, SUM, COUNT, ROUND, AVG, etc.) sem um apelido explícito usando 'AS'.
                - Garanta que as colunas geradas por funções agregadas ou cálculos na busca (usando 'AS') tenham nomes altamente explicativos sobre sua função.
                                Exemplos de nomenclatura de variáveis esperadas:
                                  * Para perguntas de duração de estoque: use 'dias_restantes' (ex: AS dias_restantes).
                                  * Para perguntas de somatórias e totais acumulados: use 'soma_total' (ex: AS soma_total) ou 'valor_total'.
                                  * Para perguntas de quantidade física total de itens: use 'quantidade_total' ou 'total_itens'.
                                  * Para buscas de maior/menor elemento: use 'maior_venda' ou 'menor_quantidade'.
                                  * Evite siglas ou nomes genéricos como 'res', 'total' ou 'qtd'.
                - NUNCA tente formatar valores ou unidades de medida diretamente no comando SQL (como concatenar textos como 'R$', 'dias' ou 'kg'). Sempre retorne os resultados numéricos como números puros (DECIMAL, INT, etc.), pois o sistema backend cuidará da formatação visual automaticamente.
                - Se o usuário realizar uma pesquisa informando APENAS o nome de um produto (ex: "Arroz", "Feijão" ou apenas um nome sem comando explícito de pergunta), você deve assumir por padrão que ele deseja uma "ficha rápida" deste produto. Retorne obrigatoriamente:
                                  . O nome do produto (AS nome_produto).
                                  . A quantidade atual disponível no estoque (AS quantidade_no_estoque).
                                  . O total de vendas individuais realizadas para este produto (AS total_vendas_realizadas).
                                  . A quantidade física total acumulada que já foi vendida (AS total_itens_vendidos).
                
                EXEMPLOS DE CONSULTAS COMUNS:
                               
                - Linguagem Natural: "Liste todos os produtos da categoria 'Eletrônicos'"
                 SQL: SELECT p.name FROM tb_produto p INNER JOIN tb_category c ON p.category_id = c.id WHERE LOWER(c.name) LIKE '%eletrônicos%' AND p.status = TRUE AND c.status = TRUE;
                - Linguagem Natural: "quantos produtos existem no estoque"
                 SQL: SELECT p.name AS produto, s.quantidade_atual AS quantidade FROM tb_produto p INNER JOIN tb_stock s ON p.product_id = s.product_id WHERE p.status = TRUE AND s.status = TRUE;
                - Linguagem Natural: "quantos quilos de arroz existem no estoque"
                 SQL: SELECT p.name AS produto, s.quantidade_atual AS quantidade FROM tb_produto p INNER JOIN tb_stock s ON p.product_id = s.product_id WHERE LOWER(p.name) LIKE '%arroz%' AND p.status = TRUE AND s.status = TRUE;
                - Linguagem Natural: "qual o produto com a menos quantidade"
                 SQL: SELECT p.name AS produto, s.quantidade_atual AS quantidade FROM tb_produto p INNER JOIN tb_stock s ON p.product_id = s.product_id WHERE p.status = TRUE AND s.status = TRUE ORDER BY s.quantidade_atual ASC LIMIT 1;
                - Linguagem Natural: "quanto tempo o produto x vai durar"
                  SQL: WITH consumo_recente AS (SELECT si.product_id, COALESCE(SUM(si.quantidade), 0) AS total_vendido FROM tb_sale_item si INNER JOIN tb_sale sa ON si.sale_id = sa.id WHERE sa.data_venda >= CURRENT_TIMESTAMP - INTERVAL '30 days' AND sa.status = TRUE GROUP BY si.product_id) SELECT p.name AS produto, s.quantidade_atual AS estoque_atual, CASE WHEN COALESCE(cr.total_vendido, 0) = 0 THEN NULL ELSE ROUND((s.quantidade_atual * 30.0) / cr.total_vendido, 1) END AS dias_restantes FROM tb_produto p INNER JOIN tb_stock s ON p.product_id = s.product_id LEFT JOIN consumo_recente cr ON p.product_id = cr.product_id WHERE LOWER(p.name) LIKE '%x%' AND p.status = TRUE AND s.status = TRUE;
                - Linguagem Natural: "quantos porcentos representa a venda do produto x do numero de vendas atual"
                  SQL: WITH total_vendas AS (SELECT COALESCE(SUM(si.quantidade), 0) AS total FROM tb_sale_item si INNER JOIN tb_sale sa ON si.sale_id = sa.id WHERE sa.status = TRUE), produto_vendas AS (SELECT COALESCE(SUM(si.quantidade), 0) AS quantidade FROM tb_sale_item si INNER JOIN tb_sale sa ON si.sale_id = sa.id INNER JOIN tb_produto p ON si.product_id = p.product_id WHERE LOWER(p.name) LIKE '%x%' AND sa.status = TRUE) SELECT ROUND((pv.quantidade * 100.0) / tv.total, 2) AS percentual FROM total_vendas tv, produto_vendas pv;
                -Linguagem Natural: "Arroz"
                  SQL: SELECT p.name AS nome_produto, s.quantidade_atual AS quantidade_no_estoque, COUNT(DISTINCT si.sale_id) AS total_vendas_realizadas, COALESCE(SUM(si.quantidade), 0) AS total_itens_vendidos FROM tb_produto p INNER JOIN tb_stock s ON p.product_id = s.product_id LEFT JOIN tb_sale_item si ON p.product_id = si.product_id LEFT JOIN tb_sale sa ON si.sale_id = sa.id AND sa.status = TRUE WHERE LOWER(p.name) LIKE '%arroz%' AND p.status = TRUE AND s.status = TRUE GROUP BY p.name, s.quantidade_atual;
                -Linguagem Natural: "compare o produto x e o produto y"
                  SQL: WITH total_vendas_loja AS (SELECT COALESCE(SUM(si.quantidade), 0) AS total_loja FROM tb_sale_item si INNER JOIN tb_sale sa ON si.sale_id = sa.id WHERE sa.status = TRUE), vendas_30d AS (SELECT si.product_id, COALESCE(SUM(si.quantidade), 0) AS qtd_30d FROM tb_sale_item si INNER JOIN tb_sale sa ON si.sale_id = sa.id WHERE sa.data_venda >= CURRENT_TIMESTAMP - INTERVAL '30 days' AND sa.status = TRUE GROUP BY si.product_id), metricas_produtos AS (SELECT p.product_id, p.name AS produto, s.quantidade_atual AS estoque_atual, COALESCE(SUM(si.quantidade), 0) AS quantidade_vendida, COALESCE(SUM(si.sub_total), 0.0) AS faturamento_individual, COALESCE(v30.qtd_30d, 0) AS qtd_vendida_30d FROM tb_produto p INNER JOIN tb_stock s ON p.product_id = s.product_id LEFT JOIN tb_sale_item si ON p.product_id = si.product_id LEFT JOIN tb_sale sa ON si.sale_id = sa.id AND sa.status = TRUE LEFT JOIN vendas_30d v30 ON p.product_id = v30.product_id WHERE (LOWER(p.name) LIKE '%x%' OR LOWER(p.name) LIKE '%y%') AND p.status = TRUE AND s.status = TRUE GROUP BY p.product_id, p.name, s.quantidade_atual, v30.qtd_30d) SELECT mp.produto AS produto, mp.estoque_atual AS quantidade_no_estoque, mp.quantidade_vendida AS total_itens_vendidos, mp.faturamento_individual AS faturamento_individual, SUM(mp.faturamento_individual) OVER() AS faturamento_somado_total, CASE WHEN tvl.total_loja = 0 THEN 0.0 ELSE ROUND((mp.quantidade_vendida * 100.0) / tvl.total_loja, 2) END AS percentual_vendas_loja, CASE WHEN mp.qtd_vendida_30d = 0 THEN NULL ELSE ROUND((mp.estoque_atual * 30.0) / mp.qtd_vendida_30d, 1) END AS dias_restantes FROM metricas_produtos mp CROSS JOIN total_vendas_loja tvl;
                """;
    }
}
