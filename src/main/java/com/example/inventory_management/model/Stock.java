package com.example.inventory_management.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;


@Data
@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "item_id")
    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id",updatable = false,insertable = false)
    private Item item;

    @Column(name = "quantity")
    private Integer quantity;
}
