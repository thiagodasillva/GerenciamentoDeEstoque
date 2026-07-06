package com.thiagoRaimundo.controleEstoque.buscaVoz;

import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaProvider {
    public String getSchemaDescription(){
        //mudar para se adequar aos atributos do banco
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
                - Use apenas SELECT para consultas.
                - Não use DELETE, DROP, UPDATE, INSERT.
                - Sempre retorne apenas a query SQL, sem explicações.
                - Para buscas de texto (nomes de produtos), utilize LOWER(nome_da_coluna) LIKE '%termo%' para ignorar maiúsculas/minúsculas e buscar palavras parciais.
                - Prefira joins explícitos (INNER JOIN, LEFT JOIN) em vez de joins implícitos.
                
                
                EXEMPLOS DE CONSULTAS COMUNS:
                
                Linguagem Natural: "Liste todos os produtos da categoria 'Eletrônicos'"
                SQL:
                
                
                """;
    }
}
