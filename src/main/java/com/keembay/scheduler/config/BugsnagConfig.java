package com.keembay.scheduler.config;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BugsnagSpringConfiguration.class)
@Slf4j
public class BugsnagConfig {

    @Value("${bugsnag.token}")
    private String bugsnagToken;

    @Bean
    public Bugsnag bugsnag() {

        if (bugsnagToken == null) {
            log.error("There is not BUGSNAG_API_KEY configured on the system");
        }

        Bugsnag  bugsnag = new Bugsnag(bugsnagToken);
        return bugsnag;
    }
}