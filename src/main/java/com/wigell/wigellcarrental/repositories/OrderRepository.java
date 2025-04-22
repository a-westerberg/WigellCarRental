package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//SA
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
