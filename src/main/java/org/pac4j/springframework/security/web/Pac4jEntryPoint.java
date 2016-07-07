package org.pac4j.springframework.security.web;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This entry point redirects the user to the identity provider for login (indirect client) or return a 401 error page.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class Pac4jEntryPoint extends DefaultSecurityLogic<Object, J2EContext> implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(Pac4jEntryPoint.class);

    private Config config;

    private String client;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {
        logger.debug("config: {}", this.config);
        logger.debug("client: {}", this.client);

        final List<Client> clients = new ArrayList<>();
        if (config != null) {
            CommonHelper.assertNotBlank("client", this.client);
            final Client c = config.getClients().findClient(client);
            clients.add(c);
        }
        final J2EContext context = new J2EContext(request, response);

        try {
            handleNoProfile(context, clients);
        } catch (final HttpAction action) {
            logger.info("Extra http action required in entry point", action);
        }
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
