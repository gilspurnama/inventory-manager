package com.example.inventory_management.util;

import org.springframework.http.HttpStatus;

public enum ResponseEnum {
    DELETE_SUCCESS                          (200, HttpStatus.OK, "Success to Delete"),
    UPDATE_SUCCESS                          (200, HttpStatus.OK, "Success to Update"),
    CREATE_SUCCESS                          (200, HttpStatus.OK, "Success to Create"),
    PAGE_START                              (50002, HttpStatus.BAD_REQUEST, "Page Start From 0"),
    MINIMUM_MAXIMUM_SIZE                    (50003, HttpStatus.BAD_REQUEST, "Minimum Size Is 1 and Max Size is 10000"),
    STOCK_NOT_SUFFICIENT                    (50005, HttpStatus.BAD_REQUEST, "Stock Is Not Sufficient For This Action"),
    ITEM_NOT_FOUND                          (51000, HttpStatus.NOT_FOUND, "Item Not Found"),
    ITEM_NAME_ALREADY_EXIST                 (51001, HttpStatus.BAD_REQUEST, "Item Name Already Exist"),
    INVENTORY_NOT_FOUND                     (52000, HttpStatus.NOT_FOUND, "Inventory Not Found"),
    DELETE_INVENTORY_ERROR                  (52001, HttpStatus.BAD_REQUEST, "Cannot Delete Inventory Record. Stock Will Be In Minus If This Record Is Deleted"),
    ORDER_ITEM_NOT_FOUND                    (53000, HttpStatus.NOT_FOUND, "Order Item Not Found"),
    STOCK_NOT_FOUND                         (54000, HttpStatus.NOT_FOUND, "Stock Not Found");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    ResponseEnum(Integer code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public HttpStatus httpStatus() {
        return this.httpStatus;
    }

    public String message() {
        return this.message;
    }

}
