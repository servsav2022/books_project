package com.servsav.books_project.controller;

import com.servsav.books_project.dto.UserDto;
import com.servsav.books_project.entity.Role;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.UserRepository;
import com.servsav.books_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SecurityController {
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    public SecurityController(UserService userService){this.userService = userService;}

    @GetMapping("/index")
    public String home() {return "index";}

    @GetMapping("/login")
    public String login() {return "login";}

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser !=null && existingUser.getEmail()!=null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "На этот адрес электронной почты уже зарегистрирована учетная запись.");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }
        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String users(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        List<UserDto> users = userService.findAllUsers();
        List<String> roleNames = user.getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.toList());
        model.addAttribute("userRoleNames", roleNames);
        model.addAttribute("users", users);
        return "users";
    }

}
