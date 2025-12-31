package com.foodie.backend.controller;

import com.foodie.backend.dto.OrderStatusUpdate;
import com.foodie.backend.model.Order;
import com.foodie.backend.repository.OrderRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AdminOrderController(
            OrderRepository orderRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /* =====================================================
       🔄 UPDATE ORDER STATUS
    ===================================================== */
    @PutMapping("/{id}/status")
    public void updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String updatedStatus = status.toUpperCase();
        order.setOrderStatus(updatedStatus);
        orderRepository.save(order);

        OrderStatusUpdate payload =
                new OrderStatusUpdate(
                        order.getId(),
                        updatedStatus,
                        order.getUserEmail()
                );

        // 🔴 ADMIN DASHBOARD (optional)
        messagingTemplate.convertAndSend(
                "/topic/admin/orders",
                payload
        );

        // 🔴 USER MY-ORDERS (IMPORTANT)
        messagingTemplate.convertAndSend(
                "/topic/orders/" + order.getUserEmail(),
                payload
        );
    }

    /* =====================================================
       ❌ CANCEL ORDER
    ===================================================== */
    @PutMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("DELIVERED".equals(order.getOrderStatus())) {
            throw new RuntimeException("Delivered order cannot be cancelled");
        }

        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);

        OrderStatusUpdate payload =
                new OrderStatusUpdate(
                        order.getId(),
                        "CANCELLED",
                        order.getUserEmail()
                );

        // 🔴 USER LIVE UPDATE
        messagingTemplate.convertAndSend(
                "/topic/orders/" + order.getUserEmail(),
                payload
        );
    }
}
