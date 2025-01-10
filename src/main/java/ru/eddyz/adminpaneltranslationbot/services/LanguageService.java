package ru.eddyz.adminpaneltranslationbot.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.adminpaneltranslationbot.domain.entities.LanguageTranslation;

import java.util.List;

public interface LanguageService {

    LanguageTranslation findByCode(String code);
    Page<LanguageTranslation> finaAll(Pageable pageable);

    List<LanguageTranslation> findAll();

    void deleteById(Long id);

    LanguageTranslation findById(Long id);

    void save(LanguageTranslation languageTranslation);
}
