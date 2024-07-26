# Introduction
This is a backend-side application for simple inventory management.
This application uses Java as the main programming language with Spring-Boot 3 as the framework.

## Tech Stack
- Java 17
- Spring Boot 3.3.2
- H2
- Open API 2.5

# Module
- Item
  The purpose of Item is for all items that are stored in the database
  attributes:
    - ID (UUID)
    - Name (String)
    - Price (Integer)
 
- Inventory
  The purpose of this module is to record all storing actions like top-up and withdrawal
  attributes:
    - ID (UUID)
    - Item ID (Item ID - UUID)
    - Quantity (Integer)
    - Type (String - "T" for top-up, "W" for withdrawal
      
- Order Item
  Order Items is to record all order items that happened
  attributes:
    - ID (UUID)
    - Item ID (Item ID - UUID)
    - Quantity (Integer)
      
- Stock
  Stock is the total number of items that are still in the database
  attributes:
   - ID (UUID)
   - Item ID (Item ID - UUID)
   - Quantity (Integer)
