package ru.eddyz.adminpaneltranslationbot.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.eddyz.adminpaneltranslationbot.domain.entities.TranslationMessage;
import ru.eddyz.adminpaneltranslationbot.repositories.TranslationMessageRepository;
import ru.eddyz.adminpaneltranslationbot.services.TranslationMessagesService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationMessagesServiceImpl implements TranslationMessagesService {

    private final TranslationMessageRepository translationMessageRepository;

    @Override
    public void save(TranslationMessage translationMessage) {
        translationMessageRepository.save(translationMessage);
    }

    @Override
    public List<TranslationMessage> findAll() {
        return translationMessageRepository.findAll();
    }

    @Override
    public List<TranslationMessage> findByCreatedAtPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return translationMessageRepository.findByCreatedAtPeriod(startDate, endDate);
    }

    @Override
    public List<TranslationMessage> findByGroupChatId(Long chatId) {
        return translationMessageRepository.findByGroupChatId(chatId);
    }
}
