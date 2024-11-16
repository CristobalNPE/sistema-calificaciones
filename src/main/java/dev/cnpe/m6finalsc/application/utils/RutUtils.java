package dev.cnpe.m6finalsc.application.utils;

public class RutUtils {

    public static String formatRut(String rut) {

        String cleanedRut = cleanRut(rut);
        if (cleanedRut.isEmpty()) {
            return "";
        }

        String number = cleanedRut.substring(0, cleanedRut.length() - 1);
        String dv = cleanedRut.substring(cleanedRut.length() - 1);

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

        return rut.replace(".", "")
                  .replace("-", "")
                  .trim();
    }

}
