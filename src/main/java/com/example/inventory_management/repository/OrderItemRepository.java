package com.example.inventory_management.repository;

import com.example.inventory_management.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    @Query("select or from OrderItem or join or.item it where it.id=?1")
    List<OrderItem> findAllByItemId(String itemId, Pageable pageable);

}
