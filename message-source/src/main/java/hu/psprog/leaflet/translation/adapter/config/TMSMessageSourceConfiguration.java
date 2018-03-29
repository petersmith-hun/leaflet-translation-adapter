package hu.psprog.leaflet.translation.adapter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * TMS based message source configuration.
 *
 * @author Peter Smith
 */
@Configuration
@Conditional(TMSMessageSourceCondition.class)
@ComponentScan("hu.psprog.leaflet.translation")
public class TMSMessageSourceConfiguration {
}
