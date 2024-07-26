package com.example.inventory_management.service;

import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderItemService {
    ResponseEntity<ResponseDto<OrderItemDto.Response>> create(OrderItemDto.CreateRequest request);
    ResponseEntity<ResponseDto<OrderItemDto.Response>> update(String id, OrderItemDto.UpdateRequest request);
    ResponseEntity<ResponseDto<OrderItemDto.Response>> get(String id);
    ResponseEntity<ResponseDto<List<OrderItemDto.Response>>> list(String itemId, Integer page, Integer size);
    ResponseEntity<ResponseDto<OrderItemDto.Response>> delete(String id);


}
