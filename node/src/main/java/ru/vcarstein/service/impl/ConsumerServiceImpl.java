package ru.vcarstein.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vcarstein.service.ConsumerService;
import ru.vcarstein.service.MainService;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessageUpdates(Update update) {
        String textMessage = update.getMessage().getText();
        String sender = update.getMessage().getFrom().getUserName();
        log.debug("NODE: Text message received: " + textMessage + " from: " + sender);
        mainService.processTextMessage(update);
    }
}
