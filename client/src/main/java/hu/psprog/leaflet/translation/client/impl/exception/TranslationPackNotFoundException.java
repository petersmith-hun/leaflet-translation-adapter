package hu.psprog.leaflet.translation.client.impl.exception;

/**
 * Exception to be thrown a requested translation pack does not exist.
 *
 * @author Peter Smith
 */
public class TranslationPackNotFoundException extends RuntimeException {

    public TranslationPackNotFoundException(String message) {
        super(message);
    }
}
