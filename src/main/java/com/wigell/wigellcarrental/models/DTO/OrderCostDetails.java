package com.wigell.wigellcarrental.models.DTO;

import java.math.BigDecimal;

//WIG-97-SJ
public class OrderCostDetails {

    private final long orderId;
    private final long carId;
    private final BigDecimal totalPrice;

    public OrderCostDetails(long orderId, long carId, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.carId = carId;
        this.totalPrice = totalPrice;
    }

    public long getOrderId() {return orderId;}

    public long getCarId() {
        return carId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
