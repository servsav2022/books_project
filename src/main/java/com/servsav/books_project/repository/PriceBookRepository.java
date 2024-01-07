package com.servsav.books_project.repository;

import com.servsav.books_project.entity.PriceBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceBookRepository extends JpaRepository<PriceBook, Long> {
}

