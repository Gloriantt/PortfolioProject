package com.example.PortfolioProject.Repository;

import com.example.PortfolioProject.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Найти категорию по имени
     */
    Optional<Category> findByName(String name);

    /**
     * Проверить существование категории по имени
     */
    boolean existsByName(String name);

    /**
     * Найти все категории, отсортированные по имени
     */
    List<Category> findAllByOrderByNameAsc();

    /**
     * Найти категории по части имени (для поиска)
     */
    List<Category> findByNameContainingIgnoreCase(String namePart);

    /**
     * Получить категории с количеством товаров (используя JPQL)
     */
    @Query("SELECT c, COUNT(p) FROM Category c LEFT JOIN c.products p GROUP BY c")
    List<Object[]> findAllWithProductCount();
}