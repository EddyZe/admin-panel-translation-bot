package ru.eddyz.adminpaneltranslationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Group;
import ru.eddyz.adminpaneltranslationbot.repositories.GroupRepository;
import ru.eddyz.adminpaneltranslationbot.services.GroupService;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;



    @Override
    public List<Group> findByChatId(Long chatId) {
        return groupRepository.findByChatId(chatId);
    }

    @Override
    public void save(Group newGroup) {
        if (groupRepository.findByTelegramGroupId(newGroup.getTelegramGroupId()).isPresent())
            throw new IllegalArgumentException("Группа с таким ID существует!");

        groupRepository.save(newGroup);
    }

    @Override
    public void update(Group group) {
        groupRepository.save(group);
    }

    @Override
    public void deleteById(Long id) {
        if (groupRepository.findById(id).isEmpty())
            throw new NoSuchElementException("Группа с таким ID не найдена!");

        groupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteLinksLanguage(Long groupId) {
        groupRepository.deleteGroupLanguageLinks(groupId);
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Группа с таким ID не найдена!"));
    }

    @Override
    public List<Group> findByMinChars(Integer chars) {
        return groupRepository.findByMinChars(chars);
    }

    @Override
    public Group findByTelegramChatId(Long telegramGroupId) {
        return groupRepository.findByTelegramGroupId(telegramGroupId)
                .orElseThrow(() -> new NoSuchElementException("Такая группа не найдена!"));
    }

    @Override
    public void deleteByTelegramChatId(Long id) {
        groupRepository.deleteByTelegramGroupId(id);
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }
}
