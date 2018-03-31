package hu.psprog.leaflet.translation.client.impl.exception;

/**
 * Exception to be thrown upon TMS responding with HTTP 500.
 *
 * @author Peter Smith
 */
public class UnknownTMSException extends RuntimeException {

    public UnknownTMSException(String message) {
        super(message);
    }
}
