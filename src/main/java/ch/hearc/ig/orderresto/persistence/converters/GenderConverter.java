package ch.hearc.ig.orderresto.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/*
    Manage the already present technical debt given with the exercise
    Gender in database is a char "O" or "N" and in the application it's "F" or "M"
*/
@Converter
public class GenderConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String genderObjectValue) {

        if (genderObjectValue == "F") {
            return "O";
        } else {
            return "N";
        }
    }

    @Override
    public String convertToEntityAttribute(String genderDatabaseValue) {

        if (genderDatabaseValue == "O") {
            return "F";
        } else {
            return "H";
        }

    }

}
