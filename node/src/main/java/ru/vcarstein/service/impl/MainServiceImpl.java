package ru.vcarstein.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.vcarstein.dao.AppUserDAO;
import ru.vcarstein.dao.RawDataDAO;
import ru.vcarstein.entity.AppDocument;
import ru.vcarstein.entity.AppPhoto;
import ru.vcarstein.entity.AppUser;
import ru.vcarstein.entity.RawData;
import ru.vcarstein.entity.enums.UserState;
import ru.vcarstein.exceptions.UploadFileException;
import ru.vcarstein.service.*;
import ru.vcarstein.ipcalc.IpCalcService;
import ru.vcarstein.pon.PONConfigService;
import ru.vcarstein.service.enums.LinkType;
import ru.vcarstein.service.enums.ServiceCommand;

import java.util.Arrays;
import java.util.Optional;

import static ru.vcarstein.entity.enums.UserState.BASIC_STATE;
import static ru.vcarstein.service.enums.ServiceCommand.*;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;

    private final ProducerService producerService;

    private final AppUserDAO appUserDAO;

    private final FileService fileService;

    private final AppUserService appUserService;

    private final PONConfigService ponConfigService;

    private final IpCalcService ipCalcService;

    @Transactional
    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output = "";

        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);

        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            String[] userCommandAndArgs = text.split(" ");
            //TODO: для дебага. Потом надо убрать
            System.out.println(Arrays.toString(userCommandAndArgs));
            output = processServiceCommand(appUser, userCommandAndArgs);
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    //TODO: Сюда добавить новых команд
    private String processServiceCommand(AppUser appUser, String[] userCommandAndArgs) {
        ServiceCommand userCommand = ServiceCommand.fromValue(userCommandAndArgs[0]);
        String[] args = Arrays.copyOfRange(userCommandAndArgs, 1, userCommandAndArgs.length);
        if (HELP.equals(userCommand)) {
            return help();
        } else if (START.equals(userCommand)) {
            appUserService.registerUser(appUser);
            return "Добро пожаловать! Чтобы посмотреть список доступных команд введите: /help";
        } else if (CONFIG.equals(userCommand)) {
            return ponConfigService.getPonConfiguration(args);
        } else if (IP_CALC.equals(userCommand)) {
            if (args.length > 0) {
                return ipCalcService.getNetworkInfo(args[0]);
            } else {
                return "Передайте IP-адрес и маску подсети";
            }
        } else {
            return "Неизвестная команда. Введите: /help для просмотра списка доступных команд";
        }
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var chatId = update.getMessage().getChatId();
        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! " + "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        long chatId = update.getMessage().getChatId();

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            String answer = "Фото успешно загружен! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }



    private String help() {
        return "Список доступных команд:\n" +
                "/cancel - отмена выполнения текущей команды;\n" +
                "/config MAC vlan адрес(улица и номер слитно) логин - команда для получения PON конфига;\n" +
                "/ipcalc IP/Mask - команда для утилиты ipcalc;";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> optionalPersistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (optionalPersistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return optionalPersistentAppUser.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
