package ru.eddyz.adminpaneltranslationbot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpaneltranslationbot.domain.entities.LanguageTranslation;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageTranslation, Long> {

    Optional<LanguageTranslation> findByCode(String code);

    @Modifying
    @Query(value = "DELETE FROM language_translations_groups WHERE language_id = :languageId", nativeQuery = true)
    void deleteGroupLanguageLinks(@Param("languageId") Long languageId);
}
