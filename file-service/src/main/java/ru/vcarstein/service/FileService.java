package ru.vcarstein.service;

import ru.vcarstein.entity.AppDocument;
import ru.vcarstein.entity.AppPhoto;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);

}
