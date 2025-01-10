package ru.eddyz.adminpaneltranslationbot.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Price;
import ru.eddyz.adminpaneltranslationbot.domain.enums.PaymentType;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    Page<Price> findByType(PaymentType type, Pageable pageable);
}
