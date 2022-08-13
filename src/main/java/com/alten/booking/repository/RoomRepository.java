package com.alten.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alten.booking.domain.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{
    
}
