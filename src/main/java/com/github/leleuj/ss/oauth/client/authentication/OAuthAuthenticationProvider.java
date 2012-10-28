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
package com.github.leleuj.ss.oauth.client.authentication;

import java.util.ArrayList;
import java.util.Collection;

import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.ProvidersDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * This provider authenticates OAuth credentials stored in (
 * {@link com.github.leleuj.ss.oauth.client.authentication.OAuthAuthenticationToken} ) to get the user profile and finally the user details
 * (and authorities).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationProvider.class);
    
    private ProvidersDefinition providersDefinition = null;
    
    private AuthenticationUserDetailsService<OAuthAuthenticationToken> oauthUserDetailsService = null;
    
    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        logger.debug("authentication : {}", authentication);
        if (!supports(authentication.getClass())) {
            logger.debug("unsupported authentication class : {}", authentication.getClass());
            return null;
        }
        
        // get the OAuth credentials
        final OAuthCredential credential = (OAuthCredential) authentication.getCredentials();
        logger.debug("credential : {}", credential);
        if (credential == null) {
            logger.debug("authentication failed");
            return null;
        }
        
        // get the right provider
        final String providerType = credential.getProviderType();
        final OAuthProvider provider = providersDefinition.findProvider(providerType);
        if (provider == null) {
            logger.debug("cannot find the right provider for authentication / required provider type : {}",
                         providerType);
            return null;
        }
        
        // get the user profile
        final UserProfile userProfile = provider.getUserProfile(credential);
        logger.debug("userProfile : {}", userProfile);
        
        // by default, no authorities
        Collection<? extends GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // get user details and check them
        if (this.oauthUserDetailsService != null) {
            final OAuthAuthenticationToken tmpToken = new OAuthAuthenticationToken(credential, userProfile, null);
            final UserDetails userDetails = this.oauthUserDetailsService.loadUserDetails(tmpToken);
            logger.debug("userDetails : {}", userDetails);
            this.userDetailsChecker.check(userDetails);
            authorities = userDetails.getAuthorities();
            logger.debug("authorities : {}", authorities);
        }
        
        // new token with credential (like previously) and user profile and
        // authorities
        final OAuthAuthenticationToken result = new OAuthAuthenticationToken(credential, userProfile, authorities);
        result.setDetails(authentication.getDetails());
        logger.debug("result : {}", result);
        return result;
    }
    
    public boolean supports(final Class<?> authentication) {
        return (OAuthAuthenticationToken.class.isAssignableFrom(authentication));
    }
    
    public void afterPropertiesSet() {
        Assert.notNull(this.providersDefinition, "provider cannot be null");
    }
    
    public void setProvider(final OAuthProvider provider) {
        this.providersDefinition = new ProvidersDefinition(provider);
    }
    
    public void setProvidersDefinition(final ProvidersDefinition providersDefinition) {
        this.providersDefinition = providersDefinition;
    }
    
    public void setOauthUserDetailsService(final AuthenticationUserDetailsService<OAuthAuthenticationToken> oauthUserDetailsService) {
        this.oauthUserDetailsService = oauthUserDetailsService;
    }
    
    public void setUserDetailsChecker(final UserDetailsChecker userDetailsChecker) {
        this.userDetailsChecker = userDetailsChecker;
    }
}
