package org.pac4j.springframework.security.config;

import org.pac4j.core.config.Config;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configure the bridge from pac4j to Spring Security (reactive).
 *
 * @author Jerome LELEU
 * @since 8.0.0
 */
@Configuration
public class Pac4jSpringSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pac4jSpringSecurityConfig.class);

    @Autowired
    private Config config;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Initializing pac4j to Spring Security bridge...");
        config.defaultProfileManagerFactory((ctx, session) -> new SpringSecurityProfileManager(ctx, session));
    }
}
