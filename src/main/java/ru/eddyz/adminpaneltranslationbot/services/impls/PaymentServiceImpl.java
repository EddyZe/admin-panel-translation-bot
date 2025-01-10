package ru.eddyz.adminpaneltranslationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Payment;
import ru.eddyz.adminpaneltranslationbot.repositories.PaymentRepository;
import ru.eddyz.adminpaneltranslationbot.services.PaymentService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;


    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findByChatId(Long chatId) {
        return paymentRepository.findByChatId(chatId);
    }

    @Override
    public List<Payment> findByCreatedAtPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByCreatedAtPeriod(startDate, endDate);
    }
}
