package com.alten.booking.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alten.booking.domain.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation>{

    Page<Reservation> findAll(Pageable pageable);

    Page<Reservation> findAllByGuestEmail(String guestEmail, Pageable pageable);  

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId AND r.status = 'RESERVED' AND (r.startDate BETWEEN :startDate AND :endDate) OR (r.endDate BETWEEN :startDate AND :endDate)")
    int findTotalReservationsByRoomAndDate(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId AND r.id <> :reservationId AND r.status = 'RESERVED' AND (r.startDate BETWEEN :startDate AND :endDate) OR (r.endDate BETWEEN :startDate AND :endDate)")
    int findTotalReservationsByRoomAndDateExceptCurrentReservation(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("reservationId") Long reservationId);
    
}
