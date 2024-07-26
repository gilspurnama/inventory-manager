package com.example.inventory_management.service;

import com.example.inventory_management.dto.ItemDto;
import com.example.inventory_management.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ItemService {

    ResponseEntity<ResponseDto<List<ItemDto.Response>>> list(String name, Integer page, Integer size);
    ResponseEntity<ResponseDto<ItemDto.Response>> create(ItemDto.CreateRequest request);

    ResponseEntity<ResponseDto<ItemDto.Response>> get(String id);
    ResponseEntity<ResponseDto<ItemDto.Response>> update(String id, ItemDto.UpdateRequest request);
    ResponseEntity<ResponseDto<ItemDto.Response>> delete(String id);


}
