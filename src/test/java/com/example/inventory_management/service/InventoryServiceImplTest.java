package com.example.inventory_management.service;

import com.example.inventory_management.dto.InventoryDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Inventory;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.InventoryRepository;
import com.example.inventory_management.repository.ItemRepository;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.impl.InventoryServiceImpl;
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

import static com.example.inventory_management.util.ResponseEnum.INVENTORY_NOT_FOUND;
import static com.example.inventory_management.util.ResponseEnum.ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @InjectMocks
    private InventoryServiceImpl inventoryService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private InventoryRepository inventoryRepository;

    @Test
    void getAllItemsNoFilter() {
        Item item = new Item();
        item.setId("UUID-ITEM");
        item.setName("Test Item");
        item.setPrice(100);

        Inventory inventory1= new Inventory();
        inventory1.setId("UUID-INV-1");
        inventory1.setItem(item);
        inventory1.setItemId(item.getId());
        inventory1.setQuantity(10);
        inventory1.setType("T");

        Inventory inventory2= new Inventory();
        inventory2.setId("UUID-INV-1");
        inventory2.setItem(item);
        inventory2.setItemId(item.getId());
        inventory2.setQuantity(5);
        inventory2.setType("W");

        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(inventory1);
        inventoryList.add(inventory2);

        Page<Inventory> inventoryPage = new PageImpl<>(inventoryList);

        when(inventoryRepository.findAll(PageRequest.of(0,10))).thenReturn(inventoryPage);
        ResponseEntity<ResponseDto<List<InventoryDto.Response>>> response = inventoryService.list("", 0, 10);
        assertThat(response.getBody().payload).hasSize(2);
    }

    @Test
    void getAllItemsWithFilter() {
        Item item1 = new Item();
        item1.setId("UUID-ITEM-1");
        item1.setName("Test Item");
        item1.setPrice(100);

        Item item2 = new Item();
        item2.setId("UUID-ITEM-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        Inventory inventory1= new Inventory();
        inventory1.setId("UUID-INV-1");
        inventory1.setItem(item1);
        inventory1.setItemId(item1.getId());
        inventory1.setQuantity(10);
        inventory1.setType("T");

        Inventory inventory2= new Inventory();
        inventory2.setId("UUID-INV-1");
        inventory2.setItem(item2);
        inventory2.setItemId(item2.getId());
        inventory2.setQuantity(5);
        inventory2.setType("W");

        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(inventory1);

        when(inventoryRepository.findAllByItemId("UUID-ITEM-1", PageRequest.of(0,10))).thenReturn(inventoryList);
        ResponseEntity<ResponseDto<List<InventoryDto.Response>>> response = inventoryService.list("UUID-ITEM-1", 0, 10);
        assertThat(response.getBody().payload).hasSize(1);
    }

    @Test
    void getAllItemsErrorPage() {
        Exception blob = null;
        try {
            inventoryService.list("", -1, 10);
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
            inventoryService.list("", 0, 0);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Minimum Size Is 1 and Max Size is 10000");
    }

    @Test
    void createInventory() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(100);

        InventoryDto.CreateRequest request = new InventoryDto.CreateRequest();
        request.setItemId("UUID-ITEM-1");
        request.setQuantity(10);
        request.setType("T");

        Inventory inventory = new Inventory();
        inventory.setType("T");
        inventory.setQuantity(10);
        inventory.setItemId(item.getId());

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);

        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(inventoryRepository.saveAndFlush(inventory)).thenReturn(inventory);
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = inventoryService.create(request);
        assertThat(response.getBody().payload).isNotNull();
    }

    @Test
    void createInventoryErrNotFound() {
        InventoryDto.CreateRequest request = new InventoryDto.CreateRequest();
        request.setItemId("UUID-ITEM-1");
        request.setQuantity(10);
        request.setType("T");

        when(itemRepository.findById("UUID-ITEM-1")).thenThrow(new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            inventoryService.create(request);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Inventory Not Found");
    }

    @Test
    void createInventoryErrStock() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(100);

        InventoryDto.CreateRequest request = new InventoryDto.CreateRequest();
        request.setItemId("UUID-ITEM-1");
        request.setQuantity(100);
        request.setType("W");

        Inventory inventory = new Inventory();
        inventory.setType("W");
        inventory.setQuantity(100);
        inventory.setItemId(item.getId());

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(10);

        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(inventoryRepository.saveAndFlush(inventory)).thenReturn(inventory);
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);

        Exception blob = null;
        try {
            inventoryService.create(request);
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Stock Is Not Sufficient For This Action");
    }

    @Test
    void getInventoryErrNotFound() {
        when(inventoryRepository.findById("UUID-INV-1")).thenThrow(new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));

        Exception blob = null;
        try {
            inventoryService.get("UUID-INV-1");
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Inventory Not Found");
    }

    @Test
    void getInventory() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(100);

        Inventory inventory = new Inventory();
        inventory.setType("W");
        inventory.setQuantity(100);
        inventory.setItemId("UUID-ITEM-1");
        inventory.setItem(item);

        when(inventoryRepository.findById("UUID-INV-1")).thenReturn(Optional.of(inventory));

        ResponseEntity<ResponseDto<InventoryDto.Response>> response = inventoryService.get("UUID-INV-1");
        assertThat(response.getBody().payload).isNotNull();
    }

    @Test
    void updateInventoryErrNotFound() {
        InventoryDto.UpdateRequest request = new InventoryDto.UpdateRequest();

        when(inventoryRepository.findById("UUID-INV-1")).thenThrow(new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));

        Exception blob = null;
        try {
            inventoryService.update("UUID-INV-1", request);
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Inventory Not Found");
    }

    @Test
    void updateInventoryErrNotSufficient() {
        InventoryDto.UpdateRequest request = new InventoryDto.UpdateRequest();
        request.setQuantity(100);
        request.setType("W");

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(100);

        Inventory inventory = new Inventory();
        inventory.setType("T");
        inventory.setQuantity(100);
        inventory.setItemId("UUID-ITEM-1");
        inventory.setItem(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);

        when(inventoryRepository.findById("UUID-INV-1")).thenReturn(Optional.of(inventory));
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);

        Exception blob = null;
        try {
            inventoryService.update("UUID-INV-1", request);
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Stock Is Not Sufficient For This Action");
    }

    @Test
    void updateInventory() {
        InventoryDto.UpdateRequest request = new InventoryDto.UpdateRequest();
        request.setQuantity(80);
        request.setType("T");

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(100);

        Inventory inventory = new Inventory();
        inventory.setType("T");
        inventory.setQuantity(100);
        inventory.setItemId("UUID-ITEM-1");
        inventory.setItem(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);

        when(inventoryRepository.findById("UUID-INV-1")).thenReturn(Optional.of(inventory));
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);
        when(inventoryRepository.saveAndFlush(inventory)).thenReturn(inventory);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);

        ResponseEntity<ResponseDto<InventoryDto.Response>> response = inventoryService.update("UUID-INV-1", request);
        assertThat(response.getBody().payload).isNotNull();
    }

    @Test
    void deleteInventoryErrNotFound() {
        when(inventoryRepository.findById("UUID-INV-1")).thenThrow(new ResponseException(INVENTORY_NOT_FOUND.code(), INVENTORY_NOT_FOUND.message(), INVENTORY_NOT_FOUND.httpStatus()));

        Exception blob = null;
        try {
            inventoryService.delete("UUID-INV-1");
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Inventory Not Found");
    }

    @Test
    void deleteinventoryErrAction() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(100);

        Inventory inventory = new Inventory();
        inventory.setType("T");
        inventory.setQuantity(100);
        inventory.setItemId("UUID-ITEM-1");
        inventory.setItem(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(0);

        when(inventoryRepository.findById("UUID-INV-1")).thenReturn(Optional.of(inventory));
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);

        Exception blob = null;
        try {
            inventoryService.delete("UUID-INV-1");
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Cannot Delete Inventory Record. Stock Will Be In Minus If This Record Is Deleted");
    }

    @Test
    void deleteInventory() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(10);

        Inventory inventory = new Inventory();
        inventory.setType("T");
        inventory.setQuantity(10);
        inventory.setItemId("UUID-ITEM-1");
        inventory.setItem(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(100);

        when(inventoryRepository.findById("UUID-INV-1")).thenReturn(Optional.of(inventory));
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = inventoryService.delete("UUID-INV-1");
        assertThat(response.getBody().payload).isNotNull();
        verify(inventoryRepository, times(1)).delete(inventory);
    }

    @Test
    void deleteInventoryWithType() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("TEST NAME");
        item.setPrice(10);

        Inventory inventory = new Inventory();
        inventory.setType("W");
        inventory.setQuantity(10);
        inventory.setItemId("UUID-ITEM-1");
        inventory.setItem(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(100);

        when(inventoryRepository.findById("UUID-INV-1")).thenReturn(Optional.of(inventory));
        when(stockRepository.findByItemId(item.getId())).thenReturn(stock);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);
        ResponseEntity<ResponseDto<InventoryDto.Response>> response = inventoryService.delete("UUID-INV-1");
        assertThat(response.getBody().payload).isNotNull();
        verify(inventoryRepository, times(1)).delete(inventory);
    }
}
