package com.wigell.wigellcarrental.models.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

//WIG-5-AA
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booked_at", nullable = false)
    private LocalDate bookedAt;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "active", nullable = false)
    private Boolean isActive;

    @Column(name = "cancelled",nullable = false)
    private Boolean isCancelled;

    public Order() {

    }

    public Order(Long id) {
        this.id = id;
    }

    public Order(Long id, LocalDate bookedAt, LocalDate startDate, LocalDate endDate, Car car, Customer customer, BigDecimal totalPrice, Boolean isActive, Boolean isCancelled) {
        this.id = id;
        this.bookedAt = bookedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.car = car;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.isActive = isActive;
        this.isCancelled = isCancelled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDate bookedAt) {
        this.bookedAt = bookedAt;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", bookedAt=" + bookedAt +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", car=" + (car != null ? car.getId() : null)  +
                ", customer=" + (customer != null ? customer.getId() : null) +
                ", totalPrice=" + totalPrice +
                ", isActive=" + isActive +
                ", isCancelled=" + isCancelled +
                '}';
    }
}
