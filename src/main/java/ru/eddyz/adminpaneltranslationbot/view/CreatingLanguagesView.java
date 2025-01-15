package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.eddyz.adminpaneltranslationbot.domain.entities.LanguageTranslation;
import ru.eddyz.adminpaneltranslationbot.domain.enums.TranslatorService;
import ru.eddyz.adminpaneltranslationbot.services.LanguageService;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Route(value = "createLanguages", layout = MainView.class)
@PageTitle("Добавление поддерживаемых языков")
@PermitAll
public class CreatingLanguagesView extends HorizontalLayout {

    private final LanguageService languageService;

    private final TextField title;
    private final TextField code;
    private final Grid<LanguageTranslation> languages;
    private final ComboBox<TranslatorService> translators;


    public CreatingLanguagesView(LanguageService languageService) {
        this.languageService = languageService;
        setSizeFull();

        languages = new Grid<>(LanguageTranslation.class, false);
        languages.addColumn(LanguageTranslation::getLanguageId)
                .setHeader("ID")
                .setAutoWidth(true);
        languages.addColumn(LanguageTranslation::getTitle)
                .setHeader("Название")
                .setAutoWidth(true);
        languages.addColumn(LanguageTranslation::getCode)
                .setHeader("Код")
                .setAutoWidth(true);
        languages.addComponentColumn(this::buildTranslators).setAutoWidth(true)
                .setHeader("Переводчик");
        languages.addComponentColumn(languageTranslation ->
                createDeleteButton(languageService, languageTranslation))
                .setHeader("Удаление")
                .setAutoWidth(true);
        languages.setItems(languageService.findAll());
        languages.setHeightFull();
        add(languages);

        var creatingBlock = new VerticalLayout();
        creatingBlock.addClassName("filter-block");
        creatingBlock.setWidth("30%");

        title = new TextField("Название языка");
        title.setWidthFull();
        code = new TextField("Код языка. Например: ru");
        code.setWidthFull();
        translators = buildTranslators(null);
        translators.setLabel("Переводчик");
        var btn = new Button("Добавить");
        btn.addClickListener(clickEvent -> addLanguage());

        creatingBlock.add(title, code, translators, btn);
        add(creatingBlock);
    }

    private Component createDeleteButton(LanguageService languageService, LanguageTranslation languageTranslation) {
        var icon = VaadinIcon.CLOSE_SMALL.create();
        icon.setColor("red");
        icon.addClickListener(click -> {
            languageService.deleteLinksGroup(languageTranslation.getLanguageId());
            languageService.deleteById(languageTranslation.getLanguageId());
            languages.setItems(languageService.findAll());
        });
        return icon;
    }

    private ComboBox<TranslatorService> buildTranslators(LanguageTranslation languageTranslation) {
        var comboBox = new ComboBox<TranslatorService>();
        comboBox.setItems(TranslatorService.values());

        if (languageTranslation != null) {
            comboBox.setValue(languageTranslation.getTranslator());
            comboBox.addValueChangeListener(click -> {
                languageTranslation.setTranslator(click.getValue());
                languageService.update(languageTranslation);
                taste("Переводчик изменен", 3000, NotificationVariant.LUMO_SUCCESS);
            });
        }
        return comboBox;
    }

    private void addLanguage() {
        try {
            var title = this.title.getValue();
            var code = this.code.getValue();
            var translator = translators.getValue();
            languageService.save(LanguageTranslation.builder()
                    .title(title)
                    .translator(translator)
                    .code(code)
                    .build());

            taste("Язык успешно добавлен", 3000, NotificationVariant.LUMO_SUCCESS);
            refreshList();

            this.title.clear();
            this.code.clear();
        } catch (IllegalArgumentException e) {
            taste(e.getMessage(), 3000, NotificationVariant.LUMO_ERROR);
        }
    }

    private void taste(String message, int duration, NotificationVariant notificationVariant) {
        Notification notification = Notification.show(message, duration,
                Notification.Position.TOP_CENTER);
        notification.addThemeVariants(notificationVariant);
    }

    private void refreshList() {
        languages.setItems(languageService.findAll());
    }


}
