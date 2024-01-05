package com.servsav.books_project.service;

import com.servsav.books_project.dto.UserDto;
import com.servsav.books_project.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
   //тест

}
