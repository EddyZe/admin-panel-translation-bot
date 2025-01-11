package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.eddyz.adminpaneltranslationbot.domain.entities.DeletedGroup;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Group;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Payment;
import ru.eddyz.adminpaneltranslationbot.domain.entities.User;
import ru.eddyz.adminpaneltranslationbot.services.DeletedGroupService;
import ru.eddyz.adminpaneltranslationbot.services.GroupService;
import ru.eddyz.adminpaneltranslationbot.services.PaymentService;
import ru.eddyz.adminpaneltranslationbot.services.UserService;

import java.time.format.DateTimeFormatter;


@Route(value = "users", layout = MainView.class)
@PageTitle("Пользователи")
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService userService;
    private final GroupService groupService;
    private final PaymentService paymentService;
    private final DeletedGroupService deletedGroupService;
    private GridListDataView<User> dataView;
    private final TextField search;

    public UsersView(UserService userService, GroupService groupService, PaymentService paymentService, DeletedGroupService deletedGroupService) {
        this.userService = userService;
        this.groupService = groupService;
        this.paymentService = paymentService;
        this.deletedGroupService = deletedGroupService;
        setSizeFull();

        var users = configUsers();

        search = new TextField("Поиск по username");
        search.setPlaceholder("Поиск...");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(user -> {
            String searchTerm = search.getValue().trim();
            if (searchTerm.isEmpty())
                return true;

            return user.getUsername().toLowerCase().contains(searchTerm);
        });

        add(search, users);
    }


    public Grid<User> configUsers() {
        final Grid<User> users;

        users = new Grid<>(User.class, false);
        users.addColumn(User::getUserId).setHeader("ID").setAutoWidth(true);
        users.addColumn(User::getUsername).setHeader("Username").setAutoWidth(true);
        users.addColumn(User::getChatId).setHeader("ID telegram").setAutoWidth(true);
        users.addColumn(user -> groupService.findByChatId(user.getChatId()).size())
                .setHeader("Добавлено групп")
                .setAutoWidth(true);
        users.setItemDetailsRenderer(createRenderUser());

        dataView = users.setItems(userService.findAll());
        return users;
    }

    private ComponentRenderer<UserLayout, User> createRenderUser() {
        return new ComponentRenderer<>(UserLayout::new,
                (userLayout, user) -> userLayout.setUser(user, groupService, paymentService, deletedGroupService));
    }

    private static class UserLayout extends FormLayout {

        private final Grid<Group> groups = new Grid<>(Group.class, false);
        private final Grid<Payment> payments = new Grid<>(Payment.class, false);

        public UserLayout() {
            setSizeFull();

            TextField titleGroup = new TextField();
            titleGroup.setValue("Группы");
            TextField titlePayment = new TextField();
            titlePayment.setValue("Платежи");
            titleGroup.setReadOnly(true);
            titlePayment.setReadOnly(true);
            add(titleGroup, titlePayment, groups, payments);

            setResponsiveSteps(new ResponsiveStep("0", 4));
            setColspan(titleGroup, 2);
            setColspan(titlePayment, 2);
            setColspan(groups, 2);
            setColspan(payments, 2);
        }

        public void setUser(User user, GroupService groupService, PaymentService paymentService,
                            DeletedGroupService deletedGroupService) {
            var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            groups.addColumn(Group::getGroupId).setHeader("ID").setAutoWidth(true);
            groups.addColumn(Group::getTitle).setHeader("Название").setAutoWidth(true);
            groups.addComponentColumn(group -> createEditMenu(groupService, group))
                    .setHeader("Символы");
            groups.addComponentColumn(group -> createDeleteBtn(groupService, deletedGroupService, group))
                    .setHeader("Удалить");
            groups.setItems(groupService.findByChatId(user.getChatId()));

            payments.setAriaLabel("Платежи пользователя");
            payments.addColumn(Payment::getPaymentId).setHeader("ID").setAutoWidth(true);
            payments.addColumn(payment -> {
                        boolean integerNumber = payment.getAmount() == payment.getAmount().intValue();
                        return "%s"
                                .formatted(
                                        integerNumber ?
                                                String.valueOf(payment.getAmount().intValue()) :
                                                payment.getAmount().toString());
                    })
                    .setHeader("Сумма").setAutoWidth(true);
            payments.addColumn(Payment::getAsset)
                    .setHeader("Валюта")
                    .setAutoWidth(true);
            payments.addColumn(payment -> payment.getNumberCharacters().toString())
                    .setHeader("Кол-во символов")
                    .setAutoWidth(true);
            payments.addColumn(payment -> dtf.format(payment.getCreatedAt()))
                    .setHeader("Дата и время")
                    .setAutoWidth(true)
                    .setSortable(true);

            payments.setItems(paymentService.findByChatId(user.getChatId()));
        }

        private Component createEditMenu(GroupService groupService, Group group) {
            var block = new HorizontalLayout();
            block.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            var editField = new NumberField();
            var icon = VaadinIcon.CHECK_CIRCLE_O.create();

            editField.setValue(group.getLimitCharacters().doubleValue());
            editField.setWidth("60%");
            icon.setColor("green");
            icon.addClickListener(click -> {
                group.setLimitCharacters(editField.getValue().intValue());
                groupService.update(group);
                taste();
            });

            block.add(editField, icon);
            return block;
        }

        private void taste() {
            Notification notification = Notification.show("Кол-во символов в группе изменено", 3000,
                    Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }

        private Icon createDeleteBtn(GroupService groupService, DeletedGroupService deletedGroupService, Group group) {
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

}
