package com.example.inventory_management.repository;

import com.example.inventory_management.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    @Query(value = "select s from Stock s join s.item si where si.id =?1")
    Stock findByItemId(String itemId);

    @Query("select s from Stock s join s.item it where it.id=?1")
    List<Stock> findAllByItemId(String itemId, Pageable pageable);
}
