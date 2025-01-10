package ru.eddyz.adminpaneltranslationbot.domain.entities;


public enum Assets {
    XTR("Звезды телеграм"), USDT("USDT"), TON("TON");

    private final String asset;

    Assets(String asset) {
        this.asset = asset;
    }

    public String toString() {
        return asset;
    }
}
