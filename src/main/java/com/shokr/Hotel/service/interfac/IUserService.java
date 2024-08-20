package com.shokr.Hotel.service.interfac;


import com.shokr.Hotel.dto.LoginRequest;
import com.shokr.Hotel.dto.Response;
import com.shokr.Hotel.entity.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);

}