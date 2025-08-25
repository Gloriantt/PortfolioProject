package com.example.PortfolioProject.Repository;

import com.example.PortfolioProject.Models.Cart;
import com.example.PortfolioProject.Models.CartStatus;
import com.example.PortfolioProject.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findBySessionIdAndStatus(String sessionId, CartStatus status);
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
}