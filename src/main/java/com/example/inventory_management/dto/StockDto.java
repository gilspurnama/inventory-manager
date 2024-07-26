package com.example.inventory_management.dto;

import lombok.*;

public class StockDto {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private String id;
        private String itemId;
        private String itemName;
        private Integer quantity;
    }
}
