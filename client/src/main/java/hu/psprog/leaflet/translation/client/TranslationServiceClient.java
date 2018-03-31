package hu.psprog.leaflet.translation.client;

import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.api.domain.TranslationPackCreationRequest;
import hu.psprog.leaflet.translation.api.domain.TranslationPackMetaInfo;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Translation management service HTTP client interface.
 *
 * @author Peter Smith
 */
public interface TranslationServiceClient {

    /**
     * Calls TMS to retrieve latest enabled translation packs by their names.
     *
     * @param packs pack names to retrieve
     * @return available {@link TranslationPack}s as {@link Set}
     */
    Set<TranslationPack> retrievePacks(List<String> packs);

    /**
     * Calls TMS to retrieve meta information of all available translation packs.
     *
     * @return List of {@link TranslationPack} objects
     */
    List<TranslationPackMetaInfo> listStoredPacks();

    /**
     * Calls TMS to retrieve pack identified by given ID as {@link UUID}.
     *
     * @param packID ID of the pack to return
     * @return existing {@link TranslationPack} identified by given ID or exception if not found
     */
    TranslationPack getPackByID(UUID packID);

    /**
     * Calls TMS to create a new translation pack.
     *
     * @param translationPackCreationRequest translation pack data to create
     * @return created {@link TranslationPack}
     */
    TranslationPack createTranslationPack(TranslationPackCreationRequest translationPackCreationRequest);

    /**
     * Calls TMS to change status (enabled/disabled) of the translation pack identified by given ID.
     *
     * @param packID ID of the pack to change status of
     * @return modified {@link TranslationPack}
     */
    TranslationPack changePackStatus(UUID packID);

    /**
     * Calls TMS to delete the translation pack identified by given ID.
     *
     * @param packID ID of the pack to delete
     */
    void deleteTranslationPack(UUID packID);
}
