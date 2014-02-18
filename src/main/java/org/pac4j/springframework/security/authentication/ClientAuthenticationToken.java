/*
  Copyright 2012 - 2014 Jerome Leleu

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

import java.util.Collection;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * This token represents a credentials ({@link Credentials}), a client name and an user profile ( {@link UserProfile}).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ClientAuthenticationToken extends AbstractAuthenticationToken {
    
    private static final long serialVersionUID = 8303047831754762526L;
    
    private final Credentials credentials;
    
    private UserProfile userProfile = null;
    
    private final String clientName;
    
    public ClientAuthenticationToken(final Credentials credentials, final String clientName) {
        super(null);
        this.credentials = credentials;
        this.clientName = clientName;
        setAuthenticated(false);
    }
    
    public ClientAuthenticationToken(final Credentials credentials, final String clientName,
                                     final UserProfile userProfile,
                                     final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.credentials = credentials;
        this.clientName = clientName;
        this.userProfile = userProfile;
        setAuthenticated(true);
    }
    
    public Object getCredentials() {
        return this.credentials;
    }
    
    public Object getPrincipal() {
        if (this.userProfile != null) {
            return this.userProfile.getTypedId();
        } else {
            return null;
        }
    }
    
    public UserProfile getUserProfile() {
        return this.userProfile;
    }
    
    public String getClientName() {
        return this.clientName;
    }
}
