package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import ru.eddyz.adminpaneltranslationbot.domain.entities.TranslationMessage;
import ru.eddyz.adminpaneltranslationbot.services.TranslationMessagesService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Route(value = "historyTranslation", layout = MainView.class)
@PermitAll
@PageTitle("История переведенных сообщений")
@Slf4j
public class TranslationMessagesView extends HorizontalLayout {

    private final TranslationMessagesService translationMessagesService;

    private final DatePicker startDate;
    private final DatePicker endDate;

    private final TextField search;

    private final Grid<TranslationMessage> translationMessages;
    private GridListDataView<TranslationMessage> dataView;

    private final LocalDate minDate = LocalDate.of(2023, 1, 1);
    private final LocalDate maxDate = LocalDate.now().plusDays(1);

    public TranslationMessagesView(TranslationMessagesService translationMessagesService) {
        this.translationMessagesService = translationMessagesService;
        setSizeFull();

        var mainBlock = new VerticalLayout();
        var messages = new HorizontalLayout();
        messages.setSizeFull();
        mainBlock.setSizeFull();

        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        translationMessages = new Grid<>(TranslationMessage.class, false);
        translationMessages.addClassName("custom-grid");
        translationMessages.setSizeFull();
        translationMessages.addColumn(TranslationMessage::getMessageId).setHeader("ID")
                .setAutoWidth(true);
        translationMessages.addColumn(TranslationMessage::getFromUsername).setHeader("Отправитель");
        translationMessages.addColumn(translationMessage -> translationMessage.getGroup()
                        .getTitle())
                .setAutoWidth(true)
                .setHeader("Группа");
        translationMessages.addColumn(translationMessage -> dtf.format(translationMessage.getTranslationTime()))
                .setHeader("Дата перевода")
                .setAutoWidth(true)
                .setSortable(true);
        translationMessages.addColumn(TranslationMessage::getNumberCharacters).setHeader("Символов")
                .setAutoWidth(true);
        translationMessages.addComponentColumn(message -> createTextAriaReadOnly(message.getMessage()))
                .setAutoWidth(true)
                .setHeader("Сообщение");
        translationMessages.addComponentColumn(message -> createTextAriaReadOnly(message.getMessageTranslate()))
                .setAutoWidth(true)
                .setHeader("Перевод");
        dataView = translationMessages.setItems(getTranslationMessages());

        var filterBlock = new FormLayout();
        filterBlock.setWidth("80%");

        startDate = new DatePicker("От");
        endDate = new DatePicker("До");

        checkerDate();
        search = new TextField("Поиск по username или группе");
        search.setWidthFull();
        search.setPlaceholder("Search");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(e -> dataView.refreshAll());

        var filterButton = new Button("Применить");

        filterButton.addClickListener(e -> dataView = translationMessages.setItems(getTranslateMessages()));

        dataView.addFilter(message -> {
            String searchTerm = search.getValue().trim();
            if (searchTerm.isEmpty())
                return true;

            return Optional.ofNullable(message.getFromUsername()).orElse("")
                           .toLowerCase().contains(searchTerm.toLowerCase()) ||
                   message.getGroup().getTitle().toLowerCase().contains(searchTerm.toLowerCase());
        });

        filterBlock.add(search, startDate, endDate, filterButton);
        filterBlock.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        filterBlock.setColspan(search, 1);
        filterBlock.setColspan(startDate, 1);
        filterBlock.setColspan(endDate, 1);
        filterBlock.setColspan(filterButton, 1);

        mainBlock.add(filterBlock, translationMessages);

        add(mainBlock);

    }

    private List<TranslationMessage> getTranslationMessages() {
        return translationMessagesService.findAll()
                .stream()
                .sorted((o1, o2) -> {
                    if (o2.getTranslationTime().isAfter(o1.getTranslationTime()))
                        return 1;
                    else if (o2.getTranslationTime().isBefore(o1.getTranslationTime()))
                        return -1;
                    else
                        return 0;
                })
                .toList();
    }

    private TextArea createTextAriaReadOnly(String message) {
        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setMaxHeight("150px");
        textArea.setReadOnly(true);
        textArea.setValue(message);
        return textArea;
    }

    private List<TranslationMessage> getTranslateMessages() {
        return translationMessagesService.findByCreatedAtPeriod(
                getDate(startDate, minDate),
                getDate(endDate, maxDate).plusDays(1));


    }

    private LocalDateTime getDate(DatePicker picker, LocalDate of) {
        return picker.getValue() == null ? LocalDateTime.of(of,
                LocalTime.of(0, 0))
                : LocalDateTime.of(picker.getValue(), LocalTime.of(0, 0));
    }

    private void checkerDate() {
        startDate.addOpenedChangeListener(openedChangeEvent -> {
            if (startDate.getValue() != null && endDate.getValue() != null) {
                if (startDate.getValue().isAfter(endDate.getValue())) {
                    startDate.setValue(endDate.getValue());
                }
            }
        });

        endDate.addOpenedChangeListener(openedChangeEvent -> {
            if (endDate.getValue() != null) {
                if (endDate.getValue().isAfter(LocalDate.now())) {
                    endDate.setValue(LocalDate.now());
                }
            }
        });
    }
}
