package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.eddyz.adminpaneltranslationbot.domain.entities.LanguageTranslation;
import ru.eddyz.adminpaneltranslationbot.services.LanguageService;

@Route(value = "createLanguages", layout = MainView.class)
@PageTitle("Добавление поддерживаемых языков")
@PermitAll
public class CreatingLanguagesView extends HorizontalLayout {

    private final LanguageService languageService;

    private final TextField title;
    private final TextField code;
    private final VirtualList<LanguageTranslation> languages;


    public CreatingLanguagesView(LanguageService languageService) {
        this.languageService = languageService;
        setSizeFull();

        languages = new VirtualList<>();
        languages.setItems(languageService.findAll());
        languages.setRenderer(new ComponentRenderer<>(this::createRender));
        languages.setHeightFull();
        add(languages);
        expand(languages);

        var creatingBlock = new VerticalLayout();
        creatingBlock.addClassName("filter-block");
        creatingBlock.setWidth("30%");

        title = new TextField("Название языка");
        title.setWidthFull();
        code = new TextField("Код языка. Например: ru");
        code.setWidthFull();
        var btn = new Button("Добавить");
        btn.addClickListener(clickEvent -> addLanguage());

        creatingBlock.add(title, code, btn);
        add(creatingBlock);
    }

    private void addLanguage() {
        try {
            var title = this.title.getValue();
            var code = this.code.getValue();
            languageService.save(LanguageTranslation.builder()
                    .title(title)
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

    private Component createRender(LanguageTranslation languageTranslation) {
        var hor = new HorizontalLayout();
        hor.setWidthFull();
        hor.addClassName("telegram-list");

        var ver = new VerticalLayout();
        Span title = new Span(languageTranslation.getTitle());
        ver.add(title);

        var deleteBtn = new Button("Удалить");
        deleteBtn.addClickListener((clickEvent) -> {
            languageService.deleteById(languageTranslation.getLanguageId());
            refreshList();
        });
        deleteBtn.setHeightFull();

        hor.add(ver, deleteBtn);
        return hor;
    }

    private void refreshList() {
        languages.setItems(languageService.findAll());
    }


}
