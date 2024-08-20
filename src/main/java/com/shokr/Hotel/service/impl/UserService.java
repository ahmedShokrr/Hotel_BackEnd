package com.shokr.Hotel.service.impl;

import com.shokr.Hotel.dto.LoginRequest;
import com.shokr.Hotel.dto.Response;
import com.shokr.Hotel.dto.UserDTO;
import com.shokr.Hotel.entity.User;
import com.shokr.Hotel.exception.OurException;
import com.shokr.Hotel.repo.UserRepository;
import com.shokr.Hotel.service.interfac.IUserService;
import com.shokr.Hotel.utils.JWTUtils;
import com.shokr.Hotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;


    @Override
    public Response register(User user) {
        Response response = new Response();
        try {
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new OurException(user.getEmail() + "Already Exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setUser(userDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During USer Registration " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new OurException("User Not Found"));
            var jwt = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");
            response.setMessage("Successfully Logged In");


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(400);
            response.setMessage("Error Occurred During User Login" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        try {
            List<User> userList= userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("Successfully Fetched All Users");
            response.setUserList(userDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During Getting All Users" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();
        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("Successfully Fetched User Booking History");
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Error Occurred During Getting User Booking History" + e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurred During Getting User Booking History" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();
        try {
            userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("User Not Found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("Successfully");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Error Occurred During delete User" + e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurred During delete User " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try {
            var user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage( e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurred During Getting User " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();
        try {
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage( e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurred During Getting User " + e.getMessage());
        }
        return response;       }
}
