package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//SA
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
