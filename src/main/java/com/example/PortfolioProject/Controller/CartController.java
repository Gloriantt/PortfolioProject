package com.example.PortfolioProject.Controller;


import com.example.PortfolioProject.Models.Cart;
import com.example.PortfolioProject.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        // Get user ID from authenticated user
        Long userId = 1L; // You need to implement getting user ID from UserDetails
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestParam Long productId,
                                          @RequestParam int quantity,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Get from UserDetails
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long productId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Get from UserDetails
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<Cart> updateQuantity(@PathVariable Long productId,
                                               @RequestParam int quantity,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Get from UserDetails
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Get from UserDetails
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}