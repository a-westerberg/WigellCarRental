package com.wigell.wigellcarrental.models.valueobjects;

import java.math.BigDecimal;
import java.util.List;

public class AverageOrderCostStats {

    private BigDecimal averageCostPerOrder;
    private List<OrderCostDetails> orders;

    public AverageOrderCostStats(BigDecimal averageCostPerOrder, List<OrderCostDetails> orders) {
        this.averageCostPerOrder = averageCostPerOrder;
        this.orders = orders;
    }

    public BigDecimal getAverageCostPerOrder() {
        return averageCostPerOrder;
    }

    public List<OrderCostDetails> getOrders() {
        return orders;
    }
}
