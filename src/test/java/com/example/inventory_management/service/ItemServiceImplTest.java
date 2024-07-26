package com.example.inventory_management.service;

import com.example.inventory_management.dto.ItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.ItemRepository;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.inventory_management.util.ResponseEnum.ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StockRepository stockRepository;

    @Test
    void createItem() {
        ItemDto.CreateRequest request = new ItemDto.CreateRequest();
        request.setName("Test Item");
        request.setPrice(100);

        Item item = new Item();
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);

        when(itemRepository.existsByName("Test Item")).thenReturn(false);
        when(itemRepository.save(item)).thenReturn(item);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);

        ResponseEntity<ResponseDto<ItemDto.Response>> response = itemService.create(request);
        assertThat(response.getBody().payload.getName()).isEqualTo("Test Item");
        assertThat(response.getBody().payload.getPrice()).isEqualTo(100);
    }

    @Test
    void createItemNameAlreadyExist() throws Exception {
        ItemDto.CreateRequest request = new ItemDto.CreateRequest();
        request.setName("Test Item");
        request.setPrice(100);

        when(itemRepository.existsByName("Test Item")).thenReturn(true);

        Exception blob = null;
        try {
            itemService.create(request);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Item Name Already Exist");
    }

    @Test
    void getItem() {
        Item item = new Item();
        item.setId("UUID-TEST");
        item.setName("Test Item");
        item.setPrice(100);

        when(itemRepository.findById("UUID-TEST")).thenReturn(Optional.of(item));
        ResponseEntity<ResponseDto<ItemDto.Response>> response = itemService.get("UUID-TEST");
        assertThat(response.getBody().payload.getId()).isEqualTo("UUID-TEST");
    }

    @Test
    void getItemNotFound() {
        when(itemRepository.findById("UUID-TEST")).thenThrow(new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            itemService.get("UUID-TEST");
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Item Not Found");
    }

    @Test
    void getAllItems() {
        Item item1 = new Item();
        item1.setId("UUID-TEST-1");
        item1.setName("Test Item");
        item1.setPrice(100);

        Item item2 = new Item();
        item2.setId("UUID-TEST-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item2);
        itemList.add(item1);

        when(itemRepository.findAllByName("", PageRequest.of(0,10))).thenReturn(itemList);
        ResponseEntity<ResponseDto<List<ItemDto.Response>>> response = itemService.list("", 0, 10);
        assertThat(response.getBody().payload).hasSize(2);
    }

    @Test
    void getAllItemsErrorPage() {
        Exception blob = null;
        try {
            itemService.list("", -1, 10);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Page Start From 0");
    }

    @Test
    void getAllItemsErrorSize() {
        Exception blob = null;
        try {
            itemService.list("", 0, 0);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Minimum Size Is 1 and Max Size is 10000");
    }

    @Test
    void updateItem() {
        ItemDto.UpdateRequest request = new ItemDto.UpdateRequest();
        request.setName("Update Test Name");
        request.setPrice(200);

        Item item = new Item();
        item.setId("UUID-TEST-1");
        item.setName("Test Item");
        item.setPrice(100);

        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("Update Test Name");
        updatedItem.setPrice(200);

        when(itemRepository.findById("UUID-TEST")).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(item)).thenReturn(updatedItem);
        ResponseEntity<ResponseDto<ItemDto.Response>> response = itemService.update("UUID-TEST", request);
        assertThat(response.getBody().payload.getName()).isEqualTo("Update Test Name");
        assertThat(response.getBody().payload.getPrice()).isEqualTo(200);
    }


    @Test
    void updateItemNotFound() {
        ItemDto.UpdateRequest request = new ItemDto.UpdateRequest();
        request.setName("Update Test Name");
        request.setPrice(200);

        when(itemRepository.findById("UUID-TEST")).thenThrow(new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            itemService.update("UUID-TEST", request);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Item Not Found");
    }

    @Test
    void deleteItem() {
        Item item = new Item();
        item.setId("UUID-TEST");
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);

        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);
        when(itemRepository.findById("UUID-TEST")).thenReturn(Optional.of(item));

        ResponseEntity<ResponseDto<ItemDto.Response>> response = itemService.delete("UUID-TEST");
        verify(itemRepository, times(1)).delete(item);
        assertThat(response.getBody().payload.getId()).isEqualTo("UUID-TEST");
    }

    @Test
    void deleteItemNotFound() {
        when(itemRepository.findById("UUID-TEST")).thenThrow(new ResponseException(ITEM_NOT_FOUND.code(), ITEM_NOT_FOUND.message(), ITEM_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            itemService.delete("UUID-TEST");
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Item Not Found");
    }

}
