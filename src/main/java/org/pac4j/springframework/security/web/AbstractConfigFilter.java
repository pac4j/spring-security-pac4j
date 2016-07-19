package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.springframework.security.store.SpringSecuritySessionStore;

import javax.servlet.*;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Abstract filter containing the configuration and computing the session store.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractConfigFilter implements Filter {

    private Config config;

    protected SessionStore<J2EContext> retrieveSessionStore() {
        assertNotNull("config", config);
        SessionStore<J2EContext> sessionStore = config.getSessionStore();
        if (sessionStore == null) {
            sessionStore = SpringSecuritySessionStore.INSTANCE;
        }
        return sessionStore;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }
}
