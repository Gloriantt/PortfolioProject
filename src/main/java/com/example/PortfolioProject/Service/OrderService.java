package com.example.PortfolioProject.Service;

import com.example.PortfolioProject.DTO.CheckoutDto;
import com.example.PortfolioProject.Models.*;
import com.example.PortfolioProject.Repository.OrderRepository;
import com.example.PortfolioProject.Repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    /**
     * Создать заказ из корзины
     * @param cart корзина с товарами
     * @param checkoutDto данные для оформления заказа
     * @return созданный заказ
     */
    public Order createOrder(Cart cart, CheckoutDto checkoutDto) {
        // Проверяем, что корзина не пустая
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setCustomerName(checkoutDto.getCustomerName());
        order.setCustomerEmail(checkoutDto.getCustomerEmail());
        order.setCustomerPhone(checkoutDto.getCustomerPhone());
        order.setShippingAddress(checkoutDto.getFullAddress());
        order.setPaymentMethod(checkoutDto.getPaymentMethod());
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);

        // Если пользователь авторизован, связываем заказ с ним
        if (cart.getUser() != null) {
            order.setUser(cart.getUser());
        }

        // Сохраняем заказ
        order = orderRepository.save(order);

        // Создаем элементы заказа из корзины
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());

            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);
        }

        // Обновляем статус корзины
        cart.setStatus(CartStatus.COMPLETED);

        return order;
    }

    /**
     * Создать заказ напрямую из корзины (упрощенный метод)
     * @param cartId ID корзины
     * @param userId ID пользователя (опционально)
     * @return созданный заказ
     */
    @Transactional
    public Order createOrderFromCart(Long cartId, Long userId) {
        // Получаем корзину
        Cart cart = cartService.getCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with id: " + cartId);
        }

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }

        // Создаем заказ
        Order order = new Order();

        // Если есть пользователь, берем его данные
        if (cart.getUser() != null) {
            User user = cart.getUser();
            order.setUser(user);
            order.setCustomerName(user.getUsername());
            order.setCustomerEmail(user.getEmail());
        }

        // Устанавливаем основные параметры заказа
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod("PENDING");

        // Сохраняем заказ
        order = orderRepository.save(order);

        // Копируем товары из корзины в заказ
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());

            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        // Обновляем общую сумму заказа
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // Помечаем корзину как завершенную
        cart.setStatus(CartStatus.COMPLETED);

        return order;
    }

    /**
     * Создать заказ из корзины по session ID
     * @param sessionId ID сессии
     * @param checkoutDto данные для оформления
     * @return созданный заказ
     */
    @Transactional
    public Order createOrderFromCart(String sessionId, CheckoutDto checkoutDto) {
        Cart cart = cartService.getCartBySessionId(sessionId);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found");
        }

        return createOrder(cart, checkoutDto);
    }

    /**
     * Получить заказы по статусу
     * @param status статус заказа
     * @return список заказов с указанным статусом
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Получить заказы по статусу с пагинацией
     * @param status статус заказа
     * @param pageable параметры пагинации
     * @return страница заказов
     */
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    /**
     * Получить заказы пользователя по статусу
     * @param user пользователь
     * @param status статус заказа
     * @return список заказов
     */
    public List<Order> getUserOrdersByStatus(User user, OrderStatus status) {
        return orderRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status);
    }

    /**
     * Получить количество заказов по статусу
     * @param status статус заказа
     * @return количество заказов
     */
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    /**
     * Получить заказы за период по статусу
     * @param status статус заказа
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список заказов
     */
    public List<Order> getOrdersByStatusAndDateRange(OrderStatus status,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate) {
        return orderRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate);
    }

    /**
     * Массовое обновление статуса заказов
     * @param orderIds список ID заказов
     * @param newStatus новый статус
     */
    @Transactional
    public void updateOrdersStatus(List<Long> orderIds, OrderStatus newStatus) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        for (Order order : orders) {
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
        }
        orderRepository.saveAll(orders);
    }

    // Остальные методы остаются без изменений
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber).orElse(null);
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        }
        return null;
    }

    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /**
     * Получить общую сумму заказов по статусу
     * @param status статус заказа
     * @return общая сумма
     */
    public BigDecimal getTotalAmountByStatus(OrderStatus status) {
        List<Order> orders = getOrdersByStatus(status);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}