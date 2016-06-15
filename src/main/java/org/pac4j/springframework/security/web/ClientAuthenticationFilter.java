/*
  Copyright 2012 - 2015 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.springframework.security.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.pac4j.springframework.security.exception.AuthenticationCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * This filter handles callbacks after authentication from the provider. It listens HTTP requests on /callback by
 * default or any suffix url you can define by constructor. An {@link org.pac4j.springframework.security.authentication.ClientAuthenticationToken}
 * is created to finish the authentication process.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ClientAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthenticationFilter.class);

    private Clients clients;

    /**
     * Define the suffix url on which the filter will listen for HTTP requests.
     *
     * @param suffixUrl the suffix url
     */
    public ClientAuthenticationFilter(final String suffixUrl) {
        super(suffixUrl);
    }

    protected ClientAuthenticationFilter() {
        super("/callback");
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        CommonHelper.assertNotNull("clients", this.clients);
        this.clients.init();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        // context
        final WebContext context = new J2EContext(request, response);

        // get the right client
        final Client client = this.clients.findClient(context);

        // get credentials
        Credentials credentials;
        try {
            credentials = client.getCredentials(context);
        } catch (final HttpAction e) {
            logger.info("Requires additionnal HTTP action", e);
            return null;
        } catch (CredentialsException ce) {
            throw new AuthenticationCredentialsException("Error retrieving credentials", ce);
        }

        logger.debug("credentials : {}", credentials);
        // if credentials/profile is null, return to the saved request url
        if (credentials == null) {
            context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, "");
            context.setSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "");
            getFailureHandler().onAuthenticationFailure(request, response, new AuthenticationCredentialsException("No credentials"));
            return null;
        }
        // and create token from credential
        final ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, client.getName(), context);
        // set details
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        logger.debug("token: {}", token);

        // authenticate
        final Authentication authentication = getAuthenticationManager().authenticate(token);
        logger.debug("authentication: {}", authentication);
        return authentication;
    }

    public Clients getClients() {
        return this.clients;
    }

    public void setClients(final Clients clients) {
        this.clients = clients;
    }
}
