package ru.eddyz.adminpaneltranslationbot.services.impls;

import org.springframework.stereotype.Service;
import ru.eddyz.adminpaneltranslationbot.domain.entities.DeletedGroup;
import ru.eddyz.adminpaneltranslationbot.repositories.DeletedGroupRepository;
import ru.eddyz.adminpaneltranslationbot.services.DeletedGroupService;

import java.util.Optional;


@Service
public class DeletedGroupServiceImpl implements DeletedGroupService {

    private final DeletedGroupRepository deletedGroupRepository;

    public DeletedGroupServiceImpl(DeletedGroupRepository deletedGroupRepository) {
        this.deletedGroupRepository = deletedGroupRepository;
    }


    @Override
    public void save(DeletedGroup group) {
        deletedGroupRepository.save(group);
    }

    @Override
    public Optional<DeletedGroup> findByTelegramGroupId(Long id) {
        return deletedGroupRepository.findByTelegramGroupId(id);
    }
}
