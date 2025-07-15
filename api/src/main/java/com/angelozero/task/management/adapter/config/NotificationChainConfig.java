package com.angelozero.task.management.adapter.config;


import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.NotificationHandler;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.SendEmailNotificationHandler;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.SendLogNotificationHandler;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.SendSmsNotificationHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@AllArgsConstructor
public class NotificationChainConfig {

    private final SendEmailNotificationHandler emailHandler;
    private final SendSmsNotificationHandler smsHandler;
    private final SendLogNotificationHandler logHandler;

    @Bean
    @Primary
    public NotificationHandler notificationChain() {
        // chain: email > sms > log (fallback)
        emailHandler.setNextHandler(smsHandler);
        smsHandler.setNextHandler(logHandler);

        return emailHandler;
    }
}