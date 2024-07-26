package com.example.inventory_management.controller;

import com.example.inventory_management.dto.ItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("items")
public class ItemController {
    private final ItemService itemService;

    @Operation(description = "List all items with pagination")
    @GetMapping
    public ResponseEntity<ResponseDto<List<ItemDto.Response>>> list(
            @Schema(description = "input if want to search a specific name")
            @RequestParam(required = false, defaultValue = "") String name,
            @Schema(description = "page is required, min value is 0")
            @RequestParam(defaultValue = "0") Integer page,
            @Schema(description = "size is required, min value is 1")
            @RequestParam(defaultValue = "10") Integer size) {
        return itemService.list(name, page, size);
    }

    @Operation(description = "Create new item")
    @PostMapping
    public ResponseEntity<ResponseDto<ItemDto.Response>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,content = {@Content(schema = @Schema(implementation = ItemDto.CreateRequest.class))})
            @RequestBody ItemDto.CreateRequest request) {
        return itemService.create(request);
    }

    @Operation(description = "get item by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ItemDto.Response>> get(
            @PathVariable String id) {
        return itemService.get(id);
    }

    @Operation(description = "Update an item")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ItemDto.Response>> update(
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,content = {@Content(schema = @Schema(implementation = ItemDto.CreateRequest.class))})
            @RequestBody ItemDto.UpdateRequest request) {
        return itemService.update(id, request);
    }

    @Operation(description = "delete item by id")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ItemDto.Response>> delete(
            @PathVariable String id) {
        return itemService.delete(id);
    }
}
