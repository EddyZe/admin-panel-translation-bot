package ru.eddyz.adminpaneltranslationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.eddyz.adminpaneltranslationbot.domain.entities.User;
import ru.eddyz.adminpaneltranslationbot.repositories.UserRepository;
import ru.eddyz.adminpaneltranslationbot.services.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;



@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Override
    public void save(User newUser) {
        if (userRepository.findByChatId(newUser.getChatId()).isPresent())
            throw new IllegalArgumentException("User with this chatId exists");

        userRepository.save(newUser);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким username не найден"));
    }
}
