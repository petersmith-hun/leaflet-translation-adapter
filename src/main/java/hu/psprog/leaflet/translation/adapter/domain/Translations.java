package hu.psprog.leaflet.translation.adapter.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Model object containing translation definitions for message source usage.
 *
 * @author Peter Smith
 */
public class Translations {

    private Map<String, Map<Locale, String>> translations;

    public Translations() {
        translations = new TreeMap<>();
    }

    /**
     * Adds a translation to the translations map.
     *
     * @param code translation key
     * @param locale translation locale
     * @param translation translated message in given locale
     */
    public void addTranslation(String code, Locale locale, String translation) {

        Map<Locale, String> translationsForCode = translations.get(code);
        if (Objects.isNull(translationsForCode)) {
            translations.put(code, createTranslationEntry(locale, translation));
        } else {
            translationsForCode.put(locale, translation);
        }
    }

    /**
     * Retrieves a translation from translations map.
     *
     * @param code code of the translation to retrieve
     * @param locale locale of the translation to retrieve in
     * @return translated message of the code itself if no translation was found
     */
    public String getTranslation(String code, Locale locale) {
        return Optional.ofNullable(translations.get(code))
                .map(translationEntry -> Optional
                        .ofNullable(translationEntry.get(locale))
                        .orElse(code))
                .orElse(code);
    }

    /**
     * Counts existing translations in translations map.
     *
     * @return number of translations
     */
    public long countTranslations() {
        return translations.values().stream()
                .mapToLong(translationEntry -> translationEntry.values().size())
                .sum();
    }

    private Map<Locale, String> createTranslationEntry(Locale locale, String translation) {

        Map<Locale, String> translationEntry = new HashMap<>();
        translationEntry.put(locale, translation);

        return translationEntry;
    }
}
