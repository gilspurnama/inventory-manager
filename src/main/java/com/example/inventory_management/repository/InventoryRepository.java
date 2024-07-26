package com.example.inventory_management.repository;

import com.example.inventory_management.model.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    @Query("select i from Inventory i join i.item it where it.id=?1")
    List<Inventory> findAllByItemId(String itemId, Pageable pageable);
}
