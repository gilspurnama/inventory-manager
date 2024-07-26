package com.example.inventory_management.service.impl;

import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.OrderItem;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.ItemRepository;
import com.example.inventory_management.repository.OrderItemRepository;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.inventory_management.util.ResponseEnum.*;

@Service
@Transactional
@AllArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;

    @Override
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> create(OrderItemDto.CreateRequest request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(()-> new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));
        Stock stock = stockRepository.findByItemId(request.getItemId());

        if (stock.getQuantity() < request.getQuantity()) {
            throw new ResponseException(STOCK_NOT_SUFFICIENT.code(), STOCK_NOT_SUFFICIENT.message(), STOCK_NOT_SUFFICIENT.httpStatus());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setQuantity(request.getQuantity());
        orderItemRepository.saveAndFlush(orderItem);

        stock.setQuantity(stock.getQuantity() - request.getQuantity());
        stockRepository.saveAndFlush(stock);

        return ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id(orderItem.getId())
                .itemId(request.getItemId())
                .itemName(item.getName())
                .quantity(orderItem.getQuantity())
                .build(), CREATE_SUCCESS.message()));
    }

    @Override
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> update(String id, OrderItemDto.UpdateRequest request) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(()-> new ResponseException(ORDER_ITEM_NOT_FOUND.code(), ORDER_ITEM_NOT_FOUND.message(), ORDER_ITEM_NOT_FOUND.httpStatus()));
        Stock stock = stockRepository.findByItemId(orderItem.getItemId());

        int difStock = orderItem.getQuantity() - request.getQuantity();
        if (difStock < 0) {
            if (stock.getQuantity() - Math.abs(difStock) < 0) {
                throw new ResponseException(STOCK_NOT_SUFFICIENT.code(), STOCK_NOT_SUFFICIENT.message(), STOCK_NOT_SUFFICIENT.httpStatus());
            }
            stock.setQuantity(stock.getQuantity() - Math.abs(difStock));
        } else {
            stock.setQuantity(stock.getQuantity() + difStock);
        }
        orderItem.setQuantity(request.getQuantity());
        orderItemRepository.saveAndFlush(orderItem);
        stockRepository.saveAndFlush(stock);

        return ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id(orderItem.getId())
                .itemId(orderItem.getItemId())
                .itemName(orderItem.getItem().getName())
                .quantity(request.getQuantity())
                .build(), UPDATE_SUCCESS.message()));
    }

    @Override
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> get(String id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(()-> new ResponseException(ORDER_ITEM_NOT_FOUND.code(), ORDER_ITEM_NOT_FOUND.message(), ORDER_ITEM_NOT_FOUND.httpStatus()));
        return ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id(orderItem.getId())
                .itemId(orderItem.getItem().getId())
                .itemName(orderItem.getItem().getName())
                .quantity(orderItem.getQuantity())
                .build()));
    }

    @Override
    public ResponseEntity<ResponseDto<List<OrderItemDto.Response>>> list(String itemId, Integer page, Integer size) {
        if (page < 0) {
            throw new ResponseException(PAGE_START.code(), PAGE_START.message(), PAGE_START.httpStatus());
        }

        if (size < 1 || size > 10000) {
            throw new ResponseException(MINIMUM_MAXIMUM_SIZE.code(), MINIMUM_MAXIMUM_SIZE.message(), MINIMUM_MAXIMUM_SIZE.httpStatus());
        }

        List<OrderItem> orderItemList;

        if (itemId.isEmpty()) {
            Page<OrderItem> orderItemPage = orderItemRepository.findAll(PageRequest.of(page, size));
            orderItemList = orderItemPage.getContent();
        } else {
            orderItemList = orderItemRepository.findAllByItemId(itemId, PageRequest.of(page, size));
        }

        List<OrderItemDto.Response> responses = orderItemList.stream().map(data -> OrderItemDto.Response.builder()
                        .id(data.getId())
                        .itemId(data.getItem().getId())
                        .itemName(data.getItem().getName())
                        .quantity(data.getQuantity())
                        .build())
                .toList();
        return ResponseEntity.ok(new ResponseDto<>(responses));
    }

    @Override
    public ResponseEntity<ResponseDto<OrderItemDto.Response>> delete(String id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(()-> new ResponseException(ORDER_ITEM_NOT_FOUND.code(), ORDER_ITEM_NOT_FOUND.message(), ORDER_ITEM_NOT_FOUND.httpStatus()));
        Stock stock = stockRepository.findByItemId(orderItem.getItemId());

        stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());
        stockRepository.saveAndFlush(stock);
        orderItemRepository.delete(orderItem);
        return ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id(orderItem.getId())
                .itemId(orderItem.getItem().getId())
                .itemName(orderItem.getItem().getName())
                .quantity(orderItem.getQuantity())
                .build(), DELETE_SUCCESS.message()));
    }
}