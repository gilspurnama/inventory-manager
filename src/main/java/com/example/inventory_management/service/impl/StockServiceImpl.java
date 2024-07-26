package com.example.inventory_management.service.impl;

import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.dto.StockDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.StockService;
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
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public ResponseEntity<ResponseDto<StockDto.Response>> get(String id) {
        Stock stock = stockRepository.findById(id).orElseThrow(()-> new ResponseException(STOCK_NOT_FOUND.code(), STOCK_NOT_FOUND.message(), STOCK_NOT_FOUND.httpStatus()));
        return ResponseEntity.ok(new ResponseDto<>(StockDto.Response.builder()
                .id(stock.getId())
                .itemId(stock.getItem().getId())
                .itemName(stock.getItem().getName())
                .quantity(stock.getQuantity())
                .build()));
    }

    @Override
    public ResponseEntity<ResponseDto<List<StockDto.Response>>> list(String itemId, Integer page, Integer size) {
        if (page < 0) {
            throw new ResponseException(PAGE_START.code(), PAGE_START.message(), PAGE_START.httpStatus());
        }

        if (size < 1 || size > 10000) {
            throw new ResponseException(MINIMUM_MAXIMUM_SIZE.code(), MINIMUM_MAXIMUM_SIZE.message(), MINIMUM_MAXIMUM_SIZE.httpStatus());
        }

        List<Stock> stockList;

        if (itemId.isEmpty()) {
            Page<Stock> stockPage = stockRepository.findAll(PageRequest.of(page, size));
            stockList = stockPage.getContent();
        } else {
            stockList = stockRepository.findAllByItemId(itemId, PageRequest.of(page, size));
        }

        List<StockDto.Response> responses = stockList.stream().map(data -> StockDto.Response.builder()
                        .id(data.getId())
                        .itemId(data.getItem().getId())
                        .itemName(data.getItem().getName())
                        .quantity(data.getQuantity())
                        .build())
                .toList();
        return ResponseEntity.ok(new ResponseDto<>(responses));
    }
}