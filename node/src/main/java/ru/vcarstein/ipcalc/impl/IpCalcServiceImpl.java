package ru.vcarstein.ipcalc.impl;

import org.springframework.stereotype.Service;
import ru.vcarstein.ipcalc.IpAndMaskValidator;
import ru.vcarstein.ipcalc.IpCalcService;

@Service
public class IpCalcServiceImpl implements IpCalcService {
    @Override
    public String getNetworkInfo(String networkAndMask) {
        return IpAndMaskValidator.validateAndReturnResult(networkAndMask);
    }
}
