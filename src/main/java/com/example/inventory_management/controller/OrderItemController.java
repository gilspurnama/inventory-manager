package com.example.inventory_management.controller;

import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("order-items")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Operation(description = "List all items with pagination")
    @GetMapping
    public ResponseEntity<ResponseDto<List<OrderItemDto.Response>>> list(
            @Schema(description = "input if want to search a specific item id")
            @RequestParam(required = false, defaultValue = "") String itemId,
            @Schema(description = "page is required, min value is 0")
            @RequestParam(defaultValue = "0") Integer page,
            @Schema(description = "size is required, min value is 1")
            @RequestParam(defaultValue = "10") Integer size) {
        return orderItemService.list(itemId, page, size);
    }

    @Operation(description = "Create new item")
    @PostMapping
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,content = {@Content(schema = @Schema(implementation = OrderItemDto.CreateRequest.class))})
            @RequestBody OrderItemDto.CreateRequest request) {
        return orderItemService.create(request);
    }

    @Operation(description = "get item by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> get(
            @PathVariable String id) {
        return orderItemService.get(id);
    }

    @Operation(description = "Update an item")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> update(
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,content = {@Content(schema = @Schema(implementation = OrderItemDto.CreateRequest.class))})
            @RequestBody OrderItemDto.UpdateRequest request) {
        return orderItemService.update(id, request);
    }

    @Operation(description = "delete item by id")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> delete(
            @PathVariable String id) {
        return orderItemService.delete(id);
    }
}
