package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

//SA
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order>findAllByIsActiveTrue();//SA
    //AWS
    List<Order> findByCustomer_PersonalIdentityNumberAndIsActiveTrue(String personalIdentityNumber);

    List<Order>findAllByEndDateBefore(LocalDate beforeThisDate);//SA
    List<Order>findAllByEndDateBeforeOrStartDateBefore(LocalDate endDate,LocalDate startDate);
    List<Order>findAllByEndDateBeforeAndIsActiveOrStartDateBeforeAndIsActive(LocalDate endDate,Boolean active,LocalDate startDate,Boolean active2);
}
