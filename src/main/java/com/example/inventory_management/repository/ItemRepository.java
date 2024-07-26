package com.example.inventory_management.repository;

import com.example.inventory_management.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    @Query("select i from Item i where lower(i.name) like %?1%")
    List<Item> findAllByName(String name, Pageable pageable);
    Boolean existsByName(String name);
}
