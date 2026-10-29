package org.example.spring_backend_clothingstore.order.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.cart.entity.Cart;
import org.example.spring_backend_clothingstore.cart.repository.CartRepository;
import org.example.spring_backend_clothingstore.cart_item.entity.CartItem;
import org.example.spring_backend_clothingstore.cart_item.repository.CartItemRepository;
import org.example.spring_backend_clothingstore.composition.customer_name.CustomerName;
import org.example.spring_backend_clothingstore.composition.phone_number.PhoneNumber;
import org.example.spring_backend_clothingstore.exception.business.CartEmptyException;
import org.example.spring_backend_clothingstore.exception.business.InvalidCustomerInformationException;
import org.example.spring_backend_clothingstore.exception.business.OperationFailedException;
import org.example.spring_backend_clothingstore.exception.business.ProductUnavailableException;
import org.example.spring_backend_clothingstore.exception.resource.CartNotFoundException;
import org.example.spring_backend_clothingstore.exception.resource.OrderNotFoundException;
import org.example.spring_backend_clothingstore.order.dto.GuestOrderDetailResponse;
import org.example.spring_backend_clothingstore.order.dto.GuestOrderHistoryResponse;
import org.example.spring_backend_clothingstore.order.dto.OrderResponse;
import org.example.spring_backend_clothingstore.order.dto.PlaceOrderRequest;
import org.example.spring_backend_clothingstore.order.entity.Order;
import org.example.spring_backend_clothingstore.order.repository.OrderRepository;
import org.example.spring_backend_clothingstore.order_item.entity.OrderItem;
import org.example.spring_backend_clothingstore.order_item.repository.OrderItemRepository;
import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.example.spring_backend_clothingstore.product_variant.repository.ProductVariantRepository;
import org.example.spring_backend_clothingstore.guest.GuestIdValidator;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final GuestIdValidator guestIdValidator;

    @Transactional
    @Override
    public OrderResponse placeOrder(String guestId, PlaceOrderRequest request) {
        validateGuestId(guestId);

        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(CartNotFoundException::new);

        List<CartItem> cartItems = cartItemRepository.findAllByCart_Id(cart.getId());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException();
        }

        CustomerName customerName = createCustomerName(request.customerName());
        PhoneNumber phoneNumber = createPhoneNumber(request.phoneNumber());
        String deliveryAddress = request.deliveryAddress().trim();
        String email = normalizeEmail(request.email());

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<ProductUnavailableException.AvailabilityError> unavailableItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ProductVariant variant = cartItem.getVariant();
            Product product = variant.getProduct();

            collectAvailabilityError(
                    unavailableItems,
                    product,
                    variant,
                    cartItem.getQuantity()
            );
            totalPrice = totalPrice.add(product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        try {
            reserveStock(cartItems, unavailableItems);

            if (!unavailableItems.isEmpty()) {
                throw new ProductUnavailableException(unavailableItems);
            }

            Order order = new Order(
                    generateOrderCode(),
                    guestId,
                    customerName,
                    phoneNumber,
                    email,
                    deliveryAddress,
                    totalPrice
            );
            orderRepository.saveAndFlush(order);

            for (CartItem cartItem : cartItems) {
                ProductVariant variant = cartItem.getVariant();
                Product product = variant.getProduct();

                OrderItem orderItem = new OrderItem(
                        order,
                        variant,
                        product.getName().name(),
                        variant.getSize(),
                        cartItem.getQuantity(),
                        variant.getColor(),
                        product.getPrice()
                );
                order.getItems().add(orderItem);
                orderItemRepository.save(orderItem);
            }

            orderItemRepository.flush();
            cartItemRepository.deleteAllByCart_Id(cart.getId());

            return OrderResponse.from(order);
        } catch (ProductUnavailableException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new OperationFailedException(exception);
        }
    }

    /** Lấy các order của guest hiện tại theo thứ tự mới nhất trước. */
    @Transactional(readOnly = true)
    @Override
    public GuestOrderHistoryResponse getGuestOrderHistory(String guestId, Pageable pageable) {
        validateGuestId(guestId);
        return GuestOrderHistoryResponse.from(
                orderRepository.findByGuestIdOrderByCreatedAtDesc(guestId, pageable)
        );
    }

    /** Lấy chi tiết order và kiểm tra order thuộc guest hiện tại. */
    @Transactional(readOnly = true)
    @Override
    public GuestOrderDetailResponse getGuestOrderDetail(String guestId, String orderCode) {
        validateGuestId(guestId);

        if (orderCode == null || orderCode.isBlank()) {
            throw new OrderNotFoundException();
        }

        Order order = orderRepository.findByOrderCodeAndGuestId(orderCode, guestId)
                .orElseThrow(OrderNotFoundException::new);

        return GuestOrderDetailResponse.from(order);
    }

    private void collectAvailabilityError(
            List<ProductUnavailableException.AvailabilityError> errors,
            Product product,
            ProductVariant variant,
            int requestedQuantity
    ) {
        if (product.getSaleStatus() != SaleStatus.ACTIVE) {
            errors.add(new ProductUnavailableException.AvailabilityError(
                    variant.getId(),
                    "PRODUCT_INACTIVE"
            ));
        } else if (variant.getDeletedAt() != null) {
            errors.add(new ProductUnavailableException.AvailabilityError(
                    variant.getId(),
                    "VARIANT_DELETED"
            ));
        } else if (variant.getQuantity() < requestedQuantity) {
            errors.add(new ProductUnavailableException.AvailabilityError(
                    variant.getId(),
                    "STOCK_INSUFFICIENT"
            ));
        }
    }

    private void reserveStock(
            List<CartItem> cartItems,
            List<ProductUnavailableException.AvailabilityError> errors
    ) {
        for (CartItem cartItem : cartItems) {
            ProductVariant variant = cartItem.getVariant();
            Product product = variant.getProduct();

            if (product.getSaleStatus() != SaleStatus.ACTIVE
                    || variant.getDeletedAt() != null
                    || variant.getQuantity() < cartItem.getQuantity()) {
                continue;
            }

            int updatedRows = productVariantRepository.decreaseQuantityIfAvailable(
                    variant.getId(),
                    cartItem.getQuantity(),
                    SaleStatus.ACTIVE
            );

            if (updatedRows == 0) {
                errors.add(new ProductUnavailableException.AvailabilityError(
                        variant.getId(),
                        "STOCK_INSUFFICIENT"
                ));
            }
        }
    }

    private CustomerName createCustomerName(String value) {
        try {
            return new CustomerName(value);
        } catch (IllegalArgumentException exception) {
            throw new InvalidCustomerInformationException("Failed: Invalid Customer's Name");
        }
    }

    private PhoneNumber createPhoneNumber(String value) {
        try {
            return new PhoneNumber(value);
        } catch (IllegalArgumentException exception) {
            throw new InvalidCustomerInformationException("Failed: Invalid phoneNumber");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }

    private void validateGuestId(String guestId) {
        guestIdValidator.validate(guestId);
    }

    private String generateOrderCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
