package dev.cnpe.m6finalsc.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidator implements ConstraintValidator<ValidRut, String> {

    @Override
    public boolean isValid(String rut, ConstraintValidatorContext constraintValidatorContext) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        rut = rut.replace(".", "").replace("-", "");

        if (!rut.matches("^[0-9]{7,8}[0-9Kk]$")) {
            return false;
        }

        String number = rut.substring(0, rut.length() - 1);
        char dv = Character.toUpperCase(rut.charAt(rut.length() - 1));

        int sum = 0;
        int multiplier = 2;

        for (int i = number.length() - 1; i >= 0; i--) {
            sum += Integer.parseInt(String.valueOf(number.charAt(i))) * multiplier;
            multiplier = multiplier == 7 ? 2 : multiplier + 1;
        }

        int remainder = sum % 11;
        char expectedDv = remainder == 0 ? '0' :
                remainder == 1 ? 'K' :
                        Character.forDigit(11 - remainder, 10);

        return dv == expectedDv;
    }



}
