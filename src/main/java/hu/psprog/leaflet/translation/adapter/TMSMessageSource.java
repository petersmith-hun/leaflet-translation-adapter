package hu.psprog.leaflet.translation.adapter;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.translation.adapter.conversion.TranslationPackSetToTranslationsConverter;
import hu.psprog.leaflet.translation.adapter.domain.Translations;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.client.MessageSourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * {@link AbstractMessageSource} implementation to handle TMS based translations.
 * This message source is initialized by calling TMS with a list of translation pack names to retrieve for the application.
 * The list of translation pack names must be provided by the application.
 * After retrieving the available translation packs, every definition is loaded to the message source.
 *
 * Configuration must be provided by the integrating application:
 * - tms.enabled: enables TMS based message source
 * - tms.packs: name of the packs that the application requests on start-up
 * - tms.forced-locale: optional, if specified, this will be used as the resolved locale for every request
 *
 * @author Peter Smith
 */
@Primary
@ConditionalOnProperty(value = "tms.enabled", havingValue = "true")
@Component("messageSource")
public class TMSMessageSource extends AbstractMessageSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TMSMessageSource.class);

    private final TranslationPackSetToTranslationsConverter translationsConverter;
    private final MessageSourceClient messageSourceClient;
    private final List<String> requiredPacks;
    private final Locale forcedLocale;
    private Translations translations;

    @Autowired
    public TMSMessageSource(TranslationPackSetToTranslationsConverter translationsConverter, MessageSourceClient messageSourceClient,
                            @Value("${tms.packs}") List<String> requiredPacks, @Value("${tms.forced-locale:}") Locale forcedLocale) {
        this.translationsConverter = translationsConverter;
        this.messageSourceClient = messageSourceClient;
        this.requiredPacks = requiredPacks;
        this.forcedLocale = forcedLocale;
    }

    @PostConstruct
    public void initMessageSource() throws CommunicationFailureException {

        LOGGER.info("Initializing TMS based message source.");

        Set<TranslationPack> translationPacks = messageSourceClient.retrievePacks(requiredPacks);
        logRetrievedPacks(translationPacks);

        translations = translationsConverter.convert(translationPacks);
        LOGGER.info("Loaded {} translations", translations.countTranslations());
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        Locale currentLocale = getLocaleToUse(locale);
        return createMessageFormat(translations.getTranslation(code, currentLocale), currentLocale);
    }

    private Locale getLocaleToUse(Locale currentLocale) {
        return Optional.ofNullable(forcedLocale)
                .orElse(currentLocale);
    }

    private void logRetrievedPacks(Set<TranslationPack> translationPacks) {
        translationPacks.forEach(translationPack -> LOGGER.info("Retrieved translation pack [{}-{} (created at {})]",
                translationPack.packName(), translationPack.locale(), translationPack.created()));
    }
}
