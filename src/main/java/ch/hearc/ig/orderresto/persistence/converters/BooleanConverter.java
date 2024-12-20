package ch.hearc.ig.orderresto.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts Boolean values between application and database representations.
 * Application uses `true`/`false`; database uses "O" for "Oui" (true) and "N" for "Non" (false).
 */
@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    /**
     * Converts a Boolean value to its database representation ("O" or "N").
     *
     * @param boolValue the Boolean value to convert.
     * @return "O" for true, "N" for false.
     */
    @Override
    public String convertToDatabaseColumn(Boolean boolValue) {
        return Boolean.TRUE.equals(boolValue) ? "O" : "N";
    }

    /**
     * Converts a database value ("O" or "N") to a Boolean.
     *
     * @param databaseValue the database value to convert.
     * @return true for "O", false otherwise.
     */
    @Override
    public Boolean convertToEntityAttribute(String databaseValue) {
        return "O".equals(databaseValue);
    }

}
