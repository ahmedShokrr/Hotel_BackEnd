package com.shokr.Hotel.service.impl;

import com.shokr.Hotel.dto.BookingDTO;
import com.shokr.Hotel.dto.Response;
import com.shokr.Hotel.entity.Booking;
import com.shokr.Hotel.entity.Room;
import com.shokr.Hotel.exception.OurException;
import com.shokr.Hotel.repo.BookingRepository;
import com.shokr.Hotel.repo.RoomRepository;
import com.shokr.Hotel.repo.UserRepository;
import com.shokr.Hotel.service.interfac.IBookingService;
import com.shokr.Hotel.service.interfac.IRoomService;
import com.shokr.Hotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {


    private final BookingRepository bookingRepository;
    private final IRoomService roomService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;



    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
                throw new IllegalArgumentException("Check out date should be after check in date");
            }
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(()->new OurException("Room not found"));
            var user = userRepository.findById(userId)
                    .orElseThrow(()->new OurException("User not found"));
            List<Booking> exitingBookings = room.getBookings();

            if (!roomIsAvailable(bookingRequest,exitingBookings)){
                throw new OurException("Room is not available for the selected dates");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode  = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());


        }

        return response;

    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        for (Booking booking : existingBookings) {
            if (booking.getCheckInDate().isBefore(bookingRequest.getCheckOutDate()) &&
                    booking.getCheckOutDate().isAfter(bookingRequest.getCheckInDate())) {
                return false; // There is an overlap
            }
        }
        return true; // No overlap found
    }



    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {

        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking Not Found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting All a bookings: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {

        Response response = new Response();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("Booking Not Found"));
            bookingRepository.deleteById(bookingId);

            response.setStatusCode(200);
            response.setMessage("successful");


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());

        }
        return response;      }

}
