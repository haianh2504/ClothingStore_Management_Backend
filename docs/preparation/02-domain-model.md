# Domain Model

### Main Entities

| Entity      | Attributes                                                                                                 |     |
| ----------- | ---------------------------------------------------------------------------------------------------------- | --- |
| **Admin**   | `email`, `passwordHash`                                                                                    |     |
| **Product** | `id`, `name`, `description`, `category`, `price`, `saleStatus`, `deletedAt`                                  |     |
| **Order**   | `orderCode` `customerName`, `phoneNumber`, `email`, `deliveryAddress`, `totalPrice`, `status`, `createdAt` |     |
| **Cart**    | `id`, `guestId`                                                                                            |     |


### Supporting Entities

| Entity             | Attributes                                                                            |     |     |
| ------------------ | ------------------------------------------------------------------------------------- | --- | --- |
| **CartItem**       | `id`, `cartId`, `variantId`, `quantity`                                               |     |     |
| **OrderItem**      | `id`, `variantId` ,`orderCode`, `productName`, `size, `quantity`, `color`,`unitPrice` |     |     |
| **ProductVariant** | `id`, `productId`, `size`, `quantity`, `imageUrl`, `color`, `deletedAt`               |     |     |


### Enumerations

| Enum                | Values                              |     |
| ------------------- | ----------------------------------- | --- |
| **OrderStatus**     | `PENDING`, `CONFIRMED`, `CANCELLED` |     |
| **SaleStatus**      | `ACTIVE`, `INACTIVE`                |     |
| **ProductSize**     | `S`, `M`, `L`, `XL`                 |     |
| **ProductCategory** | `BOTTOMS`, `TOPS`, `ACCESSORIES`    |     |

`Product.price` is the sale price for every non-deleted variant of that product. `ProductVariant` does not have its own price. `OrderItem.unitPrice` stores the product price at order time.

`deletedAt` is nullable. A non-null value means the entity is soft-deleted and unavailable for new catalog, cart, or order operations. The `(productId, size, color)` combination must be unique among non-deleted variants.

### Main Relationships

| Relationship                       | Cardinality |     |
| ---------------------------------- | ----------- | --- |
| **Product** → **ProductVariant**   | One-to-many |     |
| **Cart** → **CartItem**            | One-to-many |     |
| **CartItem** → **ProductVariant**  | Many-to-one |     |
| **Order** → **OrderItem**          | One-to-many |     |
| **OrderItem** → **ProductVariant** | Many-to-one |     |
