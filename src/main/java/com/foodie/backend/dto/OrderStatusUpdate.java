package com.foodie.backend.dto;

import lombok.Getter;
import lombok.Setter;

public class OrderStatusUpdate {

    private Long orderId;
    private String status;
    private String userEmail;

    public OrderStatusUpdate(Long orderId, String status, String userEmail) {
        this.orderId = orderId;
        this.status = status;
        this.userEmail = userEmail;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    @Getter
    @Setter
    public static class RegisterRequest {
        private String email;
        private String password;
    }
}
