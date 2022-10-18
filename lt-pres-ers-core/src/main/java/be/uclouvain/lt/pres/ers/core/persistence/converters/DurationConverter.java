package be.uclouvain.lt.pres.ers.core.persistence.converters;

import java.time.Duration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link AttributeConverter} that converts back and forth {@link Duration} and
 * {@link String}.
 */
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String> {

    public DurationConverter() {
        super();
    }

    @Override
    public String convertToDatabaseColumn(final Duration attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Duration convertToEntityAttribute(final String dbData) {
        return dbData != null ? Duration.parse(dbData) : null;
    }

}