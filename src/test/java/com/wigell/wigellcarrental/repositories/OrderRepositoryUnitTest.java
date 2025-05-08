package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//SA
@DataJpaTest
class OrderRepositoryUnitTest {

    //SA
    private OrderRepository orderRepository;
    private Order order;
    private Order orderIsActiveFalse;
    private Car car;
    private Customer customer;

    //SA
    @Autowired
    public OrderRepositoryUnitTest(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //SA
    @BeforeEach
    void setUp() {
        //car = new Car(1L,"Kia","84Q","678FOW", CarStatus.AVAILABLE, BigDecimal.valueOf(299.00),List.of(order));
        car = new Car(1L);
        //customer = new Customer(1L,"123456-7890","John","Smith","john.smith@gmail.com","098-654","123 Street", List.of(order));
        customer = new Customer(1L);
        order = new Order(1L, LocalDate.of(2025,5,8),LocalDate.of(2025,5,10),LocalDate.of(2025,5,15),car,customer,BigDecimal.valueOf(1495.00),true,false);

        orderIsActiveFalse = new Order(2L, LocalDate.of(2025,5,1),LocalDate.of(2025,5,2),LocalDate.of(2025,5,7),car,customer,BigDecimal.valueOf(1495.00),false,false);

        car.setOrders(List.of(order,orderIsActiveFalse));
        customer.setOrders(List.of(order,orderIsActiveFalse));
    }

    //SA
    @Test
    void findAllByIsActiveTrueShouldReturnOrder() {
        orderRepository.save(order);
        List<Order> orders = orderRepository.findAllByIsActiveTrue();
        assertNotNull(orders);
        assertTrue(orders.size() > 0);
        assertEquals(order.getCustomer(), customer);
    }

    //SA
    //Tar inte bort emellan
    @Test
    void findAllByIsActiveFalseShouldNotReturnOrderIfOrderIsInActive() {
        orderRepository.save(orderIsActiveFalse);

        List<Order> orders = orderRepository.findAllByIsActiveTrue();
        /*System.out.println(orders.size());
        System.out.println(orders.get(0).getIsActive().toString());
        for(Order o : orders) {
            System.out.println(o);
        }*/
        assertFalse(orders.contains(orderIsActiveFalse));

    }

    //SA //fungerar inte just nu
    @Test
    void findAllByEndDateBeforeAndIsActiveFalseShouldReturnOrder() {
        orderRepository.save(orderIsActiveFalse);

        List<Order>orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(LocalDate.of(2025,5,10));
        assertTrue(orders.contains(orderIsActiveFalse));
    }

    //SA
    @Test
    void findAllByEndDateBeforeAndIsActiveFalseShouldNotReturnOrder(){
        order.setIsActive(false);
        orderRepository.save(order);
        List<Order>orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(LocalDate.of(2025,5,10));

        assertFalse(orders.contains(order));
    }
}