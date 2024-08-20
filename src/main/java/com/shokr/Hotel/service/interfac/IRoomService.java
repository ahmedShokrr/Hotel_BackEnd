package com.shokr.Hotel.service.interfac;

import com.shokr.Hotel.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IRoomService {

    Response addNewRoom(MultipartFile photo, String roomType, BigDecimal price, String description);

    List<String> getAllRoomTypes();

    Response getAllRooms();

    Response deleteRoom(Long roomId);

    Response updateRoom(Long roomId, MultipartFile photo,String roomType, BigDecimal price, String description);

    Response getRoomById(Long roomId);

    Response getAvailableRoomsByDateAndType(LocalDate checkIn, LocalDate checkOut, String roomType);

    Response getAllAvailableRooms();

}
