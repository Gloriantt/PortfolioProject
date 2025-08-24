package com.example.PortfolioProject.Service;

import com.example.PortfolioProject.Models.Category;
import com.example.PortfolioProject.Models.Product;
import com.example.PortfolioProject.Repository.CategoryRepository;
import com.example.PortfolioProject.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Получить все категории
     * @return список всех категорий
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Получить категорию по ID
     * @param id идентификатор категории
     * @return категория или null если не найдена
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /**
     * Получить категорию по имени
     * @param name имя категории
     * @return категория или null если не найдена
     */
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    /**
     * Создать новую категорию
     * @param category объект категории
     * @return сохраненная категория
     * @throws IllegalArgumentException если категория с таким именем уже существует
     */
    public Category createCategory(Category category) {
        // Проверяем, не существует ли уже категория с таким именем
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    /**
     * Обновить существующую категорию
     * @param id идентификатор категории
     * @param categoryDetails новые данные категории
     * @return обновленная категория
     * @throws RuntimeException если категория не найдена
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Проверяем, не занято ли новое имя другой категорией
        if (!category.getName().equals(categoryDetails.getName())
                && categoryRepository.existsByName(categoryDetails.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDetails.getName() + "' already exists");
        }

        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    /**
     * Удалить категорию
     * @param id идентификатор категории
     * @throws RuntimeException если категория не найдена или содержит товары
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Проверяем, есть ли товары в этой категории
        if (hasProducts(id)) {
            throw new RuntimeException("Cannot delete category with existing products. Please reassign or delete products first.");
        }

        categoryRepository.deleteById(id);
    }

    /**
     * Удалить категорию с перемещением товаров в другую категорию
     * @param categoryId ID удаляемой категории
     * @param targetCategoryId ID категории, куда переместить товары (null для удаления связи)
     */
    @Transactional
    public void deleteCategoryWithProductReassignment(Long categoryId, Long targetCategoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        List<Product> products = productRepository.findByCategoryId(categoryId);

        if (targetCategoryId != null) {
            Category targetCategory = categoryRepository.findById(targetCategoryId)
                    .orElseThrow(() -> new RuntimeException("Target category not found with id: " + targetCategoryId));

            // Перемещаем все товары в целевую категорию
            for (Product product : products) {
                product.setCategory(targetCategory);
                productRepository.save(product);
            }
        } else {
            // Удаляем связь с категорией у всех товаров
            for (Product product : products) {
                product.setCategory(null);
                productRepository.save(product);
            }
        }

        categoryRepository.deleteById(categoryId);
    }

    /**
     * Проверить, есть ли товары в категории
     * @param categoryId идентификатор категории
     * @return true если есть товары, false если нет
     */
    public boolean hasProducts(Long categoryId) {
        return productRepository.countByCategoryId(categoryId) > 0;
    }

    /**
     * Получить количество товаров в категории
     * @param categoryId идентификатор категории
     * @return количество товаров
     */
    public long getProductCount(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    /**
     * Получить все товары в категории
     * @param categoryId идентификатор категории
     * @return список товаров
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Проверить существование категории по имени
     * @param name имя категории
     * @return true если существует, false если нет
     */
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * Проверить существование категории по ID
     * @param id идентификатор категории
     * @return true если существует, false если нет
     */
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Получить категории с количеством товаров
     * @return список категорий с подсчетом товаров
     */
    public List<CategoryWithProductCount> getCategoriesWithProductCount() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryWithProductCount(
                        category,
                        productRepository.countByCategoryId(category.getId())
                ))
                .toList();
    }

    /**
     * Вспомогательный класс для категории с количеством товаров
     */
    public static class CategoryWithProductCount {
        private Category category;
        private long productCount;

        public CategoryWithProductCount(Category category, long productCount) {
            this.category = category;
            this.productCount = productCount;
        }

        public Category getCategory() { return category; }
        public long getProductCount() { return productCount; }
    }
}