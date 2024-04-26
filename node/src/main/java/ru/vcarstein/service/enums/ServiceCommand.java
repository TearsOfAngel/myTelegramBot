package ru.vcarstein.service.enums;

public enum ServiceCommand {
    HELP("/help"),
    CANCEL("/cancel"),
    START("/start"),
    IP_CALC("/ipcalc"),
    CONFIG("/config");

    private final String value;

    ServiceCommand(String cmd) {
        this.value = cmd;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String value) {
        for (ServiceCommand command : ServiceCommand.values()) {
            if (command.value.equals(value)) {
                return command;
            }
        }
        return null;
    }
}
