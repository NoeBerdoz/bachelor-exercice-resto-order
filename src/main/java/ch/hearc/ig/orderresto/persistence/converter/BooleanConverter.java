package ch.hearc.ig.orderresto.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {

        @Override
        public String convertToDatabaseColumn(Boolean boolValue) {
            return Boolean.TRUE.equals(boolValue) ? "O" : "N";
        }

    @Override
    public Boolean convertToEntityAttribute(String databaseValue) {
            return "O".equals(databaseValue);
    }

}
