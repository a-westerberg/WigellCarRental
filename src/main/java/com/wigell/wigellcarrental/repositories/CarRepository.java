package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//SA
@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    // AWS
    List<Car> findByStatus(CarStatus status);


    //WIG-20-AA
    Optional<Car> findByRegistrationNumber(String registrationNumber);

    //WIG-37-AA
    Optional<Car> findFirstByStatusAndIdNot(CarStatus carStatus, Long id);

    //WIG-122-AA
    @Query(value = "SELECT * FROM cars c " +
            "WHERE c.id <> :excludedCarId " +
            "AND NOT EXISTS (" +
                "SELECT 1 FROM orders o " +
                "WHERE o.car_id = c.id " +
                "AND o.start_date <= :endDate " +
                "AND o.end_date >= :startDate" +
            ") " +
            "LIMIT 1",
            nativeQuery = true)
            Optional<Car> findFirstAvailableCarBetween(
                    @Param("startDate")LocalDate startDate,
                    @Param("endDate") LocalDate endDate,
                    @Param("excludedCarId") Long excludedCarId);

}
