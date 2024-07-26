package com.example.inventory_management.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "item_id")
    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_Id",updatable = false,insertable = false)
    private Item item;

    @Column(name = "qty")
    private Integer quantity;

    @Column(name = "type")
    private String type;
}
