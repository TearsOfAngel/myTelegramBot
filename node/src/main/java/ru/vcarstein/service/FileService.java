package ru.vcarstein.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vcarstein.entity.AppDocument;
import ru.vcarstein.entity.AppPhoto;
import ru.vcarstein.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
