package ru.eddyz.adminpaneltranslationbot.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Payment;
import ru.eddyz.adminpaneltranslationbot.domain.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCreatedAt(LocalDateTime localDateTime);

    List<Payment> findByChatId(Long chatId);


    @Query(value = "select p from Payment as p where p.createdAt >= :startDate and p.createdAt <= :endDate")
    List<Payment> findByCreatedAtPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = "select sum(p.amount) from Payment as p where p.type = :type and p.createdAt >= :startDate and p.createdAt <= :endDate")
    Double countAmount(@Param("type") PaymentType type,
                       @Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);
}
