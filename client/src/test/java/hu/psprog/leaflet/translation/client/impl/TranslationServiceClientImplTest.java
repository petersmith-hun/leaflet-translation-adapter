package hu.psprog.leaflet.translation.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.api.domain.TranslationPackCreationRequest;
import hu.psprog.leaflet.translation.api.domain.TranslationPackMetaInfo;
import hu.psprog.leaflet.translation.client.TranslationServiceClient;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackCreationException;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackNotFoundException;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackValidationException;
import hu.psprog.leaflet.translation.client.impl.exception.UnknownTMSException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link TranslationServiceClientImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TranslationServiceClientImplTest.TranslationServiceClientTestConfiguration.class)
public class TranslationServiceClientImplTest {

    private static final UUID PACK_ID = UUID.randomUUID();
    private static final String PACK_NAME = "pack1";
    private static final TranslationPackCreationRequest TRANSLATION_PACK_CREATION_REQUEST = new TranslationPackCreationRequest();
    private static final TranslationPack TRANSLATION_PACK = TranslationPack.getPackBuilder()
            .withId(PACK_ID)
            .withPackName(PACK_NAME)
            .build();
    private static final TranslationPackMetaInfo TRANSLATION_PACK_META_INFO = TranslationPackMetaInfo.getMetaInfoBuilder()
            .withId(PACK_ID)
            .withPackName(PACK_NAME)
            .build();
    private static final EqualToPattern PACKS_QUERY_PARAMETER_VALUE_PATTERN = new EqualToPattern(PACK_NAME);
    private static final String QUERY_PARAMETER_PACKS = "packs";
    private static final String PATH_TRANSLATIONS = "/translations";
    private static final String PATH_TRANSLATIONS_ID = "/translations/" + PACK_ID;
    private static final String PATH_TRANSLATIONS_STATUS = "/translations/" + PACK_ID + "/status";
    private static final Map<String, String> ERROR_MESSAGE_BODY = prepareErrorMessage();
    private static final Map<String, Object> VALIDATION_ERROR_MESSAGE_BODY = prepareValidationErrorMessage();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";
    private static final int HTTP_STATUS_CREATED = 201;
    private static final int HTTP_STATUS_NO_CONTENT = 204;
    private static final int HTTP_STATUS_BAD_REQUEST = 400;
    private static final int HTTP_STATUS_NOT_FOUND = 404;
    private static final int HTTP_STATUS_UNPROCESSABLE_ENTITY = 422;
    private static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;

    static {
        TRANSLATION_PACK_CREATION_REQUEST.setPackName(PACK_NAME);
        TRANSLATION_PACK_CREATION_REQUEST.setLocale(Locale.ENGLISH);
        TRANSLATION_PACK_CREATION_REQUEST.setDefinitions(prepareErrorMessage());
    }

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(options().port(9999));

    @Rule
    public WireMockClassRule wireMockInstanceRule = wireMockRule;

    @Autowired
    private TranslationServiceClient translationServiceClient;

    @Test
    public void shouldRetrievePacks() {

        // given
        givenThat(get(urlPathEqualTo(PATH_TRANSLATIONS))
                .withQueryParam(QUERY_PARAMETER_PACKS, PACKS_QUERY_PARAMETER_VALUE_PATTERN)
                .willReturn(ResponseDefinitionBuilder.okForJson(Collections.singleton(TRANSLATION_PACK))));

        // when
        Set<TranslationPack> result = translationServiceClient.retrievePacks(Collections.singletonList(PACK_NAME));

        // then
        verify(getRequestedFor(urlPathEqualTo(PATH_TRANSLATIONS))
                .withQueryParam(QUERY_PARAMETER_PACKS, PACKS_QUERY_PARAMETER_VALUE_PATTERN));
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.contains(TRANSLATION_PACK), is(true));
    }

    @Test
    public void shouldListStoredPacks() {

        // given
        givenThat(get(PATH_TRANSLATIONS)
                .willReturn(ResponseDefinitionBuilder.okForJson(Collections.singletonList(TRANSLATION_PACK_META_INFO))));

        // when
        List<TranslationPackMetaInfo> result = translationServiceClient.listStoredPacks();

        // then
        verify(getRequestedFor(urlPathEqualTo(PATH_TRANSLATIONS)));
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.contains(TRANSLATION_PACK_META_INFO), is(true));
    }

    @Test
    public void shouldGetPackByID() {

        // given
        givenThat(get(PATH_TRANSLATIONS_ID)
                .willReturn(ResponseDefinitionBuilder.okForJson(TRANSLATION_PACK)));

        // when
        TranslationPack result = translationServiceClient.getPackByID(PACK_ID);

        // then
        verify(getRequestedFor(urlPathEqualTo(PATH_TRANSLATIONS_ID)));
        assertThat(result, notNullValue());
        assertThat(result, equalTo(TRANSLATION_PACK));
    }

    @Test(expected = TranslationPackNotFoundException.class)
    public void shouldGetPackByIDThrowException() throws JsonProcessingException {

        // given
        givenThat(get(PATH_TRANSLATIONS_ID)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_NOT_FOUND)
                        .withBody(OBJECT_MAPPER.writeValueAsString(ERROR_MESSAGE_BODY))));

        // when
        translationServiceClient.getPackByID(PACK_ID);

        // then
        // exception expected
    }

    @Test
    public void shouldCreateTranslationPack() throws JsonProcessingException {

        // given
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(TRANSLATION_PACK_CREATION_REQUEST));
        givenThat(post(PATH_TRANSLATIONS)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_CREATED)
                        .withBody(OBJECT_MAPPER.writeValueAsString(TRANSLATION_PACK))));

        // when
        TranslationPack result = translationServiceClient.createTranslationPack(TRANSLATION_PACK_CREATION_REQUEST);

        // then
        verify(postRequestedFor(urlPathEqualTo(PATH_TRANSLATIONS)));
        assertThat(result, equalTo(TRANSLATION_PACK));
    }

    @Test
    public void shouldCreateTranslationPackWithValidationError() throws JsonProcessingException {

        // given
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(TRANSLATION_PACK_CREATION_REQUEST));
        givenThat(post(PATH_TRANSLATIONS)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_BAD_REQUEST)
                        .withBody(OBJECT_MAPPER.writeValueAsString(VALIDATION_ERROR_MESSAGE_BODY))));

        // when
        try {
            translationServiceClient.createTranslationPack(TRANSLATION_PACK_CREATION_REQUEST);
            fail("Test case should have thrown exception");
        } catch (TranslationPackValidationException exc) {

            // then
            // exception expected
            assertThat(exc.getValidation(), notNullValue());
            assertThat(exc.getValidation(), equalTo(VALIDATION_ERROR_MESSAGE_BODY.get("validation")));
        }
    }

    @Test(expected = TranslationPackCreationException.class)
    public void shouldCreateTranslationPackWithCreationError() throws JsonProcessingException {

        // given
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(TRANSLATION_PACK_CREATION_REQUEST));
        givenThat(post(PATH_TRANSLATIONS)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_UNPROCESSABLE_ENTITY)
                        .withBody(OBJECT_MAPPER.writeValueAsString(ERROR_MESSAGE_BODY))));

        // when
        translationServiceClient.createTranslationPack(TRANSLATION_PACK_CREATION_REQUEST);

        // then
        // exception expected
    }

    @Test(expected = UnknownTMSException.class)
    public void shouldCreateTranslationPackWithUnknownError() throws JsonProcessingException {

        // given
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(TRANSLATION_PACK_CREATION_REQUEST));
        givenThat(post(PATH_TRANSLATIONS)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_INTERNAL_SERVER_ERROR)
                        .withBody(OBJECT_MAPPER.writeValueAsString(ERROR_MESSAGE_BODY))));

        // when
        translationServiceClient.createTranslationPack(TRANSLATION_PACK_CREATION_REQUEST);

        // then
        // exception expected
    }

    @Test
    public void shouldChangePackStatus() throws JsonProcessingException {

        // given
        givenThat(put(PATH_TRANSLATIONS_STATUS)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_CREATED)
                        .withBody(OBJECT_MAPPER.writeValueAsString(TRANSLATION_PACK))));

        // when
        TranslationPack result = translationServiceClient.changePackStatus(PACK_ID);

        // then
        verify(putRequestedFor(urlPathEqualTo(PATH_TRANSLATIONS_STATUS)));
        assertThat(result, equalTo(TRANSLATION_PACK));
    }

    @Test
    public void shouldDeletePack() {

        // given
        givenThat(delete(PATH_TRANSLATIONS_ID)
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                        .withStatus(HTTP_STATUS_NO_CONTENT)));

        // when
        translationServiceClient.deleteTranslationPack(PACK_ID);

        // then
        verify(deleteRequestedFor(urlPathEqualTo(PATH_TRANSLATIONS_ID)));
    }

    private static Map<String, String> prepareErrorMessage() {

        Map<String, String> content = new HashMap<>();
        content.put("message", "error occurred");

        return content;
    }

    private static Map<String, Object> prepareValidationErrorMessage() {

        Map<String, String> validation = new HashMap<>();
        validation.put("field1", "violation message");

        Map<String, Object> content = new HashMap<>();
        content.put("message", "validation error occurred");
        content.put("validation", validation);

        return content;
    }

    @Configuration
    public static class TranslationServiceClientTestConfiguration {

        @Bean
        public TranslationServiceClient translationServiceClient() {
            return new TranslationServiceClientImpl("http://localhost:9999/translations");
        }
    }
}