package ru.eddyz.adminpaneltranslationbot.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eddyz.adminpaneltranslationbot.domain.entities.LanguageTranslation;
import ru.eddyz.adminpaneltranslationbot.repositories.LanguageRepository;
import ru.eddyz.adminpaneltranslationbot.services.LanguageService;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public LanguageTranslation findByCode(String code) {
        return languageRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Языка с кодом %s нет в базе".formatted(code)));
    }

    @Override
    public Page<LanguageTranslation> finaAll(Pageable pageable) {
        return languageRepository.findAll(pageable);
    }

    @Override
    public List<LanguageTranslation> findAll() {
        return languageRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        languageRepository.deleteById(id);
    }

    @Override
    public LanguageTranslation findById(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Язык с таким ID не найден."));
    }

    @Override
    public void save(LanguageTranslation languageTranslation) {
        if (languageRepository.findByCode(languageTranslation.getCode()).isPresent())
            throw new IllegalArgumentException("Язык с таким кодом существует!");

        languageRepository.save(languageTranslation);
    }

    @Override
    public void update(LanguageTranslation languageTranslation) {
        languageRepository.save(languageTranslation);
    }

    @Override
    @Transactional
    public void deleteLinksGroup(Long languageId) {
        languageRepository.deleteGroupLanguageLinks(languageId);
    }


}
