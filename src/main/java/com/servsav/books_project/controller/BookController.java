package com.servsav.books_project.controller;

import com.servsav.books_project.entity.Book;
import com.servsav.books_project.entity.PriceBook;
import com.servsav.books_project.entity.Role;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.BookRepository;
import com.servsav.books_project.repository.PriceBookRepository;
import com.servsav.books_project.repository.RoleRepository;
import com.servsav.books_project.repository.UserRepository;
import com.servsav.books_project.service.UserActionService;
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
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PriceBookRepository priceBookRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserActionService userActionService;
    @GetMapping("/list-books")
    public ModelAndView getAllBooks(@AuthenticationPrincipal UserDetails userDetails){

        log.info("/list-books -> connection");
        log.info(userDetails.getUsername());
        ModelAndView mav = new ModelAndView("list-books");
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        Role role = roleRepository.findByName("USER");
        if (role == currentUser.getRoles().get(0)) {
            List<Book> userBooks = currentUser.getBooks();
            mav.addObject("books", userBooks);
        }
        role = roleRepository.findByName("ROLE_ADMIN");

        if (role == currentUser.getRoles().get(0)) {
            List<Book> userBooks = bookRepository.findAll();
            mav.addObject("books", userBooks);
        }

        role = roleRepository.findByName("READ_ONLY");
        if (role == currentUser.getRoles().get(0)) {
            List<Book> userBooks = currentUser.getBooks();
            mav.addObject("books", userBooks);
        }
        userActionService.logUserAction(currentUser, "Просмотрел список книг: ");
        return mav;
    }
    @GetMapping("/priceallbooks")
    public ModelAndView priceAllBooks(@AuthenticationPrincipal UserDetails userDetails){

        log.info(userDetails.getUsername());
        ModelAndView mav = new ModelAndView("priceallbooks");
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        Role role = roleRepository.findByName("USER");
        if (role == currentUser.getRoles().get(0)) {
            List<Book> userBooks = currentUser.getBooks();
            mav.addObject("books", userBooks);
            double totalPrice = userBooks.stream()
                    .mapToDouble(book -> book.getPriceBook().getPrice())
                    .sum();
            mav.addObject("totalPrice",totalPrice);
        }
        role = roleRepository.findByName("ROLE_ADMIN");

        if (role == currentUser.getRoles().get(0)) {
            List<Book> userBooks = bookRepository.findAll();
            mav.addObject("books", userBooks);
            double totalPrice = userBooks.stream()
                    .mapToDouble(book -> book.getPriceBook().getPrice())
                    .sum();
            mav.addObject("totalPrice",totalPrice);
        }

        role = roleRepository.findByName("READ_ONLY");
        if (role == currentUser.getRoles().get(0)) {
            List<Book> userBooks = currentUser.getBooks();
            mav.addObject("books", userBooks);
            double totalPrice = userBooks.stream()
                    .mapToDouble(book -> book.getPriceBook().getPrice())
                    .sum();
            mav.addObject("totalPrice",totalPrice);
        }
        userActionService.logUserAction(currentUser, "Просмотрел цену своих книг: ");
        return mav;
    }

    @GetMapping("/addBookForm")
    public ModelAndView addBookForm(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        ModelAndView mav = new ModelAndView("add-book-form");
        Book book = new Book();
        mav.addObject("book", book);
        userActionService.logUserAction(currentUser, "добавляет книгу");
        return mav;
    }
    @PostMapping("/saveBook")
    public String saveBook(@ModelAttribute Book book, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        book.setUser(currentUser);
        PriceBook priceBook = book.getPriceBook();
            if (priceBook != null && priceBook.getId() == null) {
                // сохранил цену
                userActionService.logUserAction(currentUser,
                        "сохранил цену книги: "+book.getPriceBook().getPrice().toString());
                // Сущность PriceBook еще не была сохранена, сохраняем ее
                priceBookRepository.save(priceBook);
            }
        bookRepository.save(book);
        userActionService.logUserAction(currentUser, "сохранил книгу: "+book.getTitle());
        return "redirect:/list-books";
    }
    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm (@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        ModelAndView mav = new ModelAndView("add-book-form");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Book book = new Book();
        if (optionalBook.isPresent()) {
            book = optionalBook.get();
        }
        mav.addObject("book", book);
        //запись действия
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        userActionService.logUserAction(currentUser,
                "Изменил книгу: "+bookRepository.findById(bookId).get().getTitle());
        return mav;
    }
    @GetMapping("/deleteBook")
    public String deleteBook(@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        //запись действия
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        userActionService.logUserAction(currentUser,
                "Удалил книгу: "+bookRepository.findById(bookId).get().getTitle());
        bookRepository.deleteById(bookId);
        return "redirect:/list-books";
    }
}
