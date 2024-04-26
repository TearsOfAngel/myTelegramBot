package ru.vcarstein.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.vcarstein.CryptoTool;
import ru.vcarstein.dao.AppDocumentDAO;
import ru.vcarstein.dao.AppPhotoDAO;
import ru.vcarstein.entity.AppDocument;
import ru.vcarstein.entity.AppPhoto;
import ru.vcarstein.service.FileService;



@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String hash) {
        Long id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElseThrow();
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        Long id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElseThrow();
    }

}
