package com.example.inventory_management.service;

import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.dto.StockDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StockService {
    ResponseEntity<ResponseDto<StockDto.Response>> get(String id);
    ResponseEntity<ResponseDto<List<StockDto.Response>>> list(String itemId, Integer page, Integer size);


}
