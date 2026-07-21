package com.thiagoRaimundo.controleEstoque.buscaVoz;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    public DashboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/resumo")
    public ResponseEntity<?> obterResumoDashboard() {
        try {
            String sqlProdutos = "SELECT COUNT(*) FROM tb_produto WHERE status = TRUE";
            Integer totalProdutos = jdbcTemplate.queryForObject(sqlProdutos, Integer.class);

            String sqlVendas = "SELECT COALESCE(SUM(valor_total), 0.0) FROM tb_sale " +
                    "WHERE status = TRUE AND data_venda >= CURRENT_TIMESTAMP - INTERVAL '30 days'";
            Double valorVendasMes = jdbcTemplate.queryForObject(sqlVendas, Double.class);

            String sqlBaixoEstoque = "SELECT COUNT(*) FROM tb_stock " +
                    "WHERE status = TRUE AND quantidade_atual < quantidade_minima AND deleted_at IS NULL";
            Integer baixoEstoqueCount = jdbcTemplate.queryForObject(sqlBaixoEstoque, Integer.class);

            String sqlMovimentacoes = "SELECT COUNT(*) FROM tb_stock_movement WHERE status = TRUE";
            Integer movimentacoesCount = jdbcTemplate.queryForObject(sqlMovimentacoes, Integer.class);

            Map<String, Object> resumo = new HashMap<>();
            resumo.put("total_produtos", totalProdutos != null ? totalProdutos : 0);
            resumo.put("valor_vendas_mes", valorVendasMes != null ? valorVendasMes : 0.0);
            resumo.put("baixo_estoque_count", baixoEstoqueCount != null ? baixoEstoqueCount : 0);
            resumo.put("movimentacoes_count", movimentacoesCount != null ? movimentacoesCount : 0);

            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao carregar dados do painel: " + e.getMessage());
        }
    }

    @GetMapping("/produtos")
    public ResponseEntity<?> obterProdutos() {
        try {
            String sql = """
                SELECT p.name AS nome, s.quantidade_atual AS quantidade, 
                       COALESCE((SELECT si.valor_venda FROM tb_sale_item si WHERE si.product_id = p.product_id ORDER BY si.id DESC LIMIT 1), 0.00) AS preco 
                FROM tb_produto p 
                INNER JOIN tb_stock s ON p.product_id = s.product_id 
                WHERE p.status = TRUE AND s.status = TRUE
            """;
            List<Map<String, Object>> produtos = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar produtos: " + e.getMessage());
        }
    }

    @GetMapping("/movimentacoes/recentes")
    public ResponseEntity<?> obterMovimentacoesRecentes() {
        try {
            String sql = """
                SELECT sm.tipo, p.name AS produto, sm.quantidade 
                FROM tb_stock_movement sm 
                INNER JOIN tb_produto p ON sm.product_id = p.product_id 
                WHERE sm.status = TRUE 
                ORDER BY sm.data_hora DESC 
                LIMIT 20
            """;
            List<Map<String, Object>> movimentacoes = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao carregar movimentações recentes: " + e.getMessage());
        }
    }

    @GetMapping("/produtos/baixo-estoque")
    public ResponseEntity<?> obterProdutosBaixoEstoque() {
        try {
            String sql = """
                SELECT p.name AS nome, s.quantidade_atual AS quantidade 
                FROM tb_produto p 
                INNER JOIN tb_stock s ON p.product_id = s.product_id 
                WHERE p.status = TRUE AND s.status = TRUE AND s.quantidade_atual < s.quantidade_minima
            """;
            List<Map<String, Object>> baixoEstoque = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(baixoEstoque);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao carregar produtos com baixo estoque: " + e.getMessage());
        }
    }

    @GetMapping("/movimentacoes/detalhes-tempo")
    public ResponseEntity<?> obterMovimentacoesComTempo() {
        try {
            String sql = """
                SELECT CONCAT(sm.tipo, ' - ', COALESCE(sm.observacao, 'Sem obs.')) AS descricao, 
                       5 AS tempo_duracao 
                FROM tb_stock_movement sm 
                WHERE sm.status = TRUE 
                ORDER BY sm.data_hora DESC
            """;
            List<Map<String, Object>> detalhesTempo = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(detalhesTempo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao calcular tempo das movimentações: " + e.getMessage());
        }
    }


    // 7. RELATÓRIO DE VENDAS / CONTAGEM (Para a aba CONTAR)
    @GetMapping("/vendas")
    public ResponseEntity<?> obterResumoVendas() {
        try {
            String sql = """
            SELECT s.id, 
                   TO_CHAR(s.data_venda, 'YYYY-MM-DD HH24:MI') AS data, 
                   s.valor_total, 
                   u.name AS vendedor
            FROM tb_sale s
            LEFT JOIN tb_user u ON s.user_id = u.id
            WHERE s.status = TRUE
            ORDER BY s.data_venda DESC
            LIMIT 50
            """;
            List<Map<String, Object>> vendas = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(vendas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar histórico de vendas: " + e.getMessage());
        }
    }


}
