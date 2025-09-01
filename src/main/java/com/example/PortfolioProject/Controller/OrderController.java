package com.example.PortfolioProject.Controller;



import com.example.PortfolioProject.DTO.CheckoutDto;
import com.example.PortfolioProject.DTO.CreateOrderDto;

import com.example.PortfolioProject.Models.Order;
import com.example.PortfolioProject.Models.OrderStatus;
import com.example.PortfolioProject.Models.User;
import com.example.PortfolioProject.Repository.UserRepository;
import com.example.PortfolioProject.Service.OrderService;
import com.example.PortfolioProject.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    UserService userService;
    UserRepository userRepository;

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(orderService.getUserOrders(user));
    }


    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderDto orderDto,
                                             @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Order order;

        if (userDetails != null) {
            // Для авторизованных пользователей
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            order = orderService.createOrderFromCart(
                    user.getId(),
                    Long.valueOf(orderDto.getPaymentMethod())
            );
        } else if (session.getId() != null ) {
            // Для гостевых пользователей
            CheckoutDto checkoutDto = new CheckoutDto();
            checkoutDto.setShippingAddress(orderDto.getShippingAddress());
            checkoutDto.setPaymentMethod(orderDto.getPaymentMethod());

            order = orderService.createOrderFromCart(
                    session.getId(),
                    checkoutDto
            );
        } else {
            return ResponseEntity.badRequest()
                    .body(null); // или throw new BadRequestException("Session ID required for guest checkout");
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Получить из UserDetails
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    // Admin endpoints
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @GetMapping("/{orderId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Long orderId,@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }


}