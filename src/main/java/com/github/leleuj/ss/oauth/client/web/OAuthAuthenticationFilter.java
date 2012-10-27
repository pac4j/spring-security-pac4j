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
package com.github.leleuj.ss.oauth.client.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.session.HttpUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import com.github.leleuj.ss.oauth.client.authentication.OAuthAuthenticationToken;

/**
 * This filter handles OAuth calls after authentication at the OAuth provider. It listens HTTP requests on /j_spring_oauth_security_check by
 * default or whatever suffix url you can define by constructor. An
 * {@link com.github.leleuj.ss.oauth.client.authentication.OAuthAuthenticationToken} is created to finish the OAuth authentication process.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationFilter.class);
    
    private OAuthProvider provider = null;
    
    /**
     * Define the suffix url on which the filter will listen for HTTP requests. It can be used for multiple providers configuration :
     * /j_spring_facebook_security_check, /j_spring_twitter_security_check...
     * 
     * @param suffixUrl
     */
    public OAuthAuthenticationFilter(final String suffixUrl) {
        super(suffixUrl);
    }
    
    protected OAuthAuthenticationFilter() {
        super("/j_spring_oauth_security_check");
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(this.provider, "provider cannot be null");
        ProfileHelper.setKeepRawData(false);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {
        
        // create OAuth credentials from request
        final OAuthCredential credential = this.provider.getCredential(new HttpUserSession(request),
                                                                       request.getParameterMap());
        logger.debug("credential : {}", credential);
        
        // and token from credential
        final OAuthAuthenticationToken token = new OAuthAuthenticationToken(credential);
        // set details
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        logger.debug("token : {}", token);
        
        // authenticate
        final Authentication authentication = getAuthenticationManager().authenticate(token);
        logger.debug("authentication : {}", authentication);
        return authentication;
    }
    
    public void setProvider(final OAuthProvider provider) {
        this.provider = provider;
    }
}