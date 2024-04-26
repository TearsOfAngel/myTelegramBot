package ru.vcarstein.ipcalc;

import java.util.Arrays;
import java.util.StringJoiner;

public class IpCalculator {

    public static String calculate(String network) {

        String[] parts = network.split("/");
        String ipAddress = parts[0];
        String subnetMask = parts[1];
        int subnetInt = Integer.parseInt(subnetMask);

        int[] ipAddressOctets = parseIpOctets(ipAddress);
        int[] subnetMaskOctets = parseIpOctets(convertSubnetMask(subnetMask));
        int[] networkAddressOctets = calculateNetwork(ipAddressOctets, subnetMaskOctets);
        int[] broadcastAddressOctets = calculateBroadcastAddress(networkAddressOctets, subnetMaskOctets);

        String networkString = fromArrayToString(networkAddressOctets);
        String subnetString = fromArrayToString(subnetMaskOctets);
        String hostMin = calculateHostMin(networkAddressOctets);
        String hostMax = calculateHostMax(broadcastAddressOctets);
        String broadcastAddress = subnetInt < 31 ? fromArrayToString(broadcastAddressOctets) : "";
        int hosts = calculateMaxHostsAmount(Integer.parseInt(subnetMask));
        String networkClass = determineNetworkClass(networkAddressOctets);

        return "Network: " + networkString + "\n" +
                "Netmask: " + subnetString + "\n" +
                "HostMin: " + hostMin + "\n" +
                "HostMax: " + hostMax + "\n" +
                "Broadcast: " + broadcastAddress + "\n" +
                "Hosts/Net: " + hosts +"\n" +
                "Class: " + networkClass;
    }

    private static int[] parseIpOctets(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        int[] ipOctets = new int[4];
        for (int i = 0; i < 4; i++) {
            ipOctets[i] = Integer.parseInt(octets[i]);
        }
        return ipOctets;
    }

    private static String calculateHostMin(int[] networkAddress) {
        int[] hostMin = Arrays.copyOf(networkAddress, 4);
        int lastOctet = hostMin[3];

        if (lastOctet < 255) {
            lastOctet += 1;
        }
        hostMin[3] = lastOctet;
        return fromArrayToString(hostMin);
    }

    private static String calculateHostMax(int[] broadcastAddress) {
        int[] hostMax = Arrays.copyOf(broadcastAddress, 4);
        int lastOctet = hostMax[3];

        if (lastOctet > 0) {
            lastOctet -= 1;
        }
        hostMax[3] = lastOctet;
        return fromArrayToString(hostMax);
    }

    private static int calculateMaxHostsAmount(int subnetMask) {
        if (subnetMask == 32) {
            return 1;
        } else if (subnetMask == 31) {
            return 2;
        } else {
            int bitsForSubnet = 32 - subnetMask;
            return (int) Math.pow(2, bitsForSubnet) - 2;
        }
    }

    private static String fromArrayToString(int[] address) {
        StringJoiner joiner = new StringJoiner(".");
        for (int octet : address) {
            String stringOctet = String.valueOf(octet);
            joiner.add(stringOctet);
        }
        return joiner.toString();
    }

    private static int[] calculateNetwork(int[] ipAddress, int[] subnetMask) {
        int[] networkAddress = new int[ipAddress.length];
        for (int i = 0; i < ipAddress.length; i++) {
            networkAddress[i] = ipAddress[i] & subnetMask[i];
        }
        return networkAddress;
    }

    private static int[] calculateBroadcastAddress(int[] networkAddress, int[] subnetMask) {
        if (networkAddress.length != 4 || subnetMask.length != 4) {
            throw new IllegalArgumentException("Array must contain 4 octets");
        }

        int[] broadcastAddress = new int[4];
        for (int i = 0; i < 4; i++) {
            broadcastAddress[i] = (networkAddress[i] | (~subnetMask[i] & 0xFF));
        }

        return broadcastAddress;
    }

    private static String convertSubnetMask(String subnetMask) {
        int maskValueInt = Integer.parseInt(subnetMask);

        int octet1 = 0, octet2 = 0, octet3 = 0, octet4 = 0;

        if (maskValueInt >= 8) {
            octet1 = 255;
            maskValueInt -= 8;
        } else {
            octet1 = (int) (256 - Math.pow(2, 8 - maskValueInt));
            maskValueInt = 0;
        }
        if (maskValueInt >= 8) {
            octet2 = 255;
            maskValueInt -= 8;
        } else if (maskValueInt > 0) {
            octet2 = (int) (256 - Math.pow(2, 8 - maskValueInt));
            maskValueInt = 0;
        }
        if (maskValueInt >= 8) {
            octet3 = 255;
            maskValueInt -= 8;
        } else if (maskValueInt > 0) {
            octet3 = (int) (256 - Math.pow(2, 8 - maskValueInt));
            maskValueInt = 0;
        }
        if (maskValueInt >= 8) {
            octet4 = 255;
        } else if (maskValueInt > 0) {
            octet4 = (int) (256 - Math.pow(2, 8 - maskValueInt));
        }

        return octet1 + "." + octet2 + "." + octet3 + "." + octet4;
    }

    private static String determineNetworkClass(int[] ipAddress) {
        if (ipAddress.length != 4) {
            throw new IllegalArgumentException("Array must contain 4 octets");
        }

        int firstOctet = ipAddress[0];
        if (firstOctet >= 0 && firstOctet <= 127) {
            return "A";
        } else if (firstOctet >= 128 && firstOctet <= 191) {
            return "B";
        } else if (firstOctet >= 192 && firstOctet <= 223) {
            return "C";
        } else if (firstOctet >= 224 && firstOctet <= 239) {
            return "D";
        } else if (firstOctet >= 240 && firstOctet <= 255) {
            return "E";
        } else {
            throw new IllegalArgumentException("Wrong first octet range");
        }
    }
}
