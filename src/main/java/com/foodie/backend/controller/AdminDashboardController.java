package com.foodie.backend.controller;

import com.foodie.backend.model.Order;
import com.foodie.backend.repository.OrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final OrderRepository orderRepository;

    public AdminDashboardController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public Map<String, Object> dashboardStats() {

        List<Order> orders = orderRepository.findAll();

        long totalOrders = orders.size();
        long delivered = orders.stream()
                .filter(o -> "DELIVERED".equals(o.getOrderStatus()))
                .count();
        long cancelled = orders.stream()
                .filter(o -> "CANCELLED".equals(o.getOrderStatus()))
                .count();
        long pending = orders.stream()
                .filter(o ->
                        !"DELIVERED".equals(o.getOrderStatus()) &&
                                !"CANCELLED".equals(o.getOrderStatus())
                ).count();

        double revenue = orders.stream()
                .filter(o -> "DELIVERED".equals(o.getOrderStatus()))
                .mapToDouble(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount())
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", totalOrders);
        response.put("delivered", delivered);
        response.put("cancelled", cancelled);
        response.put("pending", pending);
        response.put("revenue", revenue);

        return response;
    }
}
