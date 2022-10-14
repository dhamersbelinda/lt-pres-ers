package be.uclouvain.lt.pres.ers.server.config;

import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ServerConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MessageInterpolator defaultMessageInterpolator = Validation.byDefaultProvider().configure()
                .getDefaultMessageInterpolator();
        final MessageInterpolator localeSpecificMessageInterpolator = new LocaleSpecificMessageInterpolator(
                Locale.ENGLISH, defaultMessageInterpolator);
        final ValidatorFactory localeSpecificValidationFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(localeSpecificMessageInterpolator).buildValidatorFactory();
        final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidatorFactory(localeSpecificValidationFactory);
        return methodValidationPostProcessor;
    }

    /**
     * Implementation of {@link MessageInterpolator} that forces the use of a given
     * {@link Locale} if no one is provided to
     * {@link #interpolate(String, javax.validation.MessageInterpolator.Context)}.
     */
    private static class LocaleSpecificMessageInterpolator implements MessageInterpolator {

        private final Locale locale;
        private final MessageInterpolator delegate;

        public LocaleSpecificMessageInterpolator(final Locale locale, final MessageInterpolator delegate) {
            this.locale = locale;
            this.delegate = delegate;
        }

        @Override
        public String interpolate(final String messageTemplate, final Context context) {
            return this.delegate.interpolate(messageTemplate, context, this.locale);
        }

        @Override
        public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
            return this.delegate.interpolate(messageTemplate, context, locale);
        }

    }

}
