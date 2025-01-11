package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Payment;
import ru.eddyz.adminpaneltranslationbot.services.PaymentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "historyPayments", layout = MainView.class)
@PermitAll
@PageTitle("История платежей")
@Transactional
public class HistoryPaymentsView extends HorizontalLayout {

    private final PaymentService paymentService;

    private final Grid<Payment> payments;
    private final DatePicker startDate;
    private final DatePicker endDate;

    private final TextField search;

    private GridListDataView<Payment> dataView;

    private final LocalDate minDate = LocalDate.of(2023, 1, 1);
    private final LocalDate maxDate = LocalDate.now().plusDays(1);

    public HistoryPaymentsView(PaymentService paymentService) {
        this.paymentService = paymentService;
        setSizeFull();

        var filterBlock = new VerticalLayout();
        filterBlock.setWidth("40%");
        filterBlock.addClassNames("filter-block");

        var dateBlock = new HorizontalLayout();
        startDate = new DatePicker("От");
        endDate = new DatePicker("До");

        checkerDate();

        dateBlock.add(startDate, endDate);

        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        payments = new Grid<>(Payment.class, false);
        payments.addColumn(Payment::getPaymentId).setHeader("ID").setAutoWidth(true);
        payments.addColumn(payment -> payment.getPayer().getUsername()).setHeader("Username");
        payments.addColumn(payment -> {
            boolean integerNumber = payment.getAmount() == payment.getAmount().intValue();
            return "%s"
                    .formatted(
                            integerNumber ?
                                    String.valueOf(payment.getAmount().intValue()) :
                                    payment.getAmount().toString());
        }).setHeader("Сумма");
        payments.addColumn(Payment::getAsset)
                .setAutoWidth(true)
                .setHeader("Валюта");
        payments.addColumn(Payment::getNumberCharacters).setHeader("Символы");
        payments.addColumn(payment -> dtf.format(payment.getCreatedAt()))
                .setHeader("Дата и время")
                .setAutoWidth(true)
                .setSortable(true);
        dataView = payments.setItems(getItemsPaymentList());
        search = new TextField("Поиск по username");
        search.setWidthFull();
        search.setPlaceholder("Search");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(e -> dataView.refreshAll());


        var filterButton = new Button("Применить");

        filterButton.addClickListener(e -> {
            dataView = payments.setItems(getItemsPaymentList());
        });

        dataView.addFilter(payment -> {
            String searchTerm = search.getValue().trim();
            if (searchTerm.isEmpty())
                return true;

            return payment.getPayer().getUsername().toLowerCase().contains(searchTerm.toLowerCase());
        });


        payments.setSizeFull();

        filterBlock.add(search, dateBlock, filterButton);
        add(payments, filterBlock);

    }

    private List<Payment> getItemsPaymentList() {
        return paymentService.findByCreatedAtPeriod(
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
