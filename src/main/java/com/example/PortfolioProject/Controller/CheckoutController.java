package com.example.PortfolioProject.Controller;

import com.example.PortfolioProject.DTO.CheckoutDto;
import com.example.PortfolioProject.Models.Cart;
import com.example.PortfolioProject.Models.Order;
import com.example.PortfolioProject.Service.CartService;
import com.example.PortfolioProject.Service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showCheckoutForm(HttpSession session, Model model) {
        String sessionId = session.getId();
        Cart cart = cartService.getCartBySessionId(sessionId);

        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("checkoutForm", new CheckoutDto());

        return "checkout";
    }

    @PostMapping("/process")
    public String processCheckout(@ModelAttribute CheckoutDto checkoutDto,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            String sessionId = session.getId();
            Cart cart = cartService.getCartBySessionId(sessionId);

            if (cart.getItems().isEmpty()) {
                return "redirect:/cart";
            }

            Order order = orderService.createOrder(cart, checkoutDto);
            cartService.clearCart(sessionId);

            redirectAttributes.addFlashAttribute("orderNumber", order.getOrderNumber());
            return "redirect:/checkout/success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/success")
    public String orderSuccess(Model model) {
        return "order-success";
    }
}