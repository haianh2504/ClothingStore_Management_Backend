# Use Cases

```
Customer
├── Product
│   └── UC01 Browse Products
│
├── Cart / CartItem
│   ├── UC02 Add Product Variant to Cart
│   ├── UC03 View Cart
│   ├── UC04 Update Cart Item Quantity
│   └── UC05 Remove Item from Cart
│
└── Order
    ├── UC06 Place Order
    └── UC07 Look Up Order

Administrator
├── Authentication
│   └── UC08 Administrator Login
│
├── Product
│   ├── UC09 Create Product
│   ├── UC10 Update Product
│   └── UC14 Change Product Sale Status
│
├── ProductVariant
│   ├── UC11 Add Product Variant
│   ├── UC12 Update Product Variant
│   └── UC13 Remove Product Variant
│
└── Order
    ├── UC15 View Orders
    ├── UC16 Confirm Order
    └── UC17 Cancel Order
```

## UC01 Browse Products

**Actor:** Customer

**Preconditions:** None

**Main flow:**

1. The customer opens the product page.
2. The system retrieves products with the `ACTIVE` sale status.
3. The system displays the products and their basic information.
4. The customer browses and selects a product.
5. The system displays the selected product and its available variants.

**Alternative flows:**

- If no active products exist, the system displays an empty state.
- If the selected product no longer exists or is inactive, the system displays a product unavailable message.

**Postconditions:**

- No data is changed.

---

## UC02 Add Product Variant to Cart

**Actor:** Customer

**Preconditions:**

- The selected product has the `ACTIVE` sale status.
- The customer has selected a valid product variant.
- The selected variant has sufficient stock.

**Main flow:**

1. The customer selects a product.
2. The customer selects a size and color.
3. The customer enters the desired quantity.
4. The customer selects **Add to Cart**.
5. The frontend sends the guest identifier, product variant identifier, and quantity to the system.
6. The system finds or creates a cart associated with the guest identifier.
7. The system verifies that the product is active and the selected variant exists.
8. The system verifies that the requested quantity does not exceed the current stock.
9. The system adds the product variant to the cart.
10. The system returns the updated cart.
11. The frontend displays a successful addition message.

**Alternative flows:**

- If the selected variant does not exist, the system rejects the request.
- If the product is inactive, the system rejects the request.
- If the requested quantity exceeds the available stock, the system displays an insufficient stock message.
- If the same variant already exists in the cart, the system increases the existing cart item quantity instead of creating another cart item.
- If the total quantity after merging exceeds the available stock, the system rejects the update.

**Postconditions:**

- The product variant is stored in the customer's guest cart.
- Product stock is not decreased at this stage.

---

## UC03 View Cart

**Actor:** Customer

**Preconditions:**

- The customer has a guest identifier.

**Main flow:**

1. The customer opens the cart page.
2. The frontend sends the guest identifier to the system.
3. The system finds the cart associated with the guest identifier.
4. The system retrieves the cart items and their current product information.
5. The system calculates the current cart subtotal.
6. The system returns the cart details.
7. The frontend displays the cart items and subtotal.

**Alternative flows:**

- If no cart exists for the guest identifier, the system displays an empty cart.
- If a product has become inactive, the system marks the corresponding cart item as unavailable.
- If the requested quantity now exceeds the available stock, the system informs the customer that the quantity must be changed before checkout.

**Postconditions:**

- No data is changed.

---

## UC04 Update Cart Item Quantity

**Actor:** Customer

**Preconditions:**

- The guest cart exists.
- The cart item exists.
- The new quantity is a positive whole number.

**Main flow:**

1. The customer changes the quantity of a cart item.
2. The frontend sends the guest identifier, cart item identifier, and new quantity to the system.
3. The system verifies that the cart belongs to the guest identifier.
4. The system verifies that the product variant is still available.
5. The system verifies that the new quantity does not exceed the current stock.
6. The system updates the cart item quantity.
7. The system recalculates the cart subtotal.
8. The system returns the updated cart.
9. The frontend displays the updated cart.

**Alternative flows:**

- If the cart or cart item does not exist, the system returns a not found response.
- If the new quantity is less than one, the system rejects the request.
- If the new quantity exceeds the available stock, the system keeps the previous quantity and returns an insufficient stock message.
- If the product or variant is no longer available, the system informs the customer that the item cannot be purchased.

**Postconditions:**

- The cart item contains the new quantity.
- Product stock remains unchanged.

---

## UC05 Remove Item from Cart

**Actor:** Customer

**Preconditions:**

- The guest cart exists.
- The selected cart item exists.

**Main flow:**

1. The customer selects **Remove** for a cart item.
2. The frontend sends the guest identifier and cart item identifier to the system.
3. The system verifies that the cart item belongs to the guest cart.
4. The system removes the item from the cart.
5. The system recalculates the cart subtotal.
6. The system returns the updated cart.
7. The frontend displays the updated cart.

**Alternative flows:**

- If the cart does not exist, the system returns a not found response.
- If the cart item does not exist, the system returns a not found response.

**Postconditions:**

- The selected item is no longer present in the cart.
- Product stock remains unchanged.

---

## UC06 Place Order

**Actor:** Customer

**Preconditions:**

- The guest cart exists.
- The cart contains at least one item.
- The customer has provided valid contact information and a delivery address.

**Main flow:**

1. The customer reviews the cart.
2. The customer enters their name, phone number, optional email address, and delivery address.
3. The customer selects **Place Order**.
4. The frontend sends the guest identifier and customer information to the system.
5. The system retrieves the cart and its items.
6. The system validates the customer information.
7. The system verifies that every product is active.
8. The system verifies that every product variant exists and has sufficient stock.
9. The system retrieves current product prices from the database.
10. The system calculates the total price.
11. The system creates an order with the `PENDING` status.
12. The system creates an order item for each cart item.
13. The system stores the product name, size, color, quantity, and unit price in each order item.
14. The system decreases the stock quantity of each ordered product variant.
15. The system clears the cart.
16. The system commits the transaction.
17. The system returns the order code, total price, status, and creation time.
18. The frontend displays the order confirmation.

**Alternative flows:**

- If the cart is empty or does not exist, the system rejects the request.
- If the customer information is invalid, the system returns validation errors.
- If a product is inactive or unavailable, the system rejects the order.
- If any variant has insufficient stock, the system rejects the order and reports the affected item.
- If any operation fails while creating the order, creating order items, decreasing stock, or clearing the cart, the system rolls back the entire transaction.

**Postconditions:**

- A new order and its order items are stored.
- Product variant stock is decreased.
- The guest cart is empty.
- No partial changes remain if the transaction fails.

---

## UC07 Look Up Order

**Actor:** Customer

**Preconditions:**

- The customer has an order code.
- The customer knows the phone number used when placing the order.

**Main flow:**

1. The customer opens the order lookup page.
2. The customer enters the order code and phone number.
3. The frontend sends the provided information to the system.
4. The system searches for an order matching both values.
5. The system returns the order summary and order items.
6. The frontend displays the order details.

**Alternative flows:**

- If no order matches both the order code and phone number, the system returns a not found response.
- If either field is empty or invalid, the system returns a validation error.

**Postconditions:**

- No data is changed.

---

## UC08 Administrator Login

**Actor:** Administrator

**Preconditions:**

- An administrator account exists.
- The administrator is not currently authenticated.

**Main flow:**

1. The administrator opens the login page.
2. The administrator enters an email address and password.
3. The frontend sends the credentials to the system.
4. The system finds the administrator account by email.
5. The system verifies the submitted password against the stored password hash.
6. The system creates an authenticated session or authentication token.
7. The system returns a successful authentication response.
8. The frontend redirects the administrator to the management page.

**Alternative flows:**

- If the email or password is incorrect, the system rejects the login attempt.
- If required fields are missing, the system returns validation errors.

**Postconditions:**

- The administrator is authenticated and can access protected management functions.

---

## UC09 Create Product

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.

**Main flow:**

1. The administrator opens the product creation page.
2. The administrator enters the product name, description, category, base price, and sale status.
3. The administrator submits the product information.
4. The system validates the product information.
5. The system stores the product.
6. The system returns the created product.
7. The frontend displays a successful creation message.

**Alternative flows:**

- If required product information is missing or invalid, the system returns validation errors.
- If the base price is negative, the system rejects the request.

**Postconditions:**

- A new product exists in the system.
- Product variants can be added separately through UC11 Add Product Variant.

---

## UC10 Update Product

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The product exists.

**Main flow:**

1. The administrator opens the product edit page.
2. The system displays the current product information.
3. The administrator changes one or more product fields.
4. The administrator submits the changes.
5. The system validates the submitted product information.
6. The system applies the valid changes to the product.
7. The system returns the updated product.
8. The frontend displays a successful update message.

**Alternative flows:**

- If the product does not exist, the system returns a not found response.
- If the submitted data is invalid, the system rejects the request.
- If the base price is negative, the system rejects the request.

**Postconditions:**

- The valid product changes are stored.
- Product variants remain unchanged unless they are modified through their dedicated use cases.
- Existing order item snapshots remain unchanged.

---

## UC11 Add Product Variant

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The product exists.

**Main flow:**

1. The administrator opens the selected product's variant management section.
2. The administrator selects **Add Variant**.
3. The administrator enters the variant color, size, stock quantity, price, and image.
4. The administrator submits the variant information.
5. The system validates the submitted variant information.
6. The system verifies that the same color and size combination does not already exist for the product.
7. The system stores the new product variant.
8. The system stores the uploaded image and records the generated image URL.
9. The system returns the created product variant.
10. The frontend displays a successful creation message.

**Alternative flows:**

- If the product does not exist, the system returns a not found response.
- If required variant information is missing or invalid, the system returns validation errors.
- If the stock quantity is negative, the system rejects the request.
- If the variant price is negative, the system rejects the request.
- If another variant with the same color and size already exists for the product, the system rejects the duplicate.
- If image upload fails, the variant creation operation fails and no incomplete variant is retained.

**Postconditions:**

