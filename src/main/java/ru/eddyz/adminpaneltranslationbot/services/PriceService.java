package ru.eddyz.adminpaneltranslationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Price;
import ru.eddyz.adminpaneltranslationbot.domain.enums.PaymentType;

import java.util.List;

public interface PriceService {

    Price findById(Long id);

    void save(Price price);

    Page<Price> findByType(PaymentType paymentType, Pageable pageable);

    List<Price> findAll();

    void deleteById(Long priceId);
}
