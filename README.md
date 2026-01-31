# Store Inventory + POS (Java Swing)

A Java Swing desktop application that simulates a small store system: manage inventory, register customers, create sales orders, and generate reports. Data persists locally using CSV files.

## Features
- Multi-screen Swing UI routed in a single window (CardLayout)
- Products module: CRUD, validation, sorting (merge sort), SKU search (binary search)
- Customers module: CRUD, regex validation (email/phone)
- Orders / POS module:
  - Build orders with multiple items (OrderItems)
  - Order statuses: DRAFT, PAID, CANCELLED
  - Stock checks before adding items
  - Stock deduction when an order is saved as PAID
- Reports module: text-based summaries built with StringBuilder
- Local persistence: CSV repositories (load on startup, save on exit)

## Project Structure (high-level)
- `model/`  
  Core entities: Product, Customer, Order, OrderItem, and shared base classes/interfaces.
- `repository/`  
  CSV-backed repositories with CRUD + load/save.
- `ui/`  
  Swing panels: Login, Dashboard, Products, Customers, Orders, Reports.
- `util/`  
  Validators (regex), CSV parsing/escaping, ID generation, algorithms (merge sort + binary search).

## Requirements
- Java (JDK 8+ recommended)
- NetBeans (recommended) or any IDE that supports Swing projects

## Run (NetBeans)
1. Open NetBeans.
2. `File -> Open Project` and select the project folder.
3. Run the main class (the application entry point) to launch the UI.

## Data Persistence
The app stores data in local CSV files (products, customers, orders). On startup it loads existing CSV data; on exit it saves current in-memory state back to CSV.

## Notes
- Input validation is enforced via shared validators (including regex rules).
- Orders saved as PAID update inventory stock accordingly.
- Sorting/search are implemented as explicit algorithms (merge sort, binary search) and used in the Products screen.

## License
MIT License (see LICENSE) 

## Author
Flavio Mëzuri
