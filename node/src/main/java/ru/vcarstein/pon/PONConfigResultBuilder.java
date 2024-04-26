package ru.vcarstein.pon;

import com.ibm.icu.text.Transliterator;
import lombok.extern.log4j.Log4j;

@Log4j
public class PONConfigResultBuilder {

    public static String buildConfigString(String mac, String vlan, String address, String login) {
        StringBuilder configBuilder = new StringBuilder();
        Transliterator toLatinTranslator = Transliterator.getInstance("Russian-Latin/BGN");
        String id = getIdFromLogin(login);
        String addressLatin = toLatinTranslator.transliterate(address);
        String macAddressValue = macAddressValidateAndReturn(mac);

        configBuilder.append("ONT MAC:\n")
                .append(macAddressValue)
                .append("\n")
                .append("\n");

        configBuilder.append("ONT general settings\n").append("ID: ").append(id).append("\n")
                .append("Desc: AH:")
                .append(login)
                .append(",")
                .append(addressLatin).append(",id").append(id)
                .append("\n")
                .append("\n");

        if (isVlanIdValid(vlan)) {
            configBuilder.append("ONT rules\n")
                    .append("13: if (VID == ")
                    .append(vlan)
                    .append(") then DeleteTag; forward\n")
                    .append("13: if (Always) then AddTagVID = ")
                    .append(vlan)
                    .append("\n");
        } else {
            configBuilder.append("ONT rules\n")
                    .append("Vlan id is invalid");
        }

        return configBuilder.toString();
    }

    private static String getIdFromLogin(String login) {
        return login.replaceAll("\\D+","");
    }

    private static String macAddressValidateAndReturn(String mac) {
        mac = mac.replaceAll("[^a-fA-F0-9]", "");

        if (mac.length() != 12) {
            log.error("Invalid MAC address length: " + mac.length());
            return "Invalid MAC address length. Should be 12 symbols";
        }

        StringBuilder formattedMac = new StringBuilder();
        for (int i = 0; i < mac.length(); i += 2) {
            formattedMac.append(mac, i, i + 2).append(":");
        }
        formattedMac.setLength(formattedMac.length() - 1);

        return formattedMac.toString().toUpperCase();
    }

    private static boolean isVlanIdValid(String vlanId) {
        try {
            Integer.parseInt(vlanId);
        } catch (NumberFormatException ex) {
            log.error("Invalid vlan id format. " + ex.getMessage());
            return false;
        }
        return true;
    }
}
