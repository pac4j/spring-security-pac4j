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
 * This provider authenticates OAuth credentials stored in ({@link com.github.leleuj.ss.oauth.client.authentication.OAuthAuthenticationToken})
 * to get the user profile and finally the user details (and authorities).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthAuthenticationProvider implements AuthenticationProvider,
    InitializingBean {
    
    private static final Logger logger = LoggerFactory
        .getLogger(OAuthAuthenticationProvider.class);
    
    private OAuthProvider provider = null;
    
    private AuthenticationUserDetailsService<OAuthAuthenticationToken> oauthUserDetailsService = null;
    
    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        logger.debug("authentication : {}", authentication);
        if (!supports(authentication.getClass())) {
            logger.debug("unsupported authentication class : {}", authentication.getClass());
            return null;
        }
        
        // get the OAuth credentials
        OAuthCredential credential = (OAuthCredential) authentication.getCredentials();
        logger.debug("credential : {}", credential);
        
        // check it is the right provider for the right credential
        if (!provider.getType().equals(credential.getProviderType())) {
            logger.debug("unsupported provider type, expected : {} / returned : {}", provider.getType(), credential.getProviderType());
            return null;
        }
        
        // get the user profile
        UserProfile userProfile = provider.getUserProfile(credential);
        logger.debug("userProfile : {}", userProfile);
        
        // by default, no authorities
        Collection<? extends GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // get user details and check them
        if (oauthUserDetailsService != null) {
            OAuthAuthenticationToken tmpToken = new OAuthAuthenticationToken(credential, userProfile, null);
            UserDetails userDetails = oauthUserDetailsService.loadUserDetails(tmpToken);
            logger.debug("userDetails : {}", userDetails);
            userDetailsChecker.check(userDetails);
            authorities = userDetails.getAuthorities();
            logger.debug("authorities : {}", authorities);
        }
        
        // new token with credential (like previously) and user profile and authorities
        OAuthAuthenticationToken result = new OAuthAuthenticationToken(credential, userProfile, authorities);
        result.setDetails(authentication.getDetails());
        logger.debug("result : {}", result);
        return result;
    }
    
    public boolean supports(Class<?> authentication) {
        return (OAuthAuthenticationToken.class.isAssignableFrom(authentication));
    }
    
    public void afterPropertiesSet() {
        Assert.notNull(provider, "provider cannot be null");
    }
    
    public void setProvider(OAuthProvider provider) {
        this.provider = provider;
    }
    
    public void setOauthUserDetailsService(AuthenticationUserDetailsService<OAuthAuthenticationToken> oauthUserDetailsService) {
        this.oauthUserDetailsService = oauthUserDetailsService;
    }
    
    public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
        this.userDetailsChecker = userDetailsChecker;
    }
}
