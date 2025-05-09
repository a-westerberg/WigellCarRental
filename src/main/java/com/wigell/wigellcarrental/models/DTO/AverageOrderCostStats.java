package com.wigell.wigellcarrental.models.DTO;

import java.math.BigDecimal;
import java.util.List;

//WIG-97-SJ
public class AverageOrderCostStats {

    private final BigDecimal averageCostPerOrder;
    private final List<OrderCostDetails> orders;

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
