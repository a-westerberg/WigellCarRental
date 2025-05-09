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
    private final OrderRepository orderRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    //SA
    private Order order;
    private Order orderIsActiveFalse;
    private final LocalDate BOOKED_DATE = LocalDate.of(2025, 5, 2);
    private final LocalDate START_DATE = LocalDate.of(2025, 5, 7);
    private final LocalDate END_DATE = LocalDate.of(2025, 5, 12);
    private final LocalDate SECOND_END_DATE = LocalDate.of(2025, 5, 9);

    //SA
    @Autowired
    public OrderRepositoryUnitTest(OrderRepository orderRepository, CarRepository carRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    //SA
    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        carRepository.deleteAll();
        customerRepository.deleteAll();

        Car car = new Car(null, "Kia", "84Q", "678FOW", CarStatus.AVAILABLE, BigDecimal.valueOf(299.00), List.of());
        Customer customer = new Customer(null, "123456-7890", "John", "Smith", "john.smith@gmail.com", "098-654", "123 Street", List.of());
        order = new Order(null, BOOKED_DATE, START_DATE, END_DATE, car, customer, BigDecimal.valueOf(1495.00), true, false);
        orderIsActiveFalse = new Order(null, BOOKED_DATE, START_DATE, SECOND_END_DATE, car, customer, BigDecimal.valueOf(598.00), false, false);

        car.setOrders(List.of(order, orderIsActiveFalse));
        customer.setOrders(List.of(order, orderIsActiveFalse));

        customerRepository.save(customer);
        carRepository.save(car);
        orderRepository.save(order);
        orderRepository.save(orderIsActiveFalse);
    }


    //SA
    @Test
    void findAllByIsActiveTrueShouldReturnOrdersThatIsActiveTrue() {
        List<Order> orders = orderRepository.findAllByIsActiveTrue();
        assertNotNull(orders);

        assertFalse(orders.isEmpty());
        assertFalse(orders.contains(orderIsActiveFalse));

        assertTrue(orders.contains(order));

        for (Order order : orders) {
            assertThat(order.getIsActive()).isTrue();
        }
    }

    //SA
    @Test
    void findAllByEndDateBeforeAndIsActiveFalseShouldReturnOrdersThatIsActiveFalseAndEndDateBeforeGivenDate() {
        LocalDate testDate = LocalDate.of(2025, 5, 10);
        List<Order> orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(testDate);

        assertFalse(orders.isEmpty());
        assertFalse(orders.contains(order));

        assertTrue(orders.contains(orderIsActiveFalse));

        for (Order o : orders) {
            assertThat(o.getIsActive()).isFalse();
            assertThat(o.getEndDate()).isBefore(testDate);
        }

    }
}