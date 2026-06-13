
package com.orders.relatosdepapel.communications.events.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    private String idCatalog;
    private Integer quantity;
    private BigDecimal subTotal;
}

