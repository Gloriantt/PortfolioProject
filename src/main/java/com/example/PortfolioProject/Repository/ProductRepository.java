package com.example.PortfolioProject.Repository;

import com.example.PortfolioProject.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Найти товары по категории
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Подсчитать количество товаров в категории
     */
    long countByCategoryId(Long categoryId);

    /**
     * Найти товары по имени категории
     */
    List<Product> findByCategoryName(String categoryName);

    /**
     * Найти товары по части названия
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Найти товары в ценовом диапазоне
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice);
}