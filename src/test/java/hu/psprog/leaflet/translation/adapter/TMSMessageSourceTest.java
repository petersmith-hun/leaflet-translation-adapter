package hu.psprog.leaflet.translation.adapter;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.translation.adapter.conversion.TranslationPackSetToTranslationsConverter;
import hu.psprog.leaflet.translation.adapter.domain.Translations;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.client.MessageSourceClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link TMSMessageSource}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TMSMessageSourceTest {

    private static final String PACK_NAME = "pack1";
    private static final List<String> PACKS = Collections.singletonList(PACK_NAME);
    private static final TranslationPack TRANSLATION_PACK = TranslationPack.getPackBuilder()
            .withPackName(PACK_NAME)
            .withLocale(Locale.ENGLISH)
            .withCreated(new Date())
            .build();
    private static final Translations TRANSLATIONS = new Translations();

    static {
        TRANSLATIONS.addTranslation("key.1", Locale.GERMAN, "value-1-de");
        TRANSLATIONS.addTranslation("key.1", Locale.ENGLISH, "value-1-en");
    }

    @Mock
    private TranslationPackSetToTranslationsConverter translationsConverter;

    @Mock
    private MessageSourceClient messageSourceClient;

    private TMSMessageSource tmsMessageSource;

    @Before
    public void setup() throws CommunicationFailureException {
        given(messageSourceClient.retrievePacks(PACKS)).willReturn(Collections.singleton(TRANSLATION_PACK));
        given(translationsConverter.convert(Collections.singleton(TRANSLATION_PACK))).willReturn(TRANSLATIONS);
    }

    @Test
    public void shouldInitMessageSource() throws CommunicationFailureException {

        // given
        tmsMessageSource = new TMSMessageSource(translationsConverter, messageSourceClient, PACKS, Locale.ENGLISH);

        // when
        tmsMessageSource.initMessageSource();

        // then
        assertThat(extractTranslationsField(), equalTo(TRANSLATIONS));
    }

    @Test
    public void shouldResolveCodeWithoutForcedLocale() throws CommunicationFailureException {

        // given
        tmsMessageSource = new TMSMessageSource(translationsConverter, messageSourceClient, PACKS, null);
        tmsMessageSource.initMessageSource();

        // when
        MessageFormat result = tmsMessageSource.resolveCode("key.1", Locale.GERMAN);

        // then
        assertThat(result, notNullValue());
        assertThat(result.toPattern(), equalTo("value-1-de"));
    }

    @Test
    public void shouldResolveCodeWithForcedLocale() throws CommunicationFailureException {

        // given
        tmsMessageSource = new TMSMessageSource(translationsConverter, messageSourceClient, PACKS, Locale.ENGLISH);
        tmsMessageSource.initMessageSource();

        // when
        MessageFormat result = tmsMessageSource.resolveCode("key.1", Locale.GERMAN);

        // then
        assertThat(result, notNullValue());
        assertThat(result.toPattern(), equalTo("value-1-en"));
    }

    @Test
    public void shouldResolveCodeWithNonExistingKey() throws CommunicationFailureException {

        // given
        tmsMessageSource = new TMSMessageSource(translationsConverter, messageSourceClient, PACKS, null);
        tmsMessageSource.initMessageSource();

        // when
        MessageFormat result = tmsMessageSource.resolveCode("key.non.existing", Locale.ENGLISH);

        // then
        assertThat(result, notNullValue());
        assertThat(result.toPattern(), equalTo("key.non.existing"));
    }

    private Translations extractTranslationsField() {

        Field translationsField = ReflectionUtils.findField(TMSMessageSource.class, "translations");
        translationsField.setAccessible(true);

        return (Translations) ReflectionUtils.getField(translationsField, tmsMessageSource);
    }
}