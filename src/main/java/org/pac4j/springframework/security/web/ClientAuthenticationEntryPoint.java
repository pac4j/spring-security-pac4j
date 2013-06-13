/*
  Copyright 2012 - 2013 Jerome Leleu

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
import javax.servlet.http.HttpSession;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * This entry point redirects the user to the provider.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ClientAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientAuthenticationEntryPoint.class);
    
    private Client<Credentials, UserProfile> client;
    
    @SuppressWarnings("rawtypes")
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {
        final HttpSession session = request.getSession(true);
        
        logger.debug("client : {}", this.client);
        
        // no profile -> has this authentication already be attempted ?
        final String triedAuth = (String) session
            .getAttribute(this.client.getName() + ClientAuthenticationFilter.ATTEMPTED_AUTHENTICATION_SUFFIX);
        logger.debug("triedAuth : {}", triedAuth);
        if (CommonHelper.isNotBlank(triedAuth)) {
            session.setAttribute(this.client.getName() + ClientAuthenticationFilter.ATTEMPTED_AUTHENTICATION_SUFFIX,
                                 null);
            response.sendError(403);
        } else {
            final WebContext context = new J2EContext(request, response);
            final BaseClient baseClient = (BaseClient) this.client;
            final String redirectionUrl = baseClient.getRedirectionUrl(context, true);
            logger.debug("redirectionUrl : {}", redirectionUrl);
            response.sendRedirect(redirectionUrl);
        }
    }
    
    public void afterPropertiesSet() throws Exception {
        CommonHelper.assertNotNull("client", this.client);
    }
    
    public Client<Credentials, UserProfile> getClient() {
        return this.client;
    }
    
    public void setClient(final Client<Credentials, UserProfile> client) {
        this.client = client;
    }
}
