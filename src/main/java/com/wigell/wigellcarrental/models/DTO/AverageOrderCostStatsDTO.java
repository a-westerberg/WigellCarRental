package com.wigell.wigellcarrental.models.DTO;

import java.math.BigDecimal;
import java.util.List;

//WIG-97-SJ
public class AverageOrderCostStatsDTO {

    private BigDecimal averageCostPerOrder;
    private List<OrderCostDetailsDTO> orders;

    public AverageOrderCostStatsDTO() {}
    public AverageOrderCostStatsDTO(BigDecimal averageCostPerOrder, List<OrderCostDetailsDTO> orders) {
        this.averageCostPerOrder = averageCostPerOrder;
        this.orders = orders;
    }

    public BigDecimal getAverageCostPerOrder() {return averageCostPerOrder;}
    public void setAverageCostPerOrder(BigDecimal averageCostPerOrder) {this.averageCostPerOrder = averageCostPerOrder;}

    public List<OrderCostDetailsDTO> getOrders() {return orders;}
    public void setOrders(List<OrderCostDetailsDTO> orders) {this.orders = orders;}

    @Override
    public String toString() {
        return "AverageOrderCostStatsDTO{" +
                "averageCostPerOrder=" + averageCostPerOrder +
                ", orders=" + orders +
                '}';
    }
}
