package hu.psprog.leaflet.translation.adapter.conversion;

import hu.psprog.leaflet.translation.adapter.domain.Translations;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Converts {@link Set} of {@link TranslationPack} objects to {@link Translations} model object.
 *
 * @author Peter Smith
 */
@Component
public class TranslationPackSetToTranslationsConverter implements Converter<Set<TranslationPack>, Translations> {

    @Override
    public Translations convert(Set<TranslationPack> source) {

        Translations translations = new Translations();

        source.forEach(translationPack -> translationPack.getDefinitions()
                .forEach(definition -> translations.addTranslation(definition.getKey(), translationPack.getLocale(), definition.getValue())));

        return translations;
    }
}
