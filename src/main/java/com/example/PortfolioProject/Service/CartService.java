package com.example.PortfolioProject.Service;

import com.example.PortfolioProject.Models.*;
import com.example.PortfolioProject.Repository.CartRepository;
import com.example.PortfolioProject.Repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    /**
     * Получить корзину по ID
     * @param cartId ID корзины
     * @return корзина или null
     */
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId).orElse(null);
    }

    /**
     * Получить активную корзину по ID
     * @param cartId ID корзины
     * @return активная корзина или null
     */
    public Cart getActiveCartById(Long cartId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent() && cart.get().getStatus() == CartStatus.ACTIVE) {
            return cart.get();
        }
        return null;
    }

    // Остальные методы остаются без изменений
    public Cart getCartBySessionId(String sessionId) {
        return cartRepository.findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE)
                .orElseGet(() -> createNewCart(sessionId));
    }

    public Cart getCartByUser(User user) {
        return cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(String sessionId) {
        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    public Cart addToCart(String sessionId, Long productId, int quantity) {
        Cart cart = getCartBySessionId(sessionId);
        Product product = productService.getProductById(productId);

        if (product != null) {
            cart.addItem(product, quantity);
            cart = cartRepository.save(cart);
        }

        return cart;
    }

    public Cart updateCartItem(String sessionId, Long productId, int quantity) {
        Cart cart = getCartBySessionId(sessionId);

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            if (quantity <= 0) {
                cart.removeItem(productId);
            } else {
                item.setQuantity(quantity);
            }
            cart.setUpdatedAt(LocalDateTime.now());
            cart = cartRepository.save(cart);
        }

        return cart;
    }

    public Cart removeFromCart(String sessionId, Long productId) {
        Cart cart = getCartBySessionId(sessionId);
        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    public void clearCart(String sessionId) {
        Cart cart = getCartBySessionId(sessionId);
        cart.clear();
        cartRepository.save(cart);
    }

    public int getCartItemCount(String sessionId) {
        Cart cart = getCartBySessionId(sessionId);
        return cart.getTotalItems();
    }

    public BigDecimal getCartTotal(String sessionId) {
        Cart cart = getCartBySessionId(sessionId);
        return cart.getTotalPrice();
    }

    public void mergeGuestCartWithUserCart(String sessionId, User user) {
        Optional<Cart> guestCart = cartRepository.findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE);

        if (guestCart.isPresent() && !guestCart.get().getItems().isEmpty()) {
            Cart userCart = getCartByUser(user);

            for (CartItem guestItem : guestCart.get().getItems()) {
                userCart.addItem(guestItem.getProduct(), guestItem.getQuantity());
            }

            cartRepository.save(userCart);
            guestCart.get().setStatus(CartStatus.ABANDONED);
            cartRepository.save(guestCart.get());
        }
    }
}