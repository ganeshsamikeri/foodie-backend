package com.foodie.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class OrderStatusWebSocketController {

    @MessageMapping("/order-status")
    public void handleStatusUpdate() {
        // Reserved for future use
    }
}
