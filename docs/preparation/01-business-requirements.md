## 1. Project Overview

The project is a demo online clothing store. Visitors can browse clothing products, manage a shopping cart, and place a demo order. An administrator can manage products, inventory, and orders.

The project is intended for learning CRUD operations, Spring Boot, JPA, Hibernate, and basic transactions. It does not process real payments or arrange real deliveries.

## 2. Administrator

- Can create, view, update, and hide products, including their name, description, category, price, image, sizes, stock quantity, and sale status.
- Can add / remove / update a specific product
- Can cancel specific orders which not allowed or invalid
- Can view an order list and order details
- Can update an order's status according to the allowed transitions 
- Admin can only interact with the website only via Admin Login with username and password

## 3. Customer

- Can browse clothing products
- Manage a shopping cart ( add, remove, change cart items )
- Do not have to login or register for shopping service and product browsing
- Have to provide contact and delivery details for a demo order

## 4. Business Rules

- Product prices and stock quantities cannot be negative
- Ordered quantities must be positive whole numbers
- Only `ACTIVE` products with `sufficient stock` can be ordered
- The order's total price is calculated from each item's price at order time multiplied by its quantity
- Each order item retains its unit price at the time the order was created, even if the product price changes later.
- An order lookup succeeds only when both the order code and the checkout phone number match.
- Demo orders do not collect payment and do not represent a real delivery process.
- Order records and their item details are not hard-deleted.

## 5. Order Placement Flow

1. Customer browse products and add their preferences into their cart.
2. Customer provide contact information and delivery details via a form when they click on `Create Order` button.
3. The system validates the contact info and cart contents.
4. The system verifies if every product is `ACTIVE` and has enough stock.
5. The system creates the order and its order-item records.
6. The system decreases stock for each ordered-variant.
7. The system return the order code and confirmation to the customer.

***Step 3 to 6 must be handled as one transaction. If any step fails, there will be a rollback together. The customer then receives a clear failure message and can review cart again.***

## 6. Order Cancellation Flow

The administrator may cancel a `PENDING` or `CONFIRMED` order. The system changes its status to `CANCELLED` and restores the stock quantities from its order items in one transaction. If either operation fails, both the status and inventory remain unchanged.

The system must prevent a cancelled order from restoring inventory more than once.

## 7. Out of Scope for the MVP

- Real online payment or money handling.
- Shipping provider integration or shipment tracking.
- Customer registration and login.
- Coupons, product reviews, refunds, and returns.
- Automated email or SMS notifications.



