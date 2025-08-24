package com.example.PortfolioProject.Service;



import com.example.PortfolioProject.Models.Cart;
import com.example.PortfolioProject.Models.CartItem;
import com.example.PortfolioProject.Models.Product;
import com.example.PortfolioProject.Repository.CartRepository;
import com.example.PortfolioProject.Repository.ProductRepository;
import com.example.PortfolioProject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public Cart addToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough product in stock");
        }

        cart.addItem(product, quantity);
        return cartRepository.save(cart);
    }

    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = getCartByUserId(userId);

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (newQuantity <= 0) {
            cart.removeItem(productId);
        } else {
            Product product = item.getProduct();
            if (product.getQuantity() < newQuantity) {
                throw new RuntimeException("Not enough product in stock");
            }
            item.setQuantity(newQuantity);
        }

        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}