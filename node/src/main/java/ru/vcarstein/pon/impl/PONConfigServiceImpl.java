package ru.vcarstein.pon.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.vcarstein.pon.PONConfigResultBuilder;
import ru.vcarstein.pon.PONConfigService;

@Log4j
@Service
public class PONConfigServiceImpl implements PONConfigService {

    /*
    String params contains:
        1. MAC-address of NTE terminal
        2. vlan id (number between 1 and 4096)
        3. Client address (where he lives)
        4. Client login (two options: kom2413 or kt_2413 (depends on client type))
     */
    @Override
    public String getPonConfiguration(String[] params) {
        if (params.length != 4) {
            log.error("PON config: Invalid amount of parameters");
            return "Invalid amount of parameters";
        }

        String mac = params[0];
        String vlan = params[1];
        String address = params[2];
        String login = params[3];

        return PONConfigResultBuilder.buildConfigString(mac, vlan, address, login);
    }
}
