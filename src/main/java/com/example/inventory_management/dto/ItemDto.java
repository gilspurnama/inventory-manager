package com.example.inventory_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class ItemDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private String id;
        private String name;
        private Integer price;
    }

    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Price is required")
        private Integer price;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        private String name;
        private Integer price;
    }
}
