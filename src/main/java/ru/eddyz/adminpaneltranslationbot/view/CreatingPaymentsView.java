package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Assets;
import ru.eddyz.adminpaneltranslationbot.domain.entities.Price;
import ru.eddyz.adminpaneltranslationbot.domain.enums.PaymentType;
import ru.eddyz.adminpaneltranslationbot.services.PriceService;

import java.util.Arrays;

@Route(value = "createPayments", layout = MainView.class)
@PageTitle("Создание платежей для покупки символов")
@PermitAll
public class CreatingPaymentsView extends HorizontalLayout {

    private final PriceService priceService;

    private final NumberField numberChars;
    private final NumberField amount;
    private final ComboBox<String> asset;
    private final Grid<Price> prices;
    private final Button save;


    public CreatingPaymentsView(PriceService priceService) {
        this.priceService = priceService;
        setSizeFull();

        prices = new Grid<>(Price.class, false);
        prices.addColumn(Price::getPriceId)
                .setHeader("ID")
                .setAutoWidth(true);
        prices.addColumn(Price::getNumberCharacters)
                .setHeader("Кол-во символов")
                .setAutoWidth(true)
                .setSortable(true);
        prices.addColumn(Price::getPrice)
                .setHeader("Сумма")
                .setAutoWidth(true);
        prices.addColumn(Price::getAsset)
                .setAutoWidth(true)
                .setHeader("Валюта");
        prices.addComponentColumn(price -> createDeleteButton(priceService, price));

        prices.setItems(priceService.findAll());
        prices.setHeightFull();
        add(prices);

        asset = new ComboBox<>("Валюта");
        asset.setAllowCustomValue(true);
        asset.setItems(Arrays.stream(Assets.values())
                .map(Assets::toString)
                .toList());
        add(asset);

        var creatingBlock = new VerticalLayout();
        creatingBlock.addClassName("filter-block");
        creatingBlock.setWidth("30%");

        numberChars = new NumberField("Кол-во символов");
        numberChars.setWidthFull();
        amount = new NumberField("Цена");
        amount.setWidthFull();
        save = new Button("Добавить");

        save.addClickListener(clickEvent -> addPrice());


        creatingBlock.add(numberChars, amount, asset, save);
        add(creatingBlock);
    }

    private Icon createDeleteButton(PriceService priceService, Price price) {
        var icon = VaadinIcon.CLOSE_SMALL.create();
        icon.setColor("red");
        icon.addClickListener(e -> {
            priceService.deleteById(price.getPriceId());
            refreshList();
        });
        return icon;
    }

    private void addPrice() {
        try {
            var chars = this.numberChars.getValue();
            var amount = this.amount.getValue();
            var asset = this.asset.getValue().equals(Assets.XTR.toString()) ? Assets.XTR : Assets.valueOf(this.asset.getValue());
            var paymentType = asset == Assets.XTR ? PaymentType.TELEGRAM_STARS : PaymentType.CRYPTO_PAY;

            var price = Price.builder()
                    .price(amount.floatValue())
                    .asset(asset.toString())
                    .numberCharacters(chars.intValue())
                    .type(paymentType)
                    .build();

            taste("Цена успешно добавлена.", 3000, NotificationVariant.LUMO_SUCCESS);

            priceService.save(price);
            refreshList();

            this.amount.clear();
            this.asset.clear();
            this.numberChars.clear();
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
        prices.setItems(priceService.findAll());
    }


}
