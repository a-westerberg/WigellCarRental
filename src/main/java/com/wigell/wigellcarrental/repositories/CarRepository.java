package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//SA
@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    // AWS
    List<Car> findByStatus(CarStatus status);
}
