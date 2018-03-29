package hu.psprog.leaflet.translation.client.impl.exception;

import java.util.Map;

/**
 * Exception to be thrown when translation pack validation fails upon creation.
 *
 * @author Peter Smith
 */
public class TranslationPackValidationException extends RuntimeException {

    private Map<String, String> validation;

    public TranslationPackValidationException(Map<String, String> validation) {
        super("Validation failed upon translation pack creation");
        this.validation = validation;
    }

    public Map<String, String> getValidation() {
        return validation;
    }
}
