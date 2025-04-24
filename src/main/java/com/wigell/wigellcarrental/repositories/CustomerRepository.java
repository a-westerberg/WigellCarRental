package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//SA
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    // WIG-27-SJ
    Customer findById(long id);

    //WIG-22-AA
    Optional<Customer> findByPersonalIdentityNumber(String personalIdentityNumber);
}
