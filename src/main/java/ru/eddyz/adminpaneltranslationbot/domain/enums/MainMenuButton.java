package ru.eddyz.adminpaneltranslationbot.domain.enums;


public enum MainMenuButton {


    MY_GROUPS("Мои группы 📋"), MY_HISTORY_PAYMENTS("История платежей 🏦"), ADD_GROUP("Добавить группу ➕");

    private final String btn;

    MainMenuButton(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
