package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.eddyz.adminpaneltranslationbot.domain.entities.DeletedGroup;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Group;
import ru.eddyz.adminpaneltranslationbot.domain.entities.TranslationMessage;
import ru.eddyz.adminpaneltranslationbot.services.DeletedGroupService;
import ru.eddyz.adminpaneltranslationbot.services.GroupService;
import ru.eddyz.adminpaneltranslationbot.services.TranslationMessagesService;
import ru.eddyz.adminpaneltranslationbot.services.UserService;

import java.time.format.DateTimeFormatter;

@Route(value = "groups", layout = MainView.class)
@PermitAll
@PageTitle("Список групп")
public class GroupListView extends VerticalLayout {

    private final GroupService groupService;
    private final UserService userService;
    private final TranslationMessagesService translationMessagesService;
    private final DeletedGroupService deletedGroupService;

    private final Grid<Group> groups;
    private GridListDataView<Group> dataView;
    private final TextField search;


    public GroupListView(GroupService groupService, UserService userService, TranslationMessagesService translationMessagesService, DeletedGroupService deletedGroupService) {
        this.groupService = groupService;
        this.userService = userService;
        this.translationMessagesService = translationMessagesService;
        this.deletedGroupService = deletedGroupService;
        setSizeFull();

        groups = new Grid<>(Group.class, false);
        configGroups();

        search = new TextField("Поиск по telegram id или имени группы");
        search.setWidth("24%");
        search.setPlaceholder("Поиск...");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(group -> {
            String searchTerm = search.getValue().trim();
            if (searchTerm.isEmpty())
                return true;

            return group.getTelegramGroupId().toString().toLowerCase().contains(searchTerm) ||
                   group.getTitle().toLowerCase().contains(searchTerm);
        });

        add(search, groups);
    }

    private void configGroups() {
        groups.setSizeFull();
        groups.addColumn(Group::getGroupId).setHeader("ID").setAutoWidth(true);
        groups.addColumn(group -> userService.findByChatId(group.getChatId()).orElseThrow().getUsername())
                .setHeader("Владелец")
                .setSortable(true)
                .setAutoWidth(true);
        groups.addColumn(Group::getTitle).setHeader("Название").setAutoWidth(true);
        groups.addColumn(Group::getTelegramGroupId).setHeader("ID telegram").setAutoWidth(true);
        groups.addColumn(group -> translationMessagesService.findByGroupChatId(group.getChatId()).size())
                .setHeader("Переведенных сообщений")
                .setAutoWidth(true);
        groups.addComponentColumn(this::createEditMenu).setHeader("Символы");
        groups.addComponentColumn(this::createDeleteBtn).setHeader("Удаление").setAutoWidth(true);
        groups.setItemDetailsRenderer(createRenderGroup());

        dataView = groups.setItems(groupService.findAll());
    }

    private Icon createDeleteBtn(Group group) {
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

    private Component createEditMenu(Group group) {
        var block = new HorizontalLayout();
        var editField = new NumberField();
        var icon = VaadinIcon.CHECK_CIRCLE_O.create();

        block.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        editField.setValue(group.getLimitCharacters().doubleValue());
        editField.setWidth("60%");
        icon.setColor("green");
        icon.addClickListener(click -> {
            group.setLimitCharacters(editField.getValue().intValue());
            groupService.update(group);
            taste("Кол-во символов изменено", 3000, NotificationVariant.LUMO_SUCCESS);
        });

        block.add(editField, icon);
        return block;
    }

    private void taste(String message, int duration, NotificationVariant not) {
        Notification notification = Notification.show(message, duration,
                Notification.Position.TOP_CENTER);
        notification.addThemeVariants(not);
    }

    private Renderer<Group> createRenderGroup() {
        return new ComponentRenderer<>(GroupListView.GroupView::new,
                (groupView, group) -> groupView.setGroup(group, translationMessagesService));
    }

    private static class GroupView extends VerticalLayout {

        private final Grid<TranslationMessage> messages = new Grid<>(TranslationMessage.class, false);

        public GroupView() {
            setSizeFull();


            TextField titleTranslateMessages = new TextField();
            titleTranslateMessages.setWidthFull();
            titleTranslateMessages.setValue("Переведенные сообщения");
            titleTranslateMessages.setReadOnly(true);
            add(titleTranslateMessages, messages);

//            setResponsiveSteps(new ResponsiveStep("0", 2));
//            setColspan(titleTranslateMessages, 2);
//            setColspan(messages, 2);
        }

        public void setGroup(Group group, TranslationMessagesService translationMessagesService) {
            var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            var translationMessages = translationMessagesService.findByGroupChatId(group.getChatId());


            messages.addComponentColumn(message -> createMessageComponent(message.getMessage()))
                    .setHeader("Оригинальное сообщение")
                    .setTextAlign(ColumnTextAlign.START)
                    .setAutoWidth(true);
            messages.addComponentColumn(message -> createMessageComponent(message.getMessageTranslate()))
                    .setHeader("Перевод")
                    .setTextAlign(ColumnTextAlign.START)
                    .setAutoWidth(true);
            messages.addColumn(message -> dtf.format(message.getTranslationTime())).setHeader("Время и дата")
                    .setSortable(true)
                    .setTextAlign(ColumnTextAlign.START)
                    .setAutoWidth(true);
            messages.setItems(translationMessages);
        }

        private Component createMessageComponent(String message) {
            var block = new HorizontalLayout();
            block.setSizeFull();
            block.setDefaultVerticalComponentAlignment(Alignment.START);
            var text = new TextArea();
            text.setWidthFull();
            text.setMaxHeight("100px");
            text.setReadOnly(true);
            text.setValue(message);
            block.add(text);
            return block;
        }
    }
}
