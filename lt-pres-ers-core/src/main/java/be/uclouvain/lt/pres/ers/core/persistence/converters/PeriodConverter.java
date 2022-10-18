package be.uclouvain.lt.pres.ers.core.persistence.converters;

import java.time.Period;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link AttributeConverter} that converts back and forth {@link Period} and
 * {@link String}.
 */
@Converter(autoApply = true)
public class PeriodConverter implements AttributeConverter<Period, String> {

    public PeriodConverter() {
        super();
    }

    @Override
    public String convertToDatabaseColumn(final Period attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Period convertToEntityAttribute(final String dbData) {
        return dbData != null ? Period.parse(dbData) : null;
    }

}