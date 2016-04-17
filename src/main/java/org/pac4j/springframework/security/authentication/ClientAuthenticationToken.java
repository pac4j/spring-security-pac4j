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

import java.util.Collection;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This token represents a credentials: {@link #credentials}, a client name: {@link #clientName},
 * a user profile: {@link #userProfile}, a web context {@link #userProfile} and a user (details): {@link #userDetails}.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ClientAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 8303047831754762526L;

    private Credentials credentials;

    private UserProfile userProfile = null;

    private final String clientName;

    private final UserDetails userDetails;

    private final WebContext context;

    public ClientAuthenticationToken(final Credentials credentials, final String clientName, final WebContext context) {
        super(null);
        this.credentials = credentials;
        this.clientName = clientName;
        this.context = context;
        this.userDetails = null;
        setAuthenticated(false);
    }

    public ClientAuthenticationToken(final Credentials credentials, final String clientName, final WebContext context,
            final UserProfile userProfile, final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.credentials = credentials;
        this.clientName = clientName;
        this.context = context;
        this.userProfile = userProfile;
        this.userDetails = null;
        setAuthenticated(true);
    }

    public ClientAuthenticationToken(final Credentials credentials, final String clientName, final WebContext context,
            final UserProfile userProfile, final Collection<? extends GrantedAuthority> authorities,
            final UserDetails userDetails) {
        super(authorities);
        this.credentials = credentials;
        this.clientName = clientName;
        this.context = context;
        this.userProfile = userProfile;
        this.userDetails = userDetails;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        if (this.userDetails != null)
            return this.userDetails;
        if (this.userProfile != null) {
            return this.userProfile.getTypedId();
        } else {
            return null;
        }
    }

    @Override
    public void eraseCredentials() {
        //credentials.clear();
        //if (userProfile != null) {
            //userProfile.clear();
        //}
        if (userDetails != null && userDetails instanceof CredentialsContainer) {
            ((CredentialsContainer) userDetails).eraseCredentials();
        }
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public String getClientName() {
        return this.clientName;
    }

    public UserDetails getUserDetails() {
        return this.userDetails;
    }

    public WebContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "credentials", this.credentials, "clientName", this.clientName,
                "userProfile", this.userProfile, "userDetails", this.userDetails);
    }
}
