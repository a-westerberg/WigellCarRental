package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//SA
@DataJpaTest
class OrderRepositoryUnitTest {

    //TODO: Tar in från data.sql filen
    //SA
    private OrderRepository orderRepository;
    private LocalDate testDate = LocalDate.of(2025,4,20);
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
    /*@BeforeEach
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

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }*/

    //SA
    @Test
    void findAllByIsActiveTrueShouldReturnOrdersWithIsActiveTrue() {
        //orderRepository.save(order);
        List<Order>allOrder = orderRepository.findAll();
        List<Order> allByIsActiveTrue = orderRepository.findAllByIsActiveTrue();
        assertNotNull(allByIsActiveTrue);
        assertTrue(allByIsActiveTrue.size() > 0);

        assertThat(allByIsActiveTrue.size()).isNotEqualTo(allOrder.size());
        for (Order order : allByIsActiveTrue) {
            assertThat(order.getIsActive()).isTrue();
        }
    }

    //SA
    //Tar inte bort emellan
    @Test
    void findAllByIsActiveFalseShouldNotReturnOrderIfOrderIsActiveFalse() {
        List<Order>allOrder = orderRepository.findAll();
        List<Order> allByIsActiveTrue = orderRepository.findAllByIsActiveTrue();

        assertThat(allByIsActiveTrue.size()).isNotEqualTo(allOrder.size());
        for(Order o : allByIsActiveTrue) {
            assertThat(o.getIsActive()).isNotEqualTo(false);
        }


    }

    //SA
    @Test
    void findAllByEndDateBeforeAndIsActiveFalseShouldReturnOrder() {
        List<Order>allOrder = orderRepository.findAll();
        List<Order>endedOrdersAndIsActiveFalse = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(testDate);

        assertThat(allOrder.size()).isNotEqualTo(endedOrdersAndIsActiveFalse.size());

        for(Order o : endedOrdersAndIsActiveFalse) {
            assertThat(o.getIsActive()).isFalse();
            assertThat(o.getEndDate()).isBefore(testDate);
        }


    }

    //SA
    @Test
    void findAllByEndDateBeforeAndIsActiveFalseShouldNotReturnActiveOrderOrOrderAfterDateGiven(){
        List<Order>orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(testDate);
        Optional<Order> activeOrder = orderRepository.findById(4L);

        if(activeOrder.isPresent()) {
            Order order2 = activeOrder.get();
            assertFalse(orders.contains(order2));
        }

        Optional<Order> orderAfter = orderRepository.findById(7L);
        if(orderAfter.isPresent()) {
            Order order3 = orderAfter.get();
            assertFalse(orders.contains(order3));
        }

    }
}