package com.alten.booking.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.alten.booking.domain.Reservation;

public class ReservationSpecifications {

    private static final String START_DATE = "startDate";

    private ReservationSpecifications(){
        throw new IllegalStateException("Do not instantiate an Utility class");
    }
    
    public static Specification<Reservation> startDateBtw(LocalDate startDate, LocalDate endDate){
        return (root, query, cb) -> {
            if(startDate != null && endDate != null) {
                return cb.between(root.get(START_DATE), startDate, endDate);
            } else if(startDate != null) {
                return cb.greaterThanOrEqualTo(root.get(START_DATE), startDate);
            } else if(endDate != null) {
                return cb.lessThanOrEqualTo(root.get(START_DATE), endDate);
            } else {
                return null;
            }
        };
    }

    public static Specification<Reservation> equalToRoom(Long roomId){
        if(roomId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("room").get("id"), roomId);
    }

    public static Specification<Reservation> equalToGuestEmail(String email){
        if(email == null) return null;
        return (root, query, cb) -> cb.equal(root.get("guestEmail"), email);
    }

}
