package com.example.inventory_management.service;

import com.example.inventory_management.dto.ItemDto;
import com.example.inventory_management.dto.OrderItemDto;
import com.example.inventory_management.dto.ResponseDto;
import com.example.inventory_management.exception.ResponseException;
import com.example.inventory_management.model.Item;
import com.example.inventory_management.model.OrderItem;
import com.example.inventory_management.model.Stock;
import com.example.inventory_management.repository.ItemRepository;
import com.example.inventory_management.repository.OrderItemRepository;
import com.example.inventory_management.repository.StockRepository;
import com.example.inventory_management.service.impl.OrderItemServiceImpl;
import org.aspectj.weaver.ast.Or;
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

import static com.example.inventory_management.util.ResponseEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceImplTest {

    @InjectMocks
    private OrderItemServiceImpl orderItemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void createOrderItem() {
        OrderItemDto.CreateRequest request = new OrderItemDto.CreateRequest();
        request.setItemId("UUID-ITEM-1");
        request.setQuantity(10);

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(100);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(10);
        orderItem.setItemId(request.getItemId());

        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(stockRepository.findByItemId(request.getItemId())).thenReturn(stock);
        when(orderItemRepository.saveAndFlush(orderItem)).thenReturn(orderItem);

        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = orderItemService.create(request);
        assertThat(response.getBody().payload).isNotNull();
    }

    @Test
    void createOrderItemErrNotSufficient() {
        OrderItemDto.CreateRequest request = new OrderItemDto.CreateRequest();
        request.setItemId("UUID-ITEM-1");
        request.setQuantity(100);

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(10);

        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(stockRepository.findByItemId(request.getItemId())).thenReturn(stock);

        Exception blob = null;
        try {
            orderItemService.create(request);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Stock Is Not Sufficient For This Action");
    }

    @Test
    void updateOrderItemErrNotFound() {
        when(orderItemRepository.findById("UUID-OI-1")).thenThrow(new ResponseException(ORDER_ITEM_NOT_FOUND.code(), ORDER_ITEM_NOT_FOUND.message(), ORDER_ITEM_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            orderItemService.update("UUID-OI-1", new OrderItemDto.UpdateRequest());
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Order Item Not Found");
    }

    @Test
    void updateOrderItem() {
        OrderItemDto.UpdateRequest request = new OrderItemDto.UpdateRequest();
        request.setQuantity(10);

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(100);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(100);
        orderItem.setItemId(item.getId());
        orderItem.setItem(item);

        when(orderItemRepository.findById("UUID-OI-1")).thenReturn(Optional.of(orderItem));
        when(stockRepository.findByItemId(orderItem.getItemId())).thenReturn(stock);
        when(orderItemRepository.saveAndFlush(orderItem)).thenReturn(orderItem);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);

        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = orderItemService.update("UUID-OI-1", request);
        assertThat(response.getBody().payload).isNotNull();
    }

    @Test
    void updateOrderItemWithLessStock() {
        OrderItemDto.UpdateRequest request = new OrderItemDto.UpdateRequest();
        request.setQuantity(30);

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(100);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(10);
        orderItem.setItemId(item.getId());
        orderItem.setItem(item);

        when(orderItemRepository.findById("UUID-OI-1")).thenReturn(Optional.of(orderItem));
        when(stockRepository.findByItemId(orderItem.getItemId())).thenReturn(stock);
        when(orderItemRepository.saveAndFlush(orderItem)).thenReturn(orderItem);
        when(stockRepository.saveAndFlush(stock)).thenReturn(stock);

        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = orderItemService.update("UUID-OI-1", request);
        assertThat(response.getBody().payload).isNotNull();
    }

    @Test
    void updateOrderItemErrStockNotSufficient() {
        OrderItemDto.UpdateRequest request = new OrderItemDto.UpdateRequest();
        request.setQuantity(200);

        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(10);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(100);
        orderItem.setItemId(item.getId());
        orderItem.setItem(item);

        when(orderItemRepository.findById("UUID-OI-1")).thenReturn(Optional.of(orderItem));
        when(stockRepository.findByItemId(orderItem.getItemId())).thenReturn(stock);

        Exception blob = null;
        try {
            orderItemService.update("UUID-OI-1", request);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Stock Is Not Sufficient For This Action");
    }

    @Test
    void getOrderItem() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        OrderItem orderItem = new OrderItem();
        orderItem.setId("UUID-OI-1");
        orderItem.setQuantity(100);
        orderItem.setItemId(item.getId());
        orderItem.setItem(item);

        when(orderItemRepository.findById("UUID-OI-1")).thenReturn(Optional.of(orderItem));
        ResponseEntity<ResponseDto<OrderItemDto.Response>> response = orderItemService.get("UUID-OI-1");
        assertThat(response.getBody().payload.getId()).isEqualTo("UUID-OI-1");
    }

    @Test
    void getOrderItemNotFound() {
        when(orderItemRepository.findById("UUID-OI-1")).thenThrow(new ResponseException(ORDER_ITEM_NOT_FOUND.code(), ORDER_ITEM_NOT_FOUND.message(), ORDER_ITEM_NOT_FOUND.httpStatus()));
        Exception blob = null;
        try {
            orderItemService.get("UUID-OI-1");
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Order Item Not Found");
    }

    @Test
    void getAllOrderItems() {
        Item item1 = new Item();
        item1.setId("UUID-TEST-1");
        item1.setName("Test Item");
        item1.setPrice(100);

        Item item2 = new Item();
        item2.setId("UUID-TEST-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId("UUID-OI-1");
        orderItem1.setQuantity(100);
        orderItem1.setItemId(item1.getId());
        orderItem1.setItem(item1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId("UUID-OI-2");
        orderItem2.setQuantity(100);
        orderItem2.setItemId(item2.getId());
        orderItem2.setItem(item2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);

        Page<OrderItem> orderItemPage = new PageImpl<>(orderItems);

        when(orderItemRepository.findAll(PageRequest.of(0,10))).thenReturn(orderItemPage);
        ResponseEntity<ResponseDto<List<OrderItemDto.Response>>> response = orderItemService.list("", 0, 10);
        assertThat(response.getBody().payload).hasSize(2);
    }

    @Test
    void getAllOrderItemsWithFilter() {
        Item item1 = new Item();
        item1.setId("UUID-TEST-1");
        item1.setName("Test Item");
        item1.setPrice(100);

        Item item2 = new Item();
        item2.setId("UUID-TEST-2");
        item2.setName("Test Item");
        item2.setPrice(100);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId("UUID-OI-1");
        orderItem1.setQuantity(100);
        orderItem1.setItemId(item1.getId());
        orderItem1.setItem(item1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId("UUID-OI-2");
        orderItem2.setQuantity(100);
        orderItem2.setItemId(item2.getId());
        orderItem2.setItem(item2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem2);

        when(orderItemRepository.findAllByItemId("UUID-TEST-2", PageRequest.of(0,10))).thenReturn(orderItems);
        ResponseEntity<ResponseDto<List<OrderItemDto.Response>>> response = orderItemService.list("UUID-TEST-2", 0, 10);
        assertThat(response.getBody().payload).hasSize(1);
    }

    @Test
    void getAllOrderItemsErrorPage() {
        Exception blob = null;
        try {
            orderItemService.list("", -1, 10);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Page Start From 0");
    }

    @Test
    void getAllOrderItemsErrorSize() {
        Exception blob = null;
        try {
            orderItemService.list("", 0, 0);
        } catch (Exception e) {
            blob = e;
        }
        assert blob != null;
        assertThat(blob.getMessage()).isEqualTo("Minimum Size Is 1 and Max Size is 10000");
    }

    @Test
    void deleteOrderItemErrNotFound() {
        when(orderItemRepository.findById("UUID-OI-1")).thenThrow(new ResponseException(ORDER_ITEM_NOT_FOUND.code(), ORDER_ITEM_NOT_FOUND.message(), ORDER_ITEM_NOT_FOUND.httpStatus()));

        Exception blob = null;
        try {
            orderItemService.delete("UUID-OI-1");
        } catch (Exception e) {
            blob = e;
        }
        assertThat(blob.getMessage()).isEqualTo("Order Item Not Found");
    }

    @Test
    void deleteOrderItem() {
        Item item = new Item();
        item.setId("UUID-ITEM-1");
        item.setName("Test Item");
        item.setPrice(100);

        OrderItem orderItem = new OrderItem();
        orderItem.setId("UUID-OI-1");
        orderItem.setQuantity(100);
        orderItem.setItemId(item.getId());
        orderItem.setItem(item);

        Stock stock = new Stock();
        stock.setItemId(item.getId());
        stock.setQuantity(10);

        when(orderItemRepository.findById("UUID-OI-1")).thenReturn(Optional.of(orderItem));
        when(stockRepository.findByItemId(orderItem.getItemId())).thenReturn(stock);
        orderItemService.delete("UUID-OI-1");
        verify(orderItemRepository, times(1)).delete(orderItem);

    }

}
