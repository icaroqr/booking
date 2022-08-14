package com.alten.booking.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.booking.domain.Room;
import com.alten.booking.exceptions.NotFoundException;
import com.alten.booking.repository.RoomRepository;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepo;

    public Room findById(Long id) {
        Optional<Room> result = roomRepo.findById(id);
		return result.orElseThrow(() -> new NotFoundException("Room not found for id: " + id));
    }
    
}
