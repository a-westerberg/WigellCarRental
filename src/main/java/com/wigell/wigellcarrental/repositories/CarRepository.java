package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//SA
@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
}
