package ru.eddyz.adminpaneltranslationbot.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
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
    private final VirtualList<Price> prices;
    private final Button save;


    public CreatingPaymentsView(PriceService priceService) {
        this.priceService = priceService;
        setSizeFull();

        prices = new VirtualList<>();
        prices.setItems(priceService.findAll());
        prices.setRenderer(new ComponentRenderer<>(this::createRender));
        prices.setHeightFull();
        add(prices);
        expand(prices);

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

    private Component createRender(Price price) {
        var hor = new HorizontalLayout();
        hor.setWidthFull();
        hor.addClassName("telegram-list");

        var ver = new VerticalLayout();

        Span title = new Span("%d символов за %.2f %s".formatted(price.getNumberCharacters(), price.getPrice(), price.getAsset()));
        title.setWidthFull();

        ver.add(title);

        var deleteBtn = new Button("Удалить");
        deleteBtn.addClickListener((clickEvent) -> {
            priceService.deleteById(price.getPriceId());
            refreshList();
        });
        deleteBtn.setHeightFull();

        hor.add(ver, deleteBtn);
        return hor;
    }

    private void refreshList() {
        prices.setItems(priceService.findAll());
    }


}
