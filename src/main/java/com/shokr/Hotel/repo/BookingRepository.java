package com.shokr.Hotel.repo;


import com.shokr.Hotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    Optional<Booking> findByBookingConfirmationCode(String confirmationCode);






}
