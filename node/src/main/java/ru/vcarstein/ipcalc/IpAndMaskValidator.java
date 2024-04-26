package ru.vcarstein.ipcalc;

import lombok.extern.log4j.Log4j;
import ru.vcarstein.exceptions.WrongIpCalcInputException;

import java.util.regex.Pattern;

@Log4j
public class IpAndMaskValidator {

    private static final String IP_REGEX = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";

    private static final String SUBNET_MASK_REGEX = "^([0-9]|[12]\\d|3[0-2])$";

    public static String validateAndReturnResult(String ipAndMask) {

        try {
            if (isIpAndMaskValid(ipAndMask)) {
                return IpCalculator.calculate(ipAndMask);
            } else {
                return "IP address and mask are not valid";
            }
        } catch (WrongIpCalcInputException e) {
            return e.getMessage();
        }
    }

    private static boolean isIpAndMaskValid(String ipAndMask) {
        String[] parts = ipAndMask.split("/");
        if (parts.length != 2)
            throw new WrongIpCalcInputException("Wrong arguments. Must be IP and subnet mask. Like 192.168.0.1/24");

        String ipAddress = parts[0];
        String subnetMask = parts[1];

        if (!Pattern.matches(IP_REGEX, ipAddress)) throw new WrongIpCalcInputException("Wrong IP address");
        if (!Pattern.matches(SUBNET_MASK_REGEX, subnetMask)) throw new WrongIpCalcInputException("Wrong subnet mask");

        int subnetMaskValue = Integer.parseInt(subnetMask);
        if (subnetMaskValue < 0 || subnetMaskValue > 32)
            throw new WrongIpCalcInputException("Subnet mask must be between 0 and 32");

        String[] octets = ipAddress.split("\\.");
        for (String octet : octets) {
            int octetValue = Integer.parseInt(octet);
            if (octetValue < 0 || octetValue > 255)
                throw new WrongIpCalcInputException("IP address octects must be between 0 and 255");
        }

        return true;
    }
}

