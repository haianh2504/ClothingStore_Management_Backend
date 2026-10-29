package org.example.spring_backend_clothingstore.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.guest.GuestIdResolver;
import org.example.spring_backend_clothingstore.order.dto.GuestOrderDetailResponse;
import org.example.spring_backend_clothingstore.order.dto.GuestOrderHistoryResponse;
import org.example.spring_backend_clothingstore.order.dto.OrderResponse;
import org.example.spring_backend_clothingstore.order.dto.PlaceOrderRequest;
import org.example.spring_backend_clothingstore.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final GuestIdResolver guestIdResolver;

    // Place Order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @Valid @RequestBody PlaceOrderRequest request
    ){
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
        OrderResponse order = orderService.placeOrder(guestId, request);
        int status = HttpStatus.CREATED.value();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.from(
                status,
                "ORDER_CREATED",
                "Order created successfully",
                OffsetDateTime.now(),
                order
        ));
    }

    /** Trả về danh sách order của guest dựa trên cookie GUEST_ID. */
    @GetMapping
    public ResponseEntity<ApiResponse<GuestOrderHistoryResponse>> getGuestOrderHistory(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Pageable pageable
    ) {
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
        GuestOrderHistoryResponse orders = orderService.getGuestOrderHistory(guestId, pageable);

        return ResponseEntity.ok(ApiResponse.from(
                HttpStatus.OK.value(),
                "GUEST_ORDERS_RETRIEVED",
                "Guest orders retrieved successfully",
                OffsetDateTime.now(),
                orders
        ));
    }

    /** Trả về chi tiết order khi order code thuộc đúng guest hiện tại. */
    @GetMapping("/{orderCode}")
    public ResponseEntity<ApiResponse<GuestOrderDetailResponse>> getGuestOrderDetail(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @PathVariable String orderCode
    ) {
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
        GuestOrderDetailResponse order = orderService.getGuestOrderDetail(guestId, orderCode);

        return ResponseEntity.ok(ApiResponse.from(
                HttpStatus.OK.value(),
                "ORDER_DETAIL_RETRIEVED",
                "Order detail retrieved successfully",
                OffsetDateTime.now(),
                order
        ));
    }
}
