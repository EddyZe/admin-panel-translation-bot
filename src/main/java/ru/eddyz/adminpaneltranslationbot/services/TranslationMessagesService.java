package ru.eddyz.adminpaneltranslationbot.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.adminpaneltranslationbot.domain.entities.TranslationMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface TranslationMessagesService {


    void save(TranslationMessage translationMessage);

    List<TranslationMessage> findAll();

    List<TranslationMessage> findByCreatedAtPeriod(LocalDateTime date, LocalDateTime localDateTime);

    List<TranslationMessage> findByGroupChatId(Long chatId);
}
