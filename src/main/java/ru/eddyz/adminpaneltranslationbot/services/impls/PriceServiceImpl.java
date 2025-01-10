package ru.eddyz.adminpaneltranslationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Price;
import ru.eddyz.adminpaneltranslationbot.domain.enums.PaymentType;
import ru.eddyz.adminpaneltranslationbot.repositories.PriceRepository;
import ru.eddyz.adminpaneltranslationbot.services.PriceService;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public Price findById(Long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Цена с таким ID не найдена"));
    }

    @Override
    public void save(Price price) {
        priceRepository.save(price);
    }

    @Override
    public Page<Price> findByType(PaymentType paymentType, Pageable pageable) {
        return priceRepository.findByType(paymentType, pageable);
    }

    @Override
    public List<Price> findAll() {
        return priceRepository.findAll();
    }

    @Override
    public void deleteById(Long priceId) {
        priceRepository.deleteById(priceId);
    }
}
