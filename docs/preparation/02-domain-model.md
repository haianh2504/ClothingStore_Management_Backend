# Domain Model 
  
### Main Entities  
  
| Entity      | Attributes                                                                                                                        |     |
| ----------- | --------------------------------------------------------------------------------------------------------------------------------- | --- |
| **Admin**   | `email`, `password_hash`                                                                                                          |     |
| **Product** | `id`, `name`, `description`, `category`, `price`, `sale_status`                                                                   |     |
| **Order**   | `id`, `customer_name`, `customer_phone_number`, `customer_email`, `delivery_address`, `total_price`, `order_status`, `created_at` |     |
| **Cart**    | `id`, `guest_id`                                                                                                                  |     |
  
### Supporting Entities  
  
| Entity             | Attributes                                                           |     |     |
| ------------------ | -------------------------------------------------------------------- | --- | --- |
| **CartItem**       | `id`, `cart_id`, `product_variant_id`, `quantity`                    |     |     |
| **OrderItem**      | `id`, `order_id`, `product_name`, `product_variant_id`, `unit_price` |     |     |
| **ProductVariant** | `id`, `product_id`, `size`, `quantity`, `image_url`                  |     |     |
  
### Enumerations  
  
| Enum | Values                              |  
|---|-------------------------------------|  
| **OrderStatus** | `PENDING`, `CONFIRMED`, `CANCELLED` |  
| **SaleStatus** | `ACTIVE`, `INACTIVE`                |  
| **ProductSize** | `S`, `M`, `L`, `XL`                 |  
| **ProductCategory** | `BOTTOMS`, `TOPS`, `ACCESSORIES`    |  
  
### Main Relationships  
  
| Relationship                       | Cardinality |     |
| ---------------------------------- | ----------- | --- |
| **Product** → **ProductVariant**   | One-to-many |     |
| **Cart** → **CartItem**            | One-to-many |     |
| **CartItem** → **ProductVariant**  | Many-to-one |     |
| **Order** → **OrderItem**          | One-to-many |     |
| **OrderItem** → **ProductVariant** | Many-to-one |     |
