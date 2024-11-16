package dev.cnpe.m6finalsc.application.utils;

public class RutUtils {

    public static String formatRut(String rut) {

        if (rut == null || rut.isBlank()) {
            return "";
        }

        rut = rut.replace(".", "").replace("-", "").trim();

        String number = rut.substring(0, rut.length() - 1);
        String dv = rut.substring(rut.length() - 1);

        StringBuilder formatted = new StringBuilder();
        for (int i = number.length() - 1, count = 0; i >= 0; i--, count++) {
            if (count > 0 && count % 3 == 0) {
                formatted.insert(0, ".");
            }
            formatted.insert(0, number.charAt(i));
        }

        return formatted.toString() + "-" + dv;
    }


    public static String cleanRut(String rut) {
        if (rut == null || rut.isBlank()) {
            return "";
        }
        String cleanRut = rut.replace(".", "")
                             .replace("-", "")
                             .trim();

        return cleanRut;
    }

}
