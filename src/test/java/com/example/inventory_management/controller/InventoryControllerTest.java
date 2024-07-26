package com.example.inventory_management.controller;

import com.example.inventory_management.dto.InventoryDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.model.Inventory;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.service.InventoryService;
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

@WebMvcTest(value = InventoryController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class InventoryControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    InventoryService inventoryService;

    @Test
    void getItemMethod() throws Exception {
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .type("T")
                .build()));

        when(inventoryService.get("UUID-ITEM-1")).thenReturn(response);
        this.mockMvc.perform(get("/inventories/UUID-ITEM-1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void listItem() throws Exception {
        Item item2 = new Item();
        item2.setId("UUID-TEST-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        Inventory inventory = new Inventory();
        inventory.setItem(item2);
        inventory.setItemId(item2.getId());
        inventory.setType("T");
        inventory.setQuantity(100);

        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(inventory);

        List<InventoryDto.Response> response = inventoryList.stream().map(data -> InventoryDto.Response.builder()
                        .id(data.getId())
                        .quantity(data.getQuantity())
                        .itemId(data.getItem().getId())
                        .itemName(data.getItem().getName())
                        .type(data.getType())
                        .build())
                .toList();
        ResponseEntity<ResponseDto<List<InventoryDto.Response>>> responseEntity = ResponseEntity.ok(new ResponseDto<>(response));

        when(inventoryService.list("", 0, 1)).thenReturn(responseEntity);
        this.mockMvc.perform(get("/inventories?name=&page="))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("")))
                .andDo(print());
    }

    @Test
    void postCreate() throws Exception {
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .type("T")
                .build()));

        InventoryDto.CreateRequest request = new InventoryDto.CreateRequest();
        request.setType("T");
        request.setQuantity(100);
        request.setItemId("UUID-ITEM-1");

        when(inventoryService.create(request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void putUpdate() throws Exception {
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .type("T")
                .build()));

        InventoryDto.UpdateRequest request = new InventoryDto.UpdateRequest();
        request.setType("T");
        request.setQuantity(100);

        when(inventoryService.update("UUID-ITEM-1", request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(put("/inventories/UUID-ITEM-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteItem() throws Exception {
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = ResponseEntity.ok(new ResponseDto<>(InventoryDto.Response.builder()
                .id("UUID-INV-1")
                .itemName("TEST NAME")
                .itemId("UUID-TEST-1")
                .quantity(100)
                .type("T")
                .build()));

        when(inventoryService.delete("UUID-ITEM-1")).thenReturn(response);
        this.mockMvc.perform(delete("/inventories/UUID-ITEM-1"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
