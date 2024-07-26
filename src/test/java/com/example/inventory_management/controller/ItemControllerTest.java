package com.example.inventory_management.controller;

import com.example.inventory_management.dto.ItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.service.ItemService;
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

import static com.example.inventory_management.util.ResponseEnum.ITEM_NOT_FOUND;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    @Test
    void getItemMethod() throws Exception {
        ResponseEntity<ResponseDto<ItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id("UUID-ITEM-1")
                .name("TEST NAME")
                .price(100)
                .build()));

        when(itemService.get("UUID-ITEM-1")).thenReturn(response);
        this.mockMvc.perform(get("/items/UUID-ITEM-1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"payload\":{\"id\":\"UUID-ITEM-1\",\"name\":\"TEST NAME\",\"price\":100},\"message\":null,\"errorCode\":null}")))
                .andDo(print());
    }

    @Test
    void listItem() throws Exception {
        Item item2 = new Item();
        item2.setId("UUID-TEST-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item2);

        List<ItemDto.Response> response = itemList.stream().map(data -> ItemDto.Response.builder()
                        .id(data.getId())
                        .name(data.getName())
                        .price(data.getPrice())
                        .build())
                .toList();
        ResponseEntity<ResponseDto<List<ItemDto.Response>>> responseEntity = ResponseEntity.ok(new ResponseDto<>(response));

        when(itemService.list("", 0, 1)).thenReturn(responseEntity);
        this.mockMvc.perform(get("/items?name=&page="))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("")))
                .andDo(print());
    }

    @Test
    void postCreate() throws Exception {
        ResponseEntity<ResponseDto<ItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id("UUID-ITEM-1")
                .name("TEST NAME")
                .price(100)
                .build()));
        ItemDto.CreateRequest request = new ItemDto.CreateRequest();
        request.setPrice(100);
        request.setName("TEST NAME");

        when(itemService.create(request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void putUpdate() throws Exception {
        ResponseEntity<ResponseDto<ItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id("UUID-ITEM-1")
                .name("TEST NAME")
                .price(100)
                .build()));
        ItemDto.UpdateRequest request = new ItemDto.UpdateRequest();
        request.setPrice(100);
        request.setName("TEST NAME");

        when(itemService.update("UUID-ITEM-1", request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(put("/items/UUID-ITEM-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteItem() throws Exception {
        ResponseEntity<ResponseDto<ItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id("UUID-ITEM-1")
                .name("TEST NAME")
                .price(100)
                .build()));

        when(itemService.delete("UUID-ITEM-1")).thenReturn(response);
        this.mockMvc.perform(delete("/items/UUID-ITEM-1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteItemErr() throws Exception {
        ResponseEntity<ResponseDto<ItemDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(ItemDto.Response.builder()
                .id("UUID-ITEM-1")
                .name("TEST NAME")
                .price(100)
                .build()));

        when(itemService.delete("UUID-ITEM-1")).thenThrow(new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        this.mockMvc.perform(delete("/items/UUID-ITEM-1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
