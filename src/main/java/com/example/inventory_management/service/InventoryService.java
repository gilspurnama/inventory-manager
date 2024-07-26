package com.example.inventory_management.service;

import com.example.inventory_management.dto.InventoryDto;
import com.example.inventory_management.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InventoryService {

    ResponseEntity<ResponseDto<List<InventoryDto.Response>>> list(String itemId, Integer page, Integer size);
    ResponseEntity<ResponseDto<InventoryDto.Response>> create(InventoryDto.CreateRequest request);

    ResponseEntity<ResponseDto<InventoryDto.Response>> get(String id);
    ResponseEntity<ResponseDto<InventoryDto.Response>> update(String id, InventoryDto.UpdateRequest request);
    ResponseEntity<ResponseDto<InventoryDto.Response>> delete(String id);
}
