package hu.psprog.leaflet.translation.client.impl;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hu.psprog.leaflet.translation.api.domain.TMSErrorResponse;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.api.domain.TranslationPackCreationRequest;
import hu.psprog.leaflet.translation.api.domain.TranslationPackMetaInfo;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackCreationException;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackNotFoundException;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackValidationException;
import hu.psprog.leaflet.translation.client.impl.exception.UnknownTMSException;
import hu.psprog.leaflet.translation.client.TranslationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of {@link TranslationServiceClient}.
 *
 * @author Peter Smith
 */
@Service
public class TranslationServiceClientImpl implements TranslationServiceClient {

    private static final String PATH_PACK_ID = "/{packID}";
    private static final String PATH_STATUS = PATH_PACK_ID + "/status";
    private static final String PARAMETER_PACK_ID = "packID";
    private static final String PARAMETER_PACKS = "packs";

    private WebTarget webTarget;
    private String translationServiceUrl;

    @Autowired
    public TranslationServiceClientImpl(@Value("${tms.url}") String translationServiceUrl) {
        this.translationServiceUrl = translationServiceUrl;
    }

    @PostConstruct
    public void setup() {
        webTarget = ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .build()
                .target(translationServiceUrl);
    }

    @Override
    public Set<TranslationPack> retrievePacks(List<String> packs) {

        Response response = webTarget
                .queryParam(PARAMETER_PACKS, packs.toArray())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return readResponse(response, new GenericType<Set<TranslationPack>>(){});
    }

    @Override
    public List<TranslationPackMetaInfo> listStoredPacks() {

        Response response = webTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return readResponse(response, new GenericType<List<TranslationPackMetaInfo>>(){});
    }

    @Override
    public TranslationPack getPackByID(UUID packID) {

        Response response = webTarget
                .path(PATH_PACK_ID)
                .resolveTemplate(PARAMETER_PACK_ID, packID)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return readResponse(response, TranslationPack.class);
    }

    @Override
    public TranslationPack createTranslationPack(TranslationPackCreationRequest translationPackCreationRequest) {

        Response response = webTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(translationPackCreationRequest, MediaType.APPLICATION_JSON_TYPE));

        return readResponse(response, TranslationPack.class);
    }

    @Override
    public TranslationPack changePackStatus(UUID packID) {

        Response response = webTarget
                .path(PATH_STATUS)
                .resolveTemplate(PARAMETER_PACK_ID, packID)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(null);

        return readResponse(response, TranslationPack.class);
    }

    @Override
    public void deleteTranslationPack(UUID packID) {

        Response response = webTarget
                .path(PATH_PACK_ID)
                .resolveTemplate(PARAMETER_PACK_ID, packID)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        checkResponseStatus(response);
    }

    private <T> T readResponse(Response response, Class<T> asClass) {
        return readResponse(response, new GenericType<>(asClass));
    }

    private <T> T readResponse(Response response, GenericType<T> asType) {
        checkResponseStatus(response);
        return response.readEntity(asType);
    }

    private void checkResponseStatus(Response response) {

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {

            TMSErrorResponse errorResponse = response.readEntity(TMSErrorResponse.class);
            if (response.getStatusInfo() == Response.Status.NOT_FOUND) {
                throw new TranslationPackNotFoundException(errorResponse.getMessage());
            } else if (response.getStatusInfo() == Response.Status.CONFLICT) {
                throw new TranslationPackCreationException(errorResponse.getMessage());
            } else if (response.getStatusInfo() == Response.Status.BAD_REQUEST) {
                throw new TranslationPackValidationException(errorResponse.getValidation());
            } else {
                throw new UnknownTMSException(errorResponse.getMessage());
            }
        }
    }
}
