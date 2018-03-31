package hu.psprog.leaflet.translation.client.impl.exception;

/**
 * Exception to be thrown when a translation pack could not be created.
 *
 * @author Peter Smith
 */
public class TranslationPackCreationException extends RuntimeException {

    public TranslationPackCreationException(String message) {
        super(message);
    }
}