- A new product variant is associated with the selected product.

---

## UC12 Update Product Variant

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The product exists.
- The product variant exists and belongs to the selected product.

**Main flow:**

1. The administrator opens the selected product's variant management section.
2. The system displays the current product variants.
3. The administrator selects a variant to edit.
4. The administrator changes one or more variant fields.
5. The administrator submits the changes.
6. The system validates the submitted variant information.
7. The system verifies that the changed color and size combination does not conflict with another variant of the same product.
8. The system applies the valid changes to the selected product variant.
9. If a new image is provided, the system stores the image and records the generated image URL.
10. The system returns the updated product variant.
11. The frontend displays a successful update message.

**Alternative flows:**

- If the product does not exist, the system returns a not found response.
- If the variant does not exist or does not belong to the selected product, the system returns a not found response.
- If the submitted data is invalid, the system rejects the request.
- If the stock quantity is negative, the system rejects the request.
- If the variant price is negative, the system rejects the request.
- If the update would create a duplicate color and size combination, the system rejects the request.
- If image upload fails, the system keeps the previous image information and rejects the image change.

**Postconditions:**

- The valid product variant changes are stored.
- Existing order item snapshots remain unchanged.

---

## UC13 Remove Product Variant

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The product exists.
- The product variant exists and belongs to the selected product.

**Main flow:**

1. The administrator opens the selected product's variant management section.
2. The administrator selects a product variant.
3. The administrator selects **Remove Variant**.
4. The frontend sends the removal request to the system.
5. The system verifies that the product variant exists and belongs to the selected product.
6. The system removes or deactivates the selected product variant according to the system's persistence policy.
7. The system returns a successful response.
8. The frontend removes the variant from the active variant list and displays a successful removal message.

**Alternative flows:**

- If the product does not exist, the system returns a not found response.
- If the variant does not exist or does not belong to the selected product, the system returns a not found response.
- If the variant cannot be physically deleted because it is referenced by historical order data, the system preserves historical references and makes the variant unavailable for future purchases.

**Postconditions:**

- The selected variant is no longer available for new purchases.
- Existing order item snapshots remain unchanged.

---

## UC14 Change Product Sale Status

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The product exists.

**Main flow:**

1. The administrator selects a product.
2. The administrator changes its sale status to `ACTIVE` or `INACTIVE`.
3. The frontend sends the requested status to the system.
4. The system validates the status change.
5. The system updates the product sale status.
6. The system returns the updated product.
7. The frontend displays the updated status.

**Alternative flows:**

- If the product does not exist, the system returns a not found response.
- If the requested status is invalid, the system rejects the request.
- If an inactive product remains in a guest cart, it is marked unavailable when that cart is viewed or checked out.

**Postconditions:**

- An `ACTIVE` product can appear in the storefront.
- An `INACTIVE` product is hidden from the storefront and cannot be included in a new order.
- Existing orders containing the product remain unchanged.

---

## UC15 View Orders

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.

**Main flow:**

1. The administrator opens the order management page.
2. The frontend requests the order list.
3. The system retrieves the orders.
4. The system returns basic order information.
5. The frontend displays the order list.
6. The administrator selects an order.
7. The system returns the selected order and its order items.
8. The frontend displays the full order details.

**Alternative flows:**

- If no orders exist, the system displays an empty state.
- If the selected order does not exist, the system returns a not found response.

**Postconditions:**

- No data is changed.

---

## UC16 Confirm Order

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The order exists.
- The order has the `PENDING` status.

**Main flow:**

1. The administrator opens the order details.
2. The administrator selects **Confirm Order**.
3. The frontend sends the requested status change to the system.
4. The system verifies that the current status is `PENDING`.
5. The system changes the order status to `CONFIRMED`.
6. The system returns the updated order.
7. The frontend displays the new status.

**Alternative flows:**

- If the order does not exist, the system returns a not found response.
- If the order is already `CONFIRMED`, the system returns its current state without applying the transition again.
- If the order is `CANCELLED`, the system rejects the transition.

**Postconditions:**

- The order has the `CONFIRMED` status.
- Product stock is unchanged because it was already decreased when the order was placed.

---

## UC17 Cancel Order

**Actor:** Administrator

**Preconditions:**

- The administrator is authenticated.
- The order exists.
- The order has the `PENDING` or `CONFIRMED` status.

**Main flow:**

1. The administrator opens the order details.
2. The administrator selects **Cancel Order**.
3. The frontend sends the cancellation request to the system.
4. The system verifies that the order can be cancelled.
5. The system changes the order status to `CANCELLED`.
6. The system restores the stock quantity of every ordered product variant.
7. The system commits the transaction.
8. The system returns the updated order.
9. The frontend displays the cancelled status.

**Alternative flows:**

- If the order does not exist, the system returns a not found response.
- If the order is already `CANCELLED`, the system rejects the repeated cancellation and does not restore stock again.
- If the status update or stock restoration fails, the system rolls back the entire transaction.

**Postconditions:**

- The order has the `CANCELLED` status.
- Product variant stock is restored exactly once.
- No partial changes remain if the transaction fails.