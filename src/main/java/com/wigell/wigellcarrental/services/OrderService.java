package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.DTO.IncomeBetweenDatesDTO;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.models.DTO.AverageOrderCostStatsDTO;
import com.wigell.wigellcarrental.models.DTO.AverageRentalPeriodStatsDTO;
import com.wigell.wigellcarrental.models.DTO.PopularBrandStats;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

//SA
public interface OrderService {
    List<Order> getActiveOrders();//SA
    List<Order> getActiveOrdersForCustomer(String personalIdentityNumber);//AWS
    String cancelOrder(Long orderId, Principal principal);//SA

    String removeOrdersBeforeDate(LocalDate date, Principal principal);//SA

    // WIG-28-SJ
    Order addOrder(Order order, Principal principal);

    List<Order> getAllOrdersHistory();//SA

    String updateOrderStatus(Long orderId, String status, Principal principal);//SA

    String updateOrderCar(Long orderId, Long carId, Principal principal);//SA

    //WIG-85-AA
    PopularBrandStats getPopularBrand(String startDate, String endDate);

    // WIG-97-SJ
    AverageRentalPeriodStatsDTO getAverageRentalPeriod();
    AverageOrderCostStatsDTO costPerOrder();

    // WIG-25-AWS
    String removeOrderById(Long orderId, Principal principal);
    //WIG-114-AWS
    IncomeBetweenDatesDTO getIncomeOnMonth(String year, String month);
    IncomeBetweenDatesDTO getIncomeBetweenDates(String startDate, String endDate);
    IncomeBetweenDatesDTO getIncomeByYear(String year);
}
