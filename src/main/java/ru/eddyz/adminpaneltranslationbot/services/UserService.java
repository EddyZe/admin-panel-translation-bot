package ru.eddyz.adminpaneltranslationbot.services;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.adminpaneltranslationbot.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByChatId(Long chatId);

    void save(User newUser);

    Page<User> findAll(Pageable pageable);

    List<User> findAll();

    User findByUsername(String username);
}
