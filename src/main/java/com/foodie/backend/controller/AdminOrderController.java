package com.foodie.backend.controller;

import com.foodie.backend.dto.OrderStatusUpdate;
import com.foodie.backend.model.Order;
import com.foodie.backend.repository.OrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderRepository orderRepository;

    public AdminOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // ✅ GET ALL ORDERS (ADMIN)
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ UPDATE ORDER STATUS
    @PutMapping("/{id}/status")
    public OrderStatusUpdate updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String updatedStatus = status.toUpperCase();
        order.setOrderStatus(updatedStatus);
        orderRepository.save(order);

        return new OrderStatusUpdate(
                order.getId(),
                updatedStatus,
                order.getUserEmail()
        );
    }

    // ✅ CANCEL ORDER
    @PutMapping("/{id}/cancel")
    public OrderStatusUpdate cancelOrder(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("DELIVERED".equals(order.getOrderStatus())) {
            throw new RuntimeException("Delivered order cannot be cancelled");
        }

        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);

        return new OrderStatusUpdate(
                order.getId(),
                "CANCELLED",
                order.getUserEmail()
        );
    }
}
