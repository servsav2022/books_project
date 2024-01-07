package com.servsav.books_project.controller;

import com.servsav.books_project.dto.UserDto;
import com.servsav.books_project.entity.Role;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.RoleRepository;
import com.servsav.books_project.repository.UserRepository;
import com.servsav.books_project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class SecurityController {
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
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
    public String users(Model model) {
        List<Role> roles = roleRepository.findAll();

        List<UserDto> users = userService.findAllUsers();
        List<String> roleNames =roles
                .stream().map(Role::getName)
                .collect(Collectors.toList());
        model.addAttribute("userRoleNames", roleNames);
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/updateRoles")
    public String updateRoles(@RequestParam("email") String email,@RequestParam("role") String roleName) {

                // Получить пользователя по email
        User selectUser = userRepository.findByEmail(email);
        if (selectUser == null) {
            // Обработка случая, когда пользователь с указанной электронной почтой не найден
            return "redirect:/users"; // или любая другая логика обработки ошибки
        }

        log.info(selectUser.getName());
        log.info(selectUser.getName());
        log.info(selectUser.getName());
        log.info(selectUser.getName());
        log.info(selectUser.getName());
        log.info(selectUser.getName());
        log.info(selectUser.getName());
        log.info("roleName пришедшее из запроса : "+roleName);
        log.info("roleName пришедшее из запроса : "+roleName);
        log.info("roleName пришедшее из запроса : "+roleName);
        // Установить новые роли для пользователя
        Role role=roleRepository.findByName(roleName);
        selectUser.setRoles(Arrays.asList(role));

        log.info("установлена роль: "+role.getName());
        log.info("установлена роль: "+role.getName());
        log.info("установлена роль: "+role.getName());

        // Сохранить обновленного пользователя
        userRepository.save(selectUser);
        // Перенаправить на страницу с пользователями
        return "redirect:/users";
    }

}
