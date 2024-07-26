package com.example.inventory_management.controller;

import com.example.inventory_management.dto.InventoryDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    @Operation(description = "List all inventories with pagination")
    @GetMapping
    public ResponseEntity<ResponseDto<List<InventoryDto.Response>>> list(
            @Schema(description = "input if want to search a specific item ID")
            @RequestParam(required = false, defaultValue = "") String itemId,
            @Schema(description = "page is required, min value is 0")
            @RequestParam(defaultValue = "0") Integer page,
            @Schema(description = "size is required, min value is 1")
            @RequestParam(defaultValue = "10") Integer size) {
        return inventoryService.list(itemId, page, size);
    }

    @Operation(description = "Create new inventory")
    @PostMapping
    public ResponseEntity<ResponseDto<InventoryDto.Response>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,content = {@Content(schema = @Schema(implementation = InventoryDto.CreateRequest.class))})
            @RequestBody InventoryDto.CreateRequest request) {
        return inventoryService.create(request);
    }

    @Operation(description = "get inventory by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<InventoryDto.Response>> get(
            @PathVariable String id) {
        return inventoryService.get(id);
    }

    @Operation(description = "Update an inventory")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<InventoryDto.Response>> update(
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,content = {@Content(schema = @Schema(implementation = InventoryDto.CreateRequest.class))})
            @RequestBody InventoryDto.UpdateRequest request) {
        return inventoryService.update(id, request);
    }

    @Operation(description = "delete an inventory by id")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<InventoryDto.Response>> delete(
            @PathVariable String id) {
        return inventoryService.delete(id);
    }
}
