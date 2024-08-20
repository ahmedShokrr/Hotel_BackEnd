package com.shokr.Hotel.service.interfac;

import com.shokr.Hotel.dto.Response;
import com.shokr.Hotel.entity.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);

}
