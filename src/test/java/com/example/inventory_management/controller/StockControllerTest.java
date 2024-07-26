package com.example.inventory_management.controller;

import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.dto.StockDto;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = StockController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class StockControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    StockService stockService;

    @Test
    void getItemMethod() throws Exception {
        ResponseEntity<ResponseDto<StockDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(StockDto.Response.builder()
                .id("UUID-STOCK-1")
                .itemId("UUID-ITEM-1")
                .itemName("ITEM NAME")
                .quantity(100)
                .build()));

        when(stockService.get("UUID-STOCK-1")).thenReturn(response);
        this.mockMvc.perform(get("/stocks/UUID-STOCK-1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void listItem() throws Exception {

        Item item2 = new Item();
        item2.setId("UUID-ITEM-1");
        item2.setName("Test Item");
        item2.setPrice(100);

        Stock stock2 = new Stock();
        stock2.setId("UUID-STOCK-2");
        stock2.setItem(item2);
        stock2.setItemId(item2.getId());
        stock2.setQuantity(10);

        List<Stock> stockList = new ArrayList<>();
        stockList.add(stock2);

        List<StockDto.Response> response = stockList.stream().map(data -> StockDto.Response.builder()
                        .id(data.getId())
                        .itemId(data.getItem().getId())
                        .itemName(data.getItem().getName())
                        .quantity(data.getQuantity())
                        .build())
                .toList();
        ResponseEntity<ResponseDto<List<StockDto.Response>>> responseEntity = ResponseEntity.ok(new ResponseDto<>(response));

        when(stockService.list("", 0, 1)).thenReturn(responseEntity);
        this.mockMvc.perform(get("/stocks?name=&page="))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("")))
                .andDo(print());
    }
}
