package com.foodie.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       BASIC DETAILS
    ========================= */

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private String description;
    private String category;
    private String image;

    /* =========================
       RELATIONSHIPS
    ========================= */

    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY)
    @JsonIgnore   // 🔥 VERY IMPORTANT (prevents infinite JSON loop)
    private List<OrderItem> orderItems = new ArrayList<>();

    /* =========================
       GETTERS & SETTERS
    ========================= */

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
