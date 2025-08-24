package com.example.PortfolioProject.Controller;

import com.example.PortfolioProject.Models.Category;
import com.example.PortfolioProject.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getCategoriesWithProductCount());
        return "admin/categories";
    }

    @PostMapping("/add")
    public String addCategory(@RequestParam String name,
                              RedirectAttributes redirectAttributes) {
        try {
            // Проверка на пустое имя
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Category name cannot be empty!");
                return "redirect:/admin/categories";
            }

            // Проверка на существование категории
            if (categoryService.existsByName(name.trim())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Category with name '" + name + "' already exists!");
                return "redirect:/admin/categories";
            }

            Category category = new Category();
            category.setName(name.trim());
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Category '" + name + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error creating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Проверка на пустое имя
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Category name cannot be empty!");
                return "redirect:/admin/categories";
            }

            Category existingCategory = categoryService.getCategoryById(id);
            if (existingCategory == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Category not found!");
                return "redirect:/admin/categories";
            }

            // Проверка на дублирование имени
            if (!existingCategory.getName().equals(name.trim())
                    && categoryService.existsByName(name.trim())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Category with name '" + name + "' already exists!");
                return "redirect:/admin/categories";
            }

            Category category = new Category();
            category.setName(name.trim());
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Category not found!");
                return "redirect:/admin/categories";
            }

            if (categoryService.hasProducts(id)) {
                long productCount = categoryService.getProductCount(id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot delete category '" + category.getName() +
                                "' with " + productCount + " products. Please reassign or delete products first.");
            } else {
                String categoryName = category.getName();
                categoryService.deleteCategory(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Category '" + categoryName + "' deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}