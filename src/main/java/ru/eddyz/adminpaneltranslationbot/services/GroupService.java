package ru.eddyz.adminpaneltranslationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Group;

import java.util.List;

public interface GroupService {

    List<Group> findByChatId(Long chatId);

    void save(Group newGroup);

    void update(Group group);

    void deleteById(Long id);

    void deleteLinksLanguage(Long groupId);

    Group findById(Long id);

    List<Group> findByMinChars(Integer chars);

    Group findByTelegramChatId(Long telegramGroupId);

    void deleteByTelegramChatId(Long id);

    List<Group> findAll();

}
