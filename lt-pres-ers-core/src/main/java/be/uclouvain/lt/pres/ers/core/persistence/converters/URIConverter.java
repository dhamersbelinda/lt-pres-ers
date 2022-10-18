package be.uclouvain.lt.pres.ers.core.persistence.converters;

import java.net.URI;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link AttributeConverter} that converts back and forth {@link URI} and
 * {@link String}.
 */
@Converter(autoApply = true)
public class URIConverter implements AttributeConverter<URI, String> {

    public URIConverter() {
        super();
    }

    @Override
    public String convertToDatabaseColumn(final URI attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public URI convertToEntityAttribute(final String dbData) {
        return dbData != null ? URI.create(dbData) : null;
    }

}