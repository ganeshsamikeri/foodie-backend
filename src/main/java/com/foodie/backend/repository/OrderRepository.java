package com.foodie.backend.repository;

import com.foodie.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.food
        WHERE LOWER(o.userEmail) = LOWER(:email)
    """)
    List<Order> findOrdersWithItems(@Param("email") String email);
}
