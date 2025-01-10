package ru.eddyz.adminpaneltranslationbot.services;




import ru.eddyz.adminpaneltranslationbot.domain.entities.DeletedGroup;

import java.util.Optional;

public interface DeletedGroupService {

    void save(DeletedGroup group);

    Optional<DeletedGroup> findByTelegramGroupId(Long id);
}
