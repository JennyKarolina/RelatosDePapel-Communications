package com.orders.relatosdepapel.communications.events.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEventBody {
    private String orderName;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private String status;
    private String ownerId;
    private String email;
    private List<OrderItemEvent> orderItems;
}
