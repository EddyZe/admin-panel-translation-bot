package ru.eddyz.adminpaneltranslationbot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Payment;
import ru.eddyz.adminpaneltranslationbot.domain.entities.TranslationMessage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TranslationMessageRepository extends JpaRepository<TranslationMessage, Long> {

    @Query(value = "select m from TranslationMessage as m where m.translationTime >= :startDate and m.translationTime    <= :endDate")
    List<TranslationMessage> findByCreatedAtPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<TranslationMessage> findByGroupChatId(Long chatId);
}
