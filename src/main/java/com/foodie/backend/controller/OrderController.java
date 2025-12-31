package com.foodie.backend.controller;

import com.foodie.backend.dto.OrderItemDTO;
import com.foodie.backend.dto.OrderResponseDTO;
import com.foodie.backend.dto.ReorderResponseDTO;
import com.foodie.backend.model.Food;
import com.foodie.backend.model.Order;
import com.foodie.backend.model.OrderItem;
import com.foodie.backend.repository.FoodRepository;
import com.foodie.backend.repository.OrderItemRepository;
import com.foodie.backend.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodRepository foodRepository;

    public OrderController(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            FoodRepository foodRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.foodRepository = foodRepository;
    }

    /* =====================================================
       ✅ PLACE ORDER
    ===================================================== */
    @PostMapping("/place")
    @Transactional
    public ResponseEntity<?> placeOrder(
            @RequestBody List<OrderItemDTO> items,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Order items cannot be empty");
        }

        String email = auth.getName().toLowerCase().trim();

        Order order = new Order();
        order.setUserEmail(email);
        order.setOrderStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());
        order.setCancelled(false);

        Order savedOrder = orderRepository.save(order);

        double total = 0.0;

        for (OrderItemDTO dto : items) {
            Food food = foodRepository.findById(dto.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Food not found"));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setFood(food);
            item.setQuantity(dto.getQuantity());
            item.setPrice(food.getPrice());

            orderItemRepository.save(item);
            savedOrder.getItems().add(item);

            total += food.getPrice() * dto.getQuantity();
        }

        savedOrder.setTotalAmount(total);
        orderRepository.save(savedOrder);

        return ResponseEntity.ok(Map.of(
                "message", "Order placed successfully",
                "orderId", savedOrder.getId(),
                "total", total
        ));
    }

    /* =====================================================
       🔁 REORDER
    ===================================================== */
    @PostMapping("/reorder/{orderId}")
    @Transactional
    public ResponseEntity<ReorderResponseDTO> reorder(
            @PathVariable Long orderId,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName().toLowerCase().trim();

        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!oldOrder.getUserEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Order newOrder = new Order();
        newOrder.setUserEmail(email);
        newOrder.setOrderStatus("PLACED");
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setCancelled(false);
        newOrder.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(newOrder);

        double total = 0;
        List<String> skippedItems = new ArrayList<>();

        for (OrderItem oldItem : oldOrder.getItems()) {
            Food food = foodRepository
                    .findById(oldItem.getFood().getId())
                    .orElse(null);

            if (food == null) {
                skippedItems.add(oldItem.getFood().getName());
                continue;
            }

            OrderItem newItem = new OrderItem();
            newItem.setOrder(savedOrder);
            newItem.setFood(food);
            newItem.setQuantity(oldItem.getQuantity());
            newItem.setPrice(food.getPrice());

            orderItemRepository.save(newItem);
            savedOrder.getItems().add(newItem);

            total += food.getPrice() * oldItem.getQuantity();
        }

        if (total == 0) {
            return ResponseEntity.badRequest().body(
                    new ReorderResponseDTO(
                            null,
                            skippedItems,
                            "All items are unavailable"
                    )
            );
        }

        savedOrder.setTotalAmount(total);
        orderRepository.save(savedOrder);

        return ResponseEntity.ok(
                new ReorderResponseDTO(
                        savedOrder.getId(),
                        skippedItems,
                        "Reorder placed successfully"
                )
        );
    }

    /* =====================================================
       📦 MY ORDERS
    ===================================================== */
    @GetMapping("/my-orders")
    @Transactional(readOnly = true)
    public ResponseEntity<List<OrderResponseDTO>> myOrders(
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName().toLowerCase().trim();

        List<Order> orders =
                orderRepository.findOrdersWithItems(email);

        List<OrderResponseDTO> response = orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setStatus(order.getOrderStatus());
            dto.setCreatedAt(order.getCreatedAt());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setCancelled(order.getCancelled());

            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO i = new OrderItemDTO();
                i.setFoodId(item.getFood().getId());
                i.setFoodName(item.getFood().getName());
                i.setPrice(item.getPrice());
                i.setQuantity(item.getQuantity());
                return i;
            }).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /* =====================================================
       ❌ CANCEL ORDER
    ===================================================== */
    @PutMapping("/cancel/{orderId}")
    @Transactional
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderId,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        String email = auth.getName().toLowerCase().trim();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Not authorized");
        }

        if (List.of("COOKING", "OUT_FOR_DELIVERY", "DELIVERED")
                .contains(order.getOrderStatus())) {
            return ResponseEntity.badRequest()
                    .body("Order cannot be cancelled at this stage");
        }

        order.setCancelled(true);
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Order cancelled successfully",
                "orderId", orderId
        ));
    }

    /* =====================================================
       🛠️ ADMIN: GET ALL ORDERS
    ===================================================== */
    @GetMapping("/admin/all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<OrderResponseDTO>>
    getAllOrdersForAdmin() {

        List<Order> orders = orderRepository.findAll();

        List<OrderResponseDTO> response = orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setStatus(order.getOrderStatus());
            dto.setCreatedAt(order.getCreatedAt());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setCancelled(order.getCancelled());

            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO i = new OrderItemDTO();
                i.setFoodId(item.getFood().getId());
                i.setFoodName(item.getFood().getName());
                i.setPrice(item.getPrice());
                i.setQuantity(item.getQuantity());
                return i;
            }).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /* =====================================================
       🛠️ ADMIN: UPDATE ORDER STATUS
    ===================================================== */
    @PutMapping("/admin/update-status/{orderId}")
    @Transactional
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(status.toUpperCase());

        if ("CANCELLED".equalsIgnoreCase(status)) {
            order.setCancelled(true);
        }

        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Order status updated successfully",
                "orderId", orderId,
                "status", status.toUpperCase()
        ));
    }

    /* =====================================================
       🛠️ TEST
    ===================================================== */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(
                "Orders API working at " + LocalDateTime.now()
        );
    }
}
