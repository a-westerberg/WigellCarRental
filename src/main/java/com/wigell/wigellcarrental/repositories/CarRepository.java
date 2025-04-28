package com.wigell.wigellcarrental.repositories;

import com.wigell.wigellcarrental.entities.Car;
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

    //SA
    @Query("SELECT c FROM Car c " +
            "WHERE c.status = :status " +
            "AND c.id NOT IN (" +
            "    SELECT o.car.id FROM Order o " +
            "    WHERE o.isActive = true AND " +
            "          o.startDate <= :endDate AND " +
            "          o.endDate >= :startDate" +
            ")")
    List<Car> findAvailableCarsForDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") CarStatus status);



}
