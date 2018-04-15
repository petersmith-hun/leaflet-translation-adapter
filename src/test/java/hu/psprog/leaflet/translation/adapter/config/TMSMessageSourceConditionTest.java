package hu.psprog.leaflet.translation.adapter.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link TMSMessageSourceCondition}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TMSMessageSourceConditionTest {

    private static final String TMS_ENABLED_PROPERTY = "tms.enabled";

    @Mock
    private ConditionContext conditionContext;

    @Mock
    private Environment environment;

    @Mock
    private AnnotatedTypeMetadata annotatedTypeMetadata;

    @InjectMocks
    private TMSMessageSourceCondition tmsMessageSourceCondition;

    @Test
    public void shouldMatch() {

        // given
        given(conditionContext.getEnvironment()).willReturn(environment);
        given(environment.getProperty(TMS_ENABLED_PROPERTY, Boolean.class)).willReturn(true);

        // when
        boolean result = tmsMessageSourceCondition.matches(conditionContext, annotatedTypeMetadata);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void shouldNotMatch() {

        // given
        given(conditionContext.getEnvironment()).willReturn(environment);
        given(environment.getProperty(TMS_ENABLED_PROPERTY, Boolean.class)).willReturn(false);

        // when
        boolean result = tmsMessageSourceCondition.matches(conditionContext, annotatedTypeMetadata);

        // then
        assertThat(result, is(false));
    }
}