package com.example.PortfolioProject.Controller;

import com.example.PortfolioProject.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin/admin";
    }




}