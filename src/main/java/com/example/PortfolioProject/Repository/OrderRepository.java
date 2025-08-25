package com.example.PortfolioProject.Repository;

import com.example.PortfolioProject.Models.Order;
import com.example.PortfolioProject.Models.OrderStatus;
import com.example.PortfolioProject.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Поиск по номеру заказа
    Optional<Order> findByOrderNumber(String orderNumber);

    // Поиск заказов пользователя
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    // Все заказы с сортировкой
    List<Order> findAllByOrderByCreatedAtDesc();

    // Поиск по статусу
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    // Поиск по статусу с пагинацией
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Поиск заказов пользователя по статусу
    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, OrderStatus status);

    // Подсчет заказов по статусу
    long countByStatus(OrderStatus status);

    // Поиск заказов за период по статусу
    List<Order> findByStatusAndCreatedAtBetween(OrderStatus status,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate);

    // Поиск заказов пользователя за период
    List<Order> findByUserAndCreatedAtBetween(User user,
                                              LocalDateTime startDate,
                                              LocalDateTime endDate);

    // Кастомные запросы
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.totalAmount > :minAmount")
    List<Order> findByStatusAndMinAmount(@Param("status") OrderStatus status,
                                         @Param("minAmount") BigDecimal minAmount);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    BigDecimal getTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.customerEmail = :email ORDER BY o.createdAt DESC")
    List<Order> findByCustomerEmail(@Param("email") String email);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND DATE(o.createdAt) = CURRENT_DATE")
    long countTodayOrdersByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findByStatusIn(@Param("statuses") List<OrderStatus> statuses);
}