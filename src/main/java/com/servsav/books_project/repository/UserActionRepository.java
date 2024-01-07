package com.servsav.books_project.repository;

import com.servsav.books_project.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}
