package hu.psprog.leaflet.translation.adapter.conversion;

import hu.psprog.leaflet.translation.adapter.domain.Translations;
import hu.psprog.leaflet.translation.api.domain.TranslationDefinition;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link TranslationPackSetToTranslationsConverter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TranslationPackSetToTranslationsConverterTest {

    private static final String KEY_1 = "key-1";
    private static final String KEY_2 = "key-2";
    private static final String KEY_3 = "key-3";
    private static final String VALUE_1_EN = "value-1-en";
    private static final String VALUE_1_CA = "value-1-ca";
    private static final String VALUE_2_CA = "value-2-ca";
    private static final String VALUE_2_EN = "value-2-en";
    private static final TranslationDefinition TRANSLATION_DEFINITION_EN_1 = TranslationDefinition.getBuilder()
            .withKey(KEY_1)
            .withValue(VALUE_1_EN)
            .build();
    private static final TranslationDefinition TRANSLATION_DEFINITION_EN_2 = TranslationDefinition.getBuilder()
            .withKey(KEY_2)
            .withValue(VALUE_2_EN)
            .build();
    private static final TranslationDefinition TRANSLATION_DEFINITION_CA_1 = TranslationDefinition.getBuilder()
            .withKey(KEY_1)
            .withValue(VALUE_1_CA)
            .build();
    private static final TranslationDefinition TRANSLATION_DEFINITION_CA_2 = TranslationDefinition.getBuilder()
            .withKey(KEY_2)
            .withValue(VALUE_2_CA)
            .build();
    private static final TranslationPack TRANSLATION_PACK_1 = TranslationPack.getPackBuilder()
            .withLocale(Locale.ENGLISH)
            .withDefinitions(Arrays.asList(TRANSLATION_DEFINITION_EN_1, TRANSLATION_DEFINITION_EN_2))
            .build();
    private static final TranslationPack TRANSLATION_PACK_2 = TranslationPack.getPackBuilder()
            .withLocale(Locale.CANADA)
            .withDefinitions(Arrays.asList(TRANSLATION_DEFINITION_CA_1, TRANSLATION_DEFINITION_CA_2))
            .build();

    @InjectMocks
    private TranslationPackSetToTranslationsConverter converter;

    @Test
    public void shouldConvert() {

        // when
        Translations result = converter.convert(new HashSet<>(Arrays.asList(TRANSLATION_PACK_1, TRANSLATION_PACK_2)));

        // then
        assertThat(result.countTranslations(), equalTo(4L));
        assertThat(result.getTranslation(KEY_1, Locale.CANADA), equalTo(VALUE_1_CA));
        assertThat(result.getTranslation(KEY_1, Locale.ENGLISH), equalTo(VALUE_1_EN));
        assertThat(result.getTranslation(KEY_2, Locale.CANADA), equalTo(VALUE_2_CA));
        assertThat(result.getTranslation(KEY_2, Locale.ENGLISH), equalTo(VALUE_2_EN));
        assertThat(result.getTranslation(KEY_1, Locale.GERMAN), equalTo(KEY_1));
        assertThat(result.getTranslation(KEY_3, Locale.ENGLISH), equalTo(KEY_3));
    }
}