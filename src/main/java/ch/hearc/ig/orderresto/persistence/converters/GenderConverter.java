package ch.hearc.ig.orderresto.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


/**
 * Converts gender between application and database representations.
 * Here to manage the already present technical debt given with the exercise.
 * Gender in database is a char "O" or "N" and in the application it's "F" or "H".
 * "O" represents "Oui" and "N" "Non", "F" represents "Femme" and "H" "Homme"
 */
@Converter
public class GenderConverter implements AttributeConverter<String, String> {

    /**
     * Converts application gender ("F" or "H") to database value ("O" or "N").
     *
     * @param genderObjectValue the application gender value.
     * @return the database gender value.
     */
    @Override
    public String convertToDatabaseColumn(String genderObjectValue) {

        if (genderObjectValue == "F") {
            return "O";
        } else {
            return "N";
        }
    }

    /**
     * Converts database gender value ("O" or "N") to application value ("F" or "H").
     *
     * @param genderDatabaseValue the database gender value.
     * @return the application gender value.
     */
    @Override
    public String convertToEntityAttribute(String genderDatabaseValue) {

        if (genderDatabaseValue == "O") {
            return "F";
        } else {
            return "H";
        }

    }

}
