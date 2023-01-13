package org.pac4j.springframework.security.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jee.context.JEEFrameworkParameters;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This entry point can be defined with a security configuration and a client name:
 * if it's an indirect client, it redirects the user to te identity provider for login. Otherwise, a 401 error page is returned.
 * If no configuration is provided, an error is returned directly.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
@Getter
@Setter
@Slf4j
@ToString
public class Pac4jEntryPoint extends DefaultSecurityLogic implements AuthenticationEntryPoint {

    private Config config;

    private String clientName;

    public Pac4jEntryPoint() {}

    public Pac4jEntryPoint(final Config config, final String clientName) {
        this.config = config;
        this.clientName = clientName;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {

        if (config != null && CommonHelper.isNotBlank(clientName)) {

            FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

            val parameters = new JEEFrameworkParameters(request, response);
            val context = config.getWebContextFactory().newContext(parameters);
            val sessionStore = config.getSessionStoreFactory().newSessionStore(parameters);
            val adapter = config.getHttpActionAdapter();

            final List<Client> currentClients = new ArrayList<>();
            final Client client = config.getClients().findClient(clientName).orElseThrow(() -> new TechnicalException("Cannot find clientName: " + clientName));
            currentClients.add(client);

            HttpAction action;
            try {
                if (startAuthentication(context, sessionStore, currentClients)) {
                    LOGGER.debug("Redirecting to identity provider for login");
                        saveRequestedUrl(context, sessionStore, currentClients, config.getClients().getAjaxRequestResolver());
                        action = redirectToIdentityProvider(context, sessionStore, config.getProfileManagerFactory(), currentClients);
                } else {
                    action = unauthorized(context, sessionStore, currentClients);
                }
            } catch (final HttpAction e) {
                LOGGER.debug("extra HTTP action required in Pac4jEntryPoint: {}", e.getCode());
                action = e;
            }
            adapter.adapt(action, context);

        } else {
            throw new TechnicalException("The Pac4jEntryPoint has been defined without config, nor clientName: it must be defined in a <security:http> section with the pac4j SecurityFilter or CallbackFilter");
        }
    }
}
