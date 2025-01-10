package ru.eddyz.adminpaneltranslationbot.domain.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TelegramBotConfig {

    private String token;
    private String botUsername;
    private String adminUsername;
}
