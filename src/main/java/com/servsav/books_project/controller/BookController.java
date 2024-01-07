package com.servsav.books_project.controller;

import com.servsav.books_project.entity.Book;
import com.servsav.books_project.entity.PriceBook;
import com.servsav.books_project.entity.Role;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.BookRepository;
import com.servsav.books_project.repository.PriceBookRepository;
import com.servsav.books_project.repository.RoleRepository;
import com.servsav.books_project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PriceBookRepository priceBookRepository;
    @Autowired
    private RoleRepository roleRepository;
    @GetMapping("/list-books")
    public ModelAndView getAllBooks(Model model, @AuthenticationPrincipal UserDetails userDetails){
        log.info("/list-books -> connection");
        log.info(userDetails.getUsername());
        ModelAndView mav = new ModelAndView("list-books");
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        currentUser.getRoles();
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == currentUser.getRoles().get(0)) {
            //получаем магазины для текущего пользователя
            List<Book> userBooks = currentUser.getBooks();
            mav.addObject("books", userBooks);
        }
        role = roleRepository.findByName("ROLE_ADMIN");
        if (role == currentUser.getRoles().get(0)) {
            //получаем магазины для текущего пользователя
            List<Book> userBooks = bookRepository.findAll();
            mav.addObject("books", userBooks);
        }
        return mav;
    }
    @GetMapping("/addBookForm")
    public ModelAndView addBookForm() {
        ModelAndView mav = new ModelAndView("add-book-form");
        Book book = new Book();
        mav.addObject("book", book);
        return mav;
    }
    @PostMapping("/saveBook")
    public String saveBook(@ModelAttribute Book book, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        book.setUser(currentUser);
        PriceBook priceBook = book.getPriceBook();
            if (priceBook != null && priceBook.getId() == null) {
                // Сущность PriceBook еще не была сохранена, сохраняем ее
                priceBookRepository.save(priceBook);
            }
        bookRepository.save(book);
        return "redirect:/list-books";
    }
    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm (@RequestParam Long bookId) {
        ModelAndView mav = new ModelAndView("add-book-form");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Book book = new Book();
        if (optionalBook.isPresent()) {
            book = optionalBook.get();
        }
        mav.addObject("book", book);
        return mav;
    }
    @GetMapping("/deleteBook")
    public String deleteBook(@RequestParam Long bookId) {
        bookRepository.deleteById(bookId);
        return "redirect:/list-books";
    }
}
