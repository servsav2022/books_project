package com.servsav.books_project.controller;

import com.servsav.books_project.entity.Book;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.repository.BookRepository;
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
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/list-books")
    public ModelAndView getAllBooks(@AuthenticationPrincipal UserDetails userDetails){
        log.info("/list-books -> connection");
        log.info(userDetails.getUsername());
        ModelAndView mav = new ModelAndView("list-books");
        // Получаем текущего пользователя
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        // Получаем список книг для текущего пользователя
        List<Book> userBooks = currentUser.getBooks();
        mav.addObject("books", userBooks);
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
