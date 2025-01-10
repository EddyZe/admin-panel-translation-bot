package ru.eddyz.adminpaneltranslationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Payment;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    void save(Payment payment);

    List<Payment> findByChatId(Long chatId);

    List<Payment> findByCreatedAtPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
