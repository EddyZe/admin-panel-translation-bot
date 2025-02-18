package ru.eddyz.adminpaneltranslationbot.domain.enums;





public enum ButtonSelectPaymentType {
    TELEGRAM_STARS_BTN("Звезды телеграм 🌟"), CRYPTO_PAY_BTN("Crypto pay 💳"),
    BACK_SETTING_GROUP("Назад ⏮️");

    private final String btn;

    ButtonSelectPaymentType(String btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return btn;
    }
}
