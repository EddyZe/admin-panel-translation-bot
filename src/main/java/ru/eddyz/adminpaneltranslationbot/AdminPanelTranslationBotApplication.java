package ru.eddyz.adminpaneltranslationbot;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@Theme(value = "my-theme")
@SpringBootApplication
public class AdminPanelTranslationBotApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AdminPanelTranslationBotApplication.class, args);
    }

}
