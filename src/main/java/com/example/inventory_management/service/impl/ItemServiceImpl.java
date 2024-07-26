package com.example.inventory_management.service.impl;

import com.example.inventory_management.dto.ItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.ItemRepository;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.inventory_management.util.ResponseEnum.*;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private StockRepository stockRepository;

    @Override
    public ResponseEntity<ResponseDto<List<ItemDto.Response>>> list(String name, Integer page, Integer size) {
        if (page < 0) {
            throw new ResponseException(PAGE_START.code(), PAGE_START.message(), PAGE_START.httpStatus());
        }

        if (size < 1 || size > 10000) {
            throw new ResponseException(MINIMUM_MAXIMUM_SIZE.code(), MINIMUM_MAXIMUM_SIZE.message(), MINIMUM_MAXIMUM_SIZE.httpStatus());
        }

        List<Item> itemList = itemRepository.findAllByName(name, PageRequest.of(page, size));
        List<ItemDto.Response> response = itemList.stream().map(data -> ItemDto.Response.builder()
                .id(data.getId())
                .name(data.getName())
                .price(data.getPrice())
                .build())
                .toList();
        return ResponseEntity.ok(new ResponseDto<>(response));
    }

    @Override
    public ResponseEntity<ResponseDto<ItemDto.Response>> create(ItemDto.CreateRequest request) {
        if (Boolean.TRUE.equals(itemRepository.existsByName(request.getName()))) {
            throw new ResponseException(ITEM_NAME_ALREADY_EXIST.code(), ITEM_NAME_ALREADY_EXIST.message(), ITEM_NAME_ALREADY_EXIST.httpStatus());
        }

        Item item = new Item();
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        itemRepository.save(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);
        stockRepository.saveAndFlush(stock);

        return ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build(), CREATE_SUCCESS.message()));
    }

    @Override
    public ResponseEntity<ResponseDto<ItemDto.Response>> get(String id) {
        Item item = itemRepository.findById(id).orElseThrow(()-> new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        return ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build()));
    }

    @Override
    public ResponseEntity<ResponseDto<ItemDto.Response>> update(String id, ItemDto.UpdateRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(()-> new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        itemRepository.saveAndFlush(item);
        return ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id(item.getId())
                .name(request.getName())
                .price(request.getPrice())
                .build(), UPDATE_SUCCESS.message()));
    }

    @Override
    public ResponseEntity<ResponseDto<ItemDto.Response>> delete(String id) {
        Item item = itemRepository.findById(id).orElseThrow(()-> new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        Stock stock = stockRepository.findByItemId(item.getId());

        stockRepository.delete(stock);
        itemRepository.delete(item);
        return ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build(), DELETE_SUCCESS.message()));
    }
}
