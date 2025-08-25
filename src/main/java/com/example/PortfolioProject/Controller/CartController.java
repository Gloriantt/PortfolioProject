package com.example.PortfolioProject.Controller;

import com.example.PortfolioProject.Models.Cart;
import com.example.PortfolioProject.Service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        String sessionId = session.getId();
        Cart cart = cartService.getCartBySessionId(sessionId);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        model.addAttribute("totalItems", cart.getTotalItems());

        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            String sessionId = session.getId();
            cartService.addToCart(sessionId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product to cart");
        }

        return "redirect:/cart";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateCartItem(@RequestParam Long productId,
                                 @RequestParam Integer quantity,
                                 HttpSession session) {
        try {
            String sessionId = session.getId();
            Cart cart = cartService.updateCartItem(sessionId, productId, quantity);
            return "{\"success\": true, \"total\": " + cart.getTotalPrice() + "}";
        } catch (Exception e) {
            return "{\"success\": false}";
        }
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            String sessionId = session.getId();
            cartService.removeFromCart(sessionId, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Product removed from cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing product");
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session,
                            RedirectAttributes redirectAttributes) {
        String sessionId = session.getId();
        cartService.clearCart(sessionId);
        redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully");

        return "redirect:/cart";
    }
}