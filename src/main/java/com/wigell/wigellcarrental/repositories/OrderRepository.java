package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

//SA
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order>findAllByIsActiveTrue();//SA
    //AWS
    List<Order> findByCustomer_PersonalIdentityNumberAndIsActiveTrue(String personalIdentityNumber);

    List<Order>findAllByEndDateBeforeAndIsActiveFalse(LocalDate beforeThisDate);//SA

    //WIG-85-AA
    @Query("SELECT o FROM Order o WHERE o.startDate <= :periodEnd AND o.endDate >= :periodStart")
    List<Order> findOverlappingOrders(@Param("periodStart") LocalDate periodEnd, @Param("periodEnd") LocalDate periodStart);
}
