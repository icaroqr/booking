package com.alten.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.alten.booking.domain.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation>{

    Page<Reservation> findAll(Pageable pageable);

    Page<Reservation> findAllByGuestEmail(String guestEmail, Pageable pageable);
    
}
