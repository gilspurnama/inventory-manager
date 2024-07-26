package com.example.inventory_management.service.impl;

import com.example.inventory_management.dto.InventoryDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Inventory;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.InventoryRepository;
import com.example.inventory_management.repository.ItemRepository;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.example.inventory_management.util.ResponseEnum.*;

@Service
@Transactional
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;

    @Override
    public ResponseEntity<ResponseDto<List<InventoryDto.Response>>> list(String itemId, Integer page, Integer size) {
        if (page < 0) {
            throw new ResponseException(PAGE_START.code(), PAGE_START.message(), PAGE_START.httpStatus());
        }

        if (size < 1 || size > 10000) {
            throw new ResponseException(MINIMUM_MAXIMUM_SIZE.code(), MINIMUM_MAXIMUM_SIZE.message(), MINIMUM_MAXIMUM_SIZE.httpStatus());
        }

        List<Inventory> inventoryList;

        if (itemId.isEmpty()) {
            Page<Inventory> inventoryPage = inventoryRepository.findAll(PageRequest.of(page, size));
            inventoryList = inventoryPage.getContent();
        } else {
            inventoryList = inventoryRepository.findAllByItemId(itemId, PageRequest.of(page, size));
        }

        List<InventoryDto.Response> responses = inventoryList.stream().map(data -> InventoryDto.Response.builder()
                .id(data.getId())
                .itemId(data.getItem().getId())
                .itemName(data.getItem().getName())
                .quantity(data.getQuantity())
                .type(data.getType().toUpperCase())
                .build())
                .toList();
        return ResponseEntity.ok(new ResponseDto<>(responses));
    }

    @Override
    public ResponseEntity<ResponseDto<InventoryDto.Response>> create(InventoryDto.CreateRequest request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(()-> new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));

        Inventory inventory = new Inventory();
        inventory.setItemId(item.getId());
        inventory.setType(request.getType());
        inventory.setQuantity(request.getQuantity());
        inventoryRepository.saveAndFlush(inventory);

        Stock stock = stockRepository.findByItemId(request.getItemId());
        if (request.getType().equals("W") && request.getQuantity() > stock.getQuantity()) {
            throw new ResponseException(STOCK_NOT_SUFFICIENT.code(), STOCK_NOT_SUFFICIENT.message(), STOCK_NOT_SUFFICIENT.httpStatus());
        }
        stock.setQuantity(Math.abs(stock.getQuantity() - request.getQuantity()));
        stockRepository.saveAndFlush(stock);

        return ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id(inventory.getId())
                .itemId(request.getItemId())
                .itemName(item.getName())
                .quantity(inventory.getQuantity())
                .type(inventory.getType().toUpperCase())
                .build(), CREATE_SUCCESS.message()));
    }

    @Override
    public ResponseEntity<ResponseDto<InventoryDto.Response>> get(String id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(()-> new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));
        return ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id(inventory.getId())
                .itemId(inventory.getItem().getId())
                .itemName(inventory.getItem().getName())
                .type(inventory.getType().toUpperCase())
                .quantity(inventory.getQuantity())
                .build()));
    }

    @Override
    public ResponseEntity<ResponseDto<InventoryDto.Response>> update(String id, InventoryDto.UpdateRequest request) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(()-> new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));
        Stock stock = stockRepository.findByItemId(inventory.getItemId());

        int updatedStock = getUpdatedStock(request.getQuantity(), request.getType(), inventory, stock);
        inventory.setType(request.getType());
        inventory.setQuantity(request.getQuantity());
        inventoryRepository.saveAndFlush(inventory);

        stock.setQuantity(updatedStock);
        stockRepository.saveAndFlush(stock);

        return ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .itemId(inventory.getItem().getId())
                .itemName(inventory.getItem().getName())
                .quantity(request.getQuantity())
                .type(request.getType().toUpperCase())
                .build(), UPDATE_SUCCESS.message()));
    }

    @Override
    public ResponseEntity<ResponseDto<InventoryDto.Response>> delete(String id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(()-> new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));

        Stock stock = stockRepository.findByItemId(inventory.getItemId());
        int updatedStock;
        if (inventory.getType().equals("W")) {
            updatedStock = stock.getQuantity() + inventory.getQuantity();
        } else {
            updatedStock = stock.getQuantity() - inventory.getQuantity();
            if (updatedStock < 0) {
                throw new ResponseException(DELETE_INVENTORY_ERROR.code(), DELETE_INVENTORY_ERROR.message(), DELETE_INVENTORY_ERROR.httpStatus());
            }
        }
        stock.setQuantity(updatedStock);
        stockRepository.saveAndFlush(stock);

        inventoryRepository.delete(inventory);
        return ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id(inventory.getId())
                .itemId(inventory.getItem().getId())
                .itemName(inventory.getItem().getName())
                .quantity(inventory.getQuantity())
                .type(inventory.getType().toUpperCase())
                .build(), DELETE_SUCCESS.message()));
    }

    private static int getUpdatedStock(int reqQty, String reqType, Inventory inventory, Stock stock) {
        int updatedStock;
            if (Objects.equals(reqType, "W")) {
                updatedStock = stock.getQuantity() - reqQty;
                if (updatedStock < 0) {
                    throw new ResponseException(STOCK_NOT_SUFFICIENT.code(), STOCK_NOT_SUFFICIENT.message(), STOCK_NOT_SUFFICIENT.httpStatus());
                }
            } else {
                updatedStock = stock.getQuantity() + Math.abs(inventory.getQuantity() - reqQty);
            }
        return updatedStock;
    }
}
