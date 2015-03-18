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
package org.pac4j.springframework.security.authentication;

import java.util.ArrayList;
import java.util.Collection;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
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

/**
 * This provider authenticates credentials stored in ( {@link ClientAuthenticationToken}) to get the user profile and finally the user
 * details (and authorities).
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ClientAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthenticationProvider.class);

    private Clients clients;

    private AuthenticationUserDetailsService<ClientAuthenticationToken> userDetailsService =
            new CopyRolesUserDetailsService();

    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        logger.debug("authentication : {}", authentication);
        if (!supports(authentication.getClass())) {
            logger.debug("unsupported authentication class : {}", authentication.getClass());
            return null;
        }
        final ClientAuthenticationToken token = (ClientAuthenticationToken) authentication;

        // get the credentials
        final Credentials credentials = (Credentials) authentication.getCredentials();
        logger.debug("credentials : {}", credentials);

        // get the right client
        final String clientName = token.getClientName();
        final Client client = this.clients.findClient(clientName);
        // get the user profile
        final UserProfile userProfile = client.getUserProfile(credentials, null);
        logger.debug("userProfile : {}", userProfile);

        // by default, no authorities
        Collection<? extends GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // get user details and check them
        UserDetails userDetails = null;
        if (this.userDetailsService != null) {
            final ClientAuthenticationToken tmpToken = new ClientAuthenticationToken(credentials, clientName,
                    userProfile, null);
            userDetails = this.userDetailsService.loadUserDetails(tmpToken);
            logger.debug("userDetails : {}", userDetails);
            if (userDetails != null) {
                this.userDetailsChecker.check(userDetails);
                authorities = userDetails.getAuthorities();
                logger.debug("authorities : {}", authorities);
            }
        }

        // new token with credentials (like previously) and user profile and
        // authorities
        final ClientAuthenticationToken result = new ClientAuthenticationToken(credentials, clientName, userProfile,
                authorities, userDetails);
        result.setDetails(authentication.getDetails());
        logger.debug("result : {}", result);
        return result;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return (ClientAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public void afterPropertiesSet() {
        CommonHelper.assertNotNull("clients", this.clients);
        this.clients.init();

    }

    public Clients getClients() {
        return this.clients;
    }

    public void setClients(final Clients clients) {
        this.clients = clients;
    }

    public AuthenticationUserDetailsService<ClientAuthenticationToken> getUserDetailsService() {
        return this.userDetailsService;
    }

    public void setUserDetailsService(final AuthenticationUserDetailsService<ClientAuthenticationToken> userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public UserDetailsChecker getUserDetailsChecker() {
        return this.userDetailsChecker;
    }

    public void setUserDetailsChecker(final UserDetailsChecker userDetailsChecker) {
        this.userDetailsChecker = userDetailsChecker;
    }
}
