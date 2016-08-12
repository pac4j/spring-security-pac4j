package org.pac4j.springframework.security.web;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.springframework.security.context.SpringSecurityContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This entry point redirects the user to the identity provider for login if the configured client is an indirect one
 * or returns a 401 error page otherwise.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class Pac4jEntryPoint extends DefaultSecurityLogic<Object, J2EContext> implements AuthenticationEntryPoint {

    private Config config;

    private String clientName;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {

        SessionStore<J2EContext> sessionStore = null;
        if (config != null) {
            sessionStore = config.getSessionStore();
        }
        final SpringSecurityContext context = new SpringSecurityContext(request, response, sessionStore);

        final List<Client> currentClients = new ArrayList<>();
        if (config != null && CommonHelper.isNotBlank(clientName)) {

            final Client client = config.getClients().findClient(clientName);
            currentClients.add(client);
            if (startAuthentication(context, currentClients)) {
                logger.debug("Redirecting to identity provider for login");
                try {
                    saveRequestedUrl(context, currentClients);
                    redirectToIdentityProvider(context, currentClients);
                    return;
                } catch (final HttpAction e) {
                    logger.debug("extra HTTP action required in Pac4jEntryPoint: {}", e.getCode());
                }
            }
        }

        unauthorized(context, currentClients);
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "config", config, "clientName", clientName);
    }
}
