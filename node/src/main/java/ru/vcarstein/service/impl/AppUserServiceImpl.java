package ru.vcarstein.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.vcarstein.CryptoTool;
import ru.vcarstein.dao.AppUserDAO;
import ru.vcarstein.entity.AppUser;
import ru.vcarstein.service.AppUserService;

@Log4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDAO appUserDAO;

    @Override
    public void registerUser(AppUser appUser) {
        appUserDAO.save(appUser);
    }
}
