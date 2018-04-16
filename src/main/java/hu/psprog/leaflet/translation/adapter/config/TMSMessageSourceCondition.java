package hu.psprog.leaflet.translation.adapter.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Translation Management Service based message source configuration usage condition.
 * Message source will be configured if {@code tms.enabled} parameter exists and is true.
 *
 * @author Peter Smith
 */
public class TMSMessageSourceCondition implements Condition {

    private static final String TMS_ENABLED = "tms.enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(TMS_ENABLED, Boolean.class);
    }
}
