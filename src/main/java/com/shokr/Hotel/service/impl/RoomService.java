package com.shokr.Hotel.service.impl;


import com.shokr.Hotel.dto.Response;
import com.shokr.Hotel.dto.RoomDTO;
import com.shokr.Hotel.entity.Room;
import com.shokr.Hotel.exception.OurException;
import com.shokr.Hotel.repo.BookingRepository;
import com.shokr.Hotel.repo.RoomRepository;
import com.shokr.Hotel.service.AwsS3Service;
import com.shokr.Hotel.service.interfac.IRoomService;
import com.shokr.Hotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final AwsS3Service awsS3Service;


    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {

        Response response = new Response();
        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room  = new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("Room saved successfully");
            response.setRoom(roomDTO);


        }catch (OurException e){

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error saving a room"+e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {

        Response response = new Response();
        try {
            List<Room> rooms = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOS = Utils.mapRoomListEntityToRoomListDTO(rooms);
            response.setStatusCode(200);
            response.setMessage("Room saved successfully");
            response.setRoomList(roomDTOS);


        }catch (OurException e){

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error finding rooms"+e.getMessage());
        }
        return response;

    }

    @Override
    public Response deleteRoom(Long roomId) {

        Response response = new Response();

        try {
            var room = roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room not found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Room saved Deleted");


        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Error finding rooms"+e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error finding rooms"+e.getMessage());
        }
        return response;

    }

    @Override
    public Response updateRoom(Long roomId, MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {

        Response response = new Response();

        try {
            String imageUrl = null;
            if (photo!=null && !photo.isEmpty()){
                imageUrl = awsS3Service.saveImageToS3(photo);
            }
            Room room = roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room not found"));
            if (roomType!=null){
                room.setRoomType(roomType);
            }

            if (roomPrice!=null){
                room.setRoomPrice(roomPrice);
            }
            if (description!=null){
                room.setRoomDescription(description);
            }
            if (imageUrl!=null){
                room.setRoomPhotoUrl(imageUrl);
            }

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setRoom(roomDTO);
            response.setStatusCode(200);
            response.setMessage("Room Updated Successfully");
        }

        catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Error finding rooms"+e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error finding rooms"+e.getMessage());
        }
        return response;

    }

    @Override
    public Response getRoomById(Long roomId) {

        Response response = new Response();

        try {
            var room = roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room not found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);


        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;     }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();

        try {
            // Validate if the check-in date is before the check-out date
            if (checkInDate.isAfter(checkOutDate)) {
                response.setStatusCode(400);
                response.setMessage("Check-in date must be before the check-out date.");
                return response;
            }

            // Validate if the check-in date is not in the past
            if (checkInDate.isBefore(LocalDate.now())) {
                response.setStatusCode(400);
                response.setMessage("Check-in date cannot be in the past.");
                return response;
            }
            // Validate if the check-out date is not in the past
            if (checkOutDate.isBefore(LocalDate.now())) {
                response.setStatusCode(400);
                response.setMessage("Check-in date cannot be in the past.");
                return response;
            }

            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error fetching available rooms: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;

    }
}
