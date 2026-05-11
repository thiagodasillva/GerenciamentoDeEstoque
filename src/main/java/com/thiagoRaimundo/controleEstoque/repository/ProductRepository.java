package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Category;
import com.thiagoRaimundo.controleEstoque.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategoryIdAndStatusTrue(Long id);
    Optional<Product> findByNameAndStatusTrue(String name);

    List<Product> findByStatusTrue();
    Optional<Product> findByIdAndStatusTrue(Long id);

    Boolean existsByIdAndStatusTrue(Long id);


}
