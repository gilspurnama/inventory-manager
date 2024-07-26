package com.example.inventory_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class InventoryDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private String id;
        private String itemId;
        private String itemName;
        private Integer quantity;
        private String type;
    }

    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank(message = "Quantity is required")
        private Integer quantity;

        @NotBlank(message = "Type is required")
        private String type;

        @NotBlank(message = "Item ID is required")
        private String itemId;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        private Integer quantity;
        private String type;
    }
}
