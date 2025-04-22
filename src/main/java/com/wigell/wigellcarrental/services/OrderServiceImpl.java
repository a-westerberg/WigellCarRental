package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

//SA
@Service
public class OrderServiceImpl implements OrderService{
    //AWS
    private final OrderRepository orderRepository;
    //AWS
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    //AWS
    @Override
    public List<Order> getActiveOrdersForCustomer(String personalIdentityNumber) {
        return orderRepository.findByCustomer_PersonalIdentityNumberAndIsActiveTrue(personalIdentityNumber);
    }

    //SA
    @Override
    public String cancelOrder(Long orderId, Principal principal) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            //TODO: fixa när säkerhets läggs in då principal är null just nu utan den
            if (order.get().getCustomer().getPersonalIdentityNumber().equals(principal.getName())) {
                Order orderToCancel = order.get();
                orderToCancel.setActive(false);
                orderRepository.save(order.get());
                return "Order with id '"+orderId+"' is cancelled";
            }else {
                return "No order for '" + principal.getName() + "' with id: " + orderId;
            }

        }
        return "Order not found";
    }


}
