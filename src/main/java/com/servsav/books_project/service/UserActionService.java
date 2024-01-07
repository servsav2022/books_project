package com.servsav.books_project.service;
import com.servsav.books_project.entity.User;
import com.servsav.books_project.entity.UserAction;
import com.servsav.books_project.repository.UserActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserActionService {

    private final UserActionRepository userActionRepository;

    @Autowired
    public UserActionService(UserActionRepository userActionRepository) {
        this.userActionRepository = userActionRepository;
    }

    public void logUserAction(User user, String description) {
        UserAction userAction = new UserAction(null, LocalDateTime.now(), description, user);
        userActionRepository.save(userAction);
    }
}