package com.foodie.backend.dto;

import java.util.List;

public class ReorderResponseDTO {

    private Long newOrderId;
    private List<String> skippedItems;
    private String message;

    public ReorderResponseDTO(Long newOrderId, List<String> skippedItems, String message) {
        this.newOrderId = newOrderId;
        this.skippedItems = skippedItems;
        this.message = message;
    }

    public Long getNewOrderId() {
        return newOrderId;
    }

    public List<String> getSkippedItems() {
        return skippedItems;
    }

    public String getMessage() {
        return message;
    }
}
