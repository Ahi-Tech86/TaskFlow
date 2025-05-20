package com.ahicode.TextMe.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportConsumer {

    @RabbitListener(queues = "reports_queue")
    public void receiveMessage(String message) {
        log.info("Received message {}", message);
    }
}
