package hu.psprog.leaflet.translation.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/**
 * TMS error response model.
 *
 * @author Peter Smith
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TMSErrorResponse {

    private String message;
    private Map<String, String> validation;

    public String getMessage() {
        return message;
    }

    public Map<String, String> getValidation() {
        return validation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TMSErrorResponse that = (TMSErrorResponse) o;

        return new EqualsBuilder()
                .append(message, that.message)
                .append(validation, that.validation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(message)
                .append(validation)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("message", message)
                .append("validation", validation)
                .toString();
    }

    public static TMSErrorResponseBuilder getBuilder() {
        return new TMSErrorResponseBuilder();
    }

    /**
     * Builder for {@link TMSErrorResponse}.
     */
    public static final class TMSErrorResponseBuilder {
        private String message;
        private Map<String, String> validation;

        private TMSErrorResponseBuilder() {
        }

        public TMSErrorResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public TMSErrorResponseBuilder withValidation(Map<String, String> validation) {
            this.validation = validation;
            return this;
        }

        public TMSErrorResponse build() {
            TMSErrorResponse tMSErrorResponse = new TMSErrorResponse();
            tMSErrorResponse.message = this.message;
            tMSErrorResponse.validation = this.validation;
            return tMSErrorResponse;
        }
    }
}
