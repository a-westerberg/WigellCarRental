package com.wigell.wigellcarrental.models.DTO;

import java.math.BigDecimal;

//WIG-97-SJ
public class OrderCostDetailsDTO {

    private long orderId;
    private long carId;
    private BigDecimal totalPrice;

    public OrderCostDetailsDTO (){}
    public OrderCostDetailsDTO(long orderId, long carId, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.carId = carId;
        this.totalPrice = totalPrice;
    }

    public long getOrderId() {return orderId;}
    public void setOrderId(long orderId) {this.orderId = orderId;}

    public long getCarId() {return carId;}
    public void setCarId(long carId) {this.carId = carId;}

    public BigDecimal getTotalPrice() {return totalPrice;}
    public void setTotalPrice(BigDecimal totalPrice) {this.totalPrice = totalPrice;}

    @Override
    public String toString() {
        return "OrderCostDetailsDTO{" +
                "orderId=" + orderId +
                ", carId=" + carId +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
