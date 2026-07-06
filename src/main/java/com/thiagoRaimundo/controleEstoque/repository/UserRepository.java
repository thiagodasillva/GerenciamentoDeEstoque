package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findByStatusTrue();

    Optional<User> findByIdAndStatusTrue(Long id);

    Optional<User> findByEmailAndStatusTrue(String email);
    boolean existsByEmailAndStatusTrue(String email);
    Optional<User> findByEmail(String email);

}
