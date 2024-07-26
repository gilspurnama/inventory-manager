package com.example.inventory_management.controller;

import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.OrderItem;
import com.example.inventory_management.service.OrderItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

@WebMvcTest(value = OrderItemController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class OrderItemControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    OrderItemService orderItemService;

    @Test
    void getItemMethod() throws Exception {
        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id("UUID-OI-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .build()));

        when(orderItemService.get("UUID-OI-1")).thenReturn(response);
        this.mockMvc.perform(get("/order-items/UUID-OI-1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void listItem() throws Exception {

        Item item2 = new Item();
        item2.setId("UUID-TEST-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId("UUID-OI-2");
        orderItem2.setQuantity(100);
        orderItem2.setItemId(item2.getId());
        orderItem2.setItem(item2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem2);

        List<OrderItemDto.Response> response = orderItems.stream().map(data -> OrderItemDto.Response.builder()
                        .id(data.getId())
                        .quantity(data.getQuantity())
                        .itemId(data.getItem().getId())
                        .itemName(data.getItem().getName())
                        .build())
                .toList();
        ResponseEntity<ResponseDto<List<OrderItemDto.Response>>> responseEntity = ResponseEntity.ok(new ResponseDto<>(response));

        when(orderItemService.list("", 0, 1)).thenReturn(responseEntity);
        this.mockMvc.perform(get("/order-items?name=&page="))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("")))
                .andDo(print());
    }

    @Test
    void postCreate() throws Exception {
        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .build()));

        OrderItemDto.CreateRequest request = new OrderItemDto.CreateRequest();
        request.setQuantity(100);
        request.setItemId("UUID-ITEM-1");

        when(orderItemService.create(request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void putUpdate() throws Exception {
        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .build()));

        OrderItemDto.UpdateRequest request = new OrderItemDto.UpdateRequest();
        request.setQuantity(100);

        when(orderItemService.update("UUID-ITEM-1", request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(put("/order-items/UUID-ITEM-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteItem() throws Exception {
        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(OrderItemDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .build()));

        when(orderItemService.delete("UUID-ITEM-1")).thenReturn(response);
        this.mockMvc.perform(delete("/order-items/UUID-ITEM-1"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
