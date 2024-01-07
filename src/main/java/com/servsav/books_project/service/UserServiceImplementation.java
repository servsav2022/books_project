package com.servsav.books_project.service;

import com.servsav.books_project.dto.UserDto;
import com.servsav.books_project.entity.Role;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.RoleRepository;
import com.servsav.books_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public  UserServiceImplementation (UserRepository userRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public  void saveUser (UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName("READ_ONLY");
        if (role ==null){
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }
    @Override
    public User findUserByEmail(String email) {return userRepository.findByEmail(email);}

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return  users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }
    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        List<String> roleNames = user.getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.toList());
        userDto.setRoles(roleNames);
        return userDto;
    }
    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("READ_ONLY");
        return roleRepository.save(role);
    }
}