package com.example.inventory_management.controller;

import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.dto.StockDto;
import com.example.inventory_management.service.OrderItemService;
import com.example.inventory_management.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stocks")
public class StockController {
    private final StockService stockService;

    @Operation(description = "List all stocks with pagination")
    @GetMapping
    public ResponseEntity<ResponseDto<List<StockDto.Response>>> list(
            @Schema(description = "input if want to search a specific item id")
            @RequestParam(required = false, defaultValue = "") String itemId,
            @Schema(description = "page is required, min value is 0")
            @RequestParam(defaultValue = "0") Integer page,
            @Schema(description = "size is required, min value is 1")
            @RequestParam(defaultValue = "10") Integer size) {
        return stockService.list(itemId, page, size);
    }

    @Operation(description = "get item by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<StockDto.Response>> get(
            @PathVariable String id) {
        return stockService.get(id);
    }
}
