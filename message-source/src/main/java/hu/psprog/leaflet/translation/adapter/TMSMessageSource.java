package hu.psprog.leaflet.translation.adapter;

import hu.psprog.leaflet.translation.adapter.domain.Translations;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.client.TranslationServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * {@link AbstractMessageSource} implementation to handle TMS based translations.
 * This message source is initialized by calling TMS with a list of translation pack names to retrieve for the application.
 * The list of translation pack names must be provided by the application.
 * After retrieving the available translation packs, every definitions are loaded to the message source.
 *
 * @author Peter Smith
 */
@Component
public class TMSMessageSource extends AbstractMessageSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TMSMessageSource.class);

    private ConversionService conversionService;
    private TranslationServiceClient translationServiceClient;
    private List<String> requiredPacks;
    private Translations translations;

    @Autowired
    public TMSMessageSource(ConversionService conversionService, TranslationServiceClient translationServiceClient,
                            @Value("${tms.packs}") List<String> requiredPacks) {
        this.conversionService = conversionService;
        this.translationServiceClient = translationServiceClient;
        this.requiredPacks = requiredPacks;
    }

    @PostConstruct
    public void initMessageSource() {

        LOGGER.info("Initializing TMS based message source.");

        Set<TranslationPack> translationPacks = translationServiceClient.retrievePacks(requiredPacks);
        logRetrievedPacks(translationPacks);

        translations = conversionService.convert(translationPacks, Translations.class);
        LOGGER.info("Loaded {} translations", translations.countTranslations());
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return createMessageFormat(translations.getTranslation(code, locale), locale);
    }

    private void logRetrievedPacks(Set<TranslationPack> translationPacks) {
        translationPacks.forEach(translationPack -> LOGGER.info("Retrieved translation pack [{}.{} (created at {})]",
                translationPack.getPackName(), translationPack.getLocale(), translationPack.getCreated()));
    }
}