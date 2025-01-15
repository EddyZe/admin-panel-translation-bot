package ru.eddyz.adminpaneltranslationbot.util;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import ru.eddyz.adminpaneltranslationbot.domain.entities.DeletedGroup;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Group;
import ru.eddyz.adminpaneltranslationbot.services.DeletedGroupService;
import ru.eddyz.adminpaneltranslationbot.services.GroupService;

public class GridButton {

    public Icon createDeleteBtnGroup(GroupService groupService, DeletedGroupService deletedGroupService, Group group, Grid<Group> groups) {
        var icon = VaadinIcon.CLOSE_SMALL.create();
        icon.setColor("red");
        icon.addClickListener(clickEvent -> {
            groupService.deleteLinksLanguage(group.getGroupId());
            groupService.deleteById(group.getGroupId());
            groups.setItems(groupService.findAll());
            var deletedGroupOp = deletedGroupService.findByTelegramGroupId(group.getTelegramGroupId());

            if (deletedGroupOp.isEmpty()) {
                deletedGroupService.save(DeletedGroup.builder()
                        .telegramGroupId(group.getTelegramGroupId())
                        .chars(group.getLimitCharacters())
                        .build());
            } else {
                var deletedGroup = deletedGroupOp.get();
                deletedGroup.setChars(group.getLimitCharacters());
                deletedGroupService.save(deletedGroup);
            }
        });
        return icon;
    }
}
