# Domain Model

### Main Entities

| Entity      | Attributes                                                                                                 |     |
| ----------- | ---------------------------------------------------------------------------------------------------------- | --- |
| **Admin**   | `email`, `passwordHash`                                                                                    |     |
| **Product** | `id`, `name`, `description`, `category`, `price`, `saleStatus`                                             |     |
| **Order**   | `orderCode` `customerName`, `phoneNumber`, `email`, `deliveryAddress`, `totalPrice`, `status`, `createdAt` |     |
| **Cart**    | `id`, `guestId`                                                                                            |     |


### Supporting Entities

| Entity             | Attributes                                                                            |     |     |
| ------------------ | ------------------------------------------------------------------------------------- | --- | --- |
| **CartItem**       | `id`, `cartId`, `variantId`, `quantity`                                               |     |     |
| **OrderItem**      | `id`, `variantId` ,`orderCode`, `productName`, `size, `quantity`, `color`,`unitPrice` |     |     |
| **ProductVariant** | `id`, `productId`, `size`, `quantity`, `imageUrl`, `color`                            |     |     |


### Enumerations

| Enum                | Values                              |     |
| ------------------- | ----------------------------------- | --- |
| **OrderStatus**     | `PENDING`, `CONFIRMED`, `CANCELLED` |     |
| **SaleStatus**      | `ACTIVE`, `INACTIVE`                |     |
| **ProductSize**     | `S`, `M`, `L`, `XL`                 |     |
| **ProductCategory** | `BOTTOMS`, `TOPS`, `ACCESSORIES`    |     |

### Main Relationships

| Relationship                       | Cardinality |     |
| ---------------------------------- | ----------- | --- |
| **Product** → **ProductVariant**   | One-to-many |     |
| **Cart** → **CartItem**            | One-to-many |     |
| **CartItem** → **ProductVariant**  | Many-to-one |     |
| **Order** → **OrderItem**          | One-to-many |     |
| **OrderItem** → **ProductVariant** | Many-to-one |     |
