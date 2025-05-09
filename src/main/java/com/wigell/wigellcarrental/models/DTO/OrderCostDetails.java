package com.wigell.wigellcarrental.models.DTO;

import java.math.BigDecimal;

public class OrderCostDetails {

    private long orderId;
    private long carId;
    private BigDecimal totalPrice;

    public OrderCostDetails(long orderId, long carId, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.carId = carId;
        this.totalPrice = totalPrice;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getCarId() {
        return carId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
