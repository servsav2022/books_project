package com.servsav.books_project.controller;

import com.servsav.books_project.entity.Role;
import com.servsav.books_project.entity.Shop;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.RoleRepository;
import com.servsav.books_project.repository.ShopRepository;
import com.servsav.books_project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class ShopController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private RoleRepository roleRepository;
    @GetMapping("/list-shops")
    public ModelAndView getAllShops(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("/list-shops -> connection");
        log.info(userDetails.getUsername());
        ModelAndView mav = new ModelAndView("list-shops");
        // Получаем текущего пользователя
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        currentUser.getRoles();
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == currentUser.getRoles().get(0)) {
            //получаем магазины для текущего пользователя
            List<Shop> userShops = currentUser.getShops();
            mav.addObject("shops", userShops);
        }
        role = roleRepository.findByName("ROLE_ADMIN");
        if (role == currentUser.getRoles().get(0)) {
            //получаем магазины для текущего пользователя
            List<Shop> userShops = shopRepository.findAll();
            mav.addObject("shops", userShops);
        }
        return mav;
    }
    @GetMapping("/addShopForm")
    public ModelAndView addShopForm() {
        ModelAndView mav = new ModelAndView("add-shop-form");
        Shop shop = new Shop();
        mav.addObject("shop", shop);
        return mav;
    }
    @PostMapping("/saveShop")
    public String saveShop(@ModelAttribute Shop shop, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        shop.setUser(currentUser);
        shopRepository.save(shop);
        return "redirect:/list-shops";
    }
    @GetMapping("/showUpdateShopForm")
    public ModelAndView showUpdateForm (@RequestParam Long shopId) {
        ModelAndView mav = new ModelAndView("add-shop-form");
        Optional<Shop> optionalShop = shopRepository.findById(shopId);
        Shop shop = new Shop();
        if (optionalShop.isPresent()) {
            shop = optionalShop.get();
        }
        mav.addObject("shop", shop);
        return mav;
    }
       @GetMapping("/deleteShop")
    public String deleteShop(@RequestParam Long shopId) {
        shopRepository.deleteById(shopId);
        return "redirect:/list-shops";
    }
}