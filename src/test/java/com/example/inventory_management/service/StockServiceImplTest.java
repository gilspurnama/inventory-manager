package com.example.inventory_management.service;

import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.dto.StockDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.OrderItem;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.inventory_management.util.ResponseEnum.ORDER_ITEM_NOT_FOUND;
import static com.example.inventory_management.util.ResponseEnum.STOCK_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTest {
    @InjectMocks
    private StockServiceImpl stockService;
    @Mock
    private StockRepository stockRepository;

    @Test
    void getAllStocksErrorPage() {
        Exception blob = null;
        try {
            stockService.list("", -1, 10);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Page Start From 0");
    }

    @Test
    void getAllStocksErrorSize() {
        Exception blob = null;
        try {
            stockService.list("", 0, 0);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Minimum Size Is 1 and Max Size is 10000");
    }

    @Test
    void getAllStocks() {
        Item item1 = new Item();
        item1.setId("UUID-ITEM-1");
        item1.setName("Test Item");
        item1.setPrice(100);

        Stock stock1 = new Stock();
        stock1.setItemId(item1.getId());
        stock1.setItem(item1);
        stock1.setQuantity(10);

        Item item2 = new Item();
        item2.setId("UUID-ITEM-1");
        item2.setName("Test Item");
        item2.setPrice(100);

        Stock stock2 = new Stock();
        stock2.setItemId(item2.getId());
        stock2.setItem(item2);
        stock2.setQuantity(10);

        List<Stock> stockList = new ArrayList<>();
        stockList.add(stock1);
        stockList.add(stock2);

        Page<Stock> stockPage = new PageImpl<>(stockList);

        when(stockRepository.findAll(PageRequest.of(0,10))).thenReturn(stockPage);
        ResponseEntity<ResponseDto<List<StockDto.Response>>> response = stockService.list("", 0, 10);
        assertThat(response.getBody().payload).hasSize(2);
    }

    @Test
    void getAllOrderItemsWithFilter() {

        Item item2 = new Item();
        item2.setId("UUID-ITEM-1");
        item2.setName("Test Item");
        item2.setPrice(100);

        Stock stock2 = new Stock();
        stock2.setItem(item2);
        stock2.setItemId(item2.getId());
        stock2.setQuantity(10);

        List<Stock> stockList = new ArrayList<>();
        stockList.add(stock2);

        when(stockRepository.findAllByItemId("UUID-ITEM-2", PageRequest.of(0,10))).thenReturn(stockList);
        ResponseEntity<ResponseDto<List<StockDto.Response>>> response = stockService.list("UUID-ITEM-2", 0, 10);
        assertThat(response.getBody().payload).hasSize(1);
    }
    @Test
    void getStock() {
        Item item2 = new Item();
        item2.setId("UUID-ITEM-1");
        item2.setName("Test Item");
        item2.setPrice(100);

        Stock stock2 = new Stock();
        stock2.setId("UUID-STOCK-1");
        stock2.setItem(item2);
        stock2.setItemId(item2.getId());
        stock2.setQuantity(10);

        when(stockRepository.findById("UUID-STOCK-1")).thenReturn(Optional.of(stock2));
        ResponseEntity<ResponseDto<StockDto.Response>> response = stockService.get("UUID-STOCK-1");
        assertThat(response.getBody().payload.getId()).isEqualTo("UUID-STOCK-1");
    }

    @Test
    void getOrderItemNotFound() {
        when(stockRepository.findById("UUID-STOCK-1")).thenThrow(new ResponseException(STOCK_NOT_FOUND.code(), STOCK_NOT_FOUND.message(), STOCK_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            stockService.get("UUID-STOCK-1");
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Stock Not Found");
    }

}
