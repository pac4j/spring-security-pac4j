/*
  Copyright 2012 Jerome Leleu

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
package org.leleuj.ss.oauth.client.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.session.HttpUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

/**
 * This entry point redirects the user to the authorization url of the OAuth provider.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationEntryPoint.class);
    
    private OAuthProvider provider = null;
    
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException, ServletException {
        String authorizationUrl = provider.getAuthorizationUrl(new HttpUserSession(request));
        logger.debug("authorizationUrl : {}", authorizationUrl);
        
        response.sendRedirect(authorizationUrl);
    }
    
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(provider, "provider cannot be null");
    }
    
    public void setProvider(OAuthProvider provider) {
        this.provider = provider;
    }
}
