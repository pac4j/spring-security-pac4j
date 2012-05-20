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
package org.leleuj.ss.oauth.client.authentication;

import java.util.Collection;

import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * This token represents an OAuth credentials ({@link org.scribe.up.credential.OAuthCredential}) and an user profile (
 * {@link org.scribe.up.profile.UserProfile}).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthAuthenticationToken extends AbstractAuthenticationToken {
    
    private static final long serialVersionUID = -2952756871952641628L;
    
    private OAuthCredential credential = null;
    
    private UserProfile userProfile = null;
    
    public OAuthAuthenticationToken(OAuthCredential credential) {
        super(null);
        this.credential = credential;
        setAuthenticated(false);
    }
    
    public OAuthAuthenticationToken(OAuthCredential credential, UserProfile userProfile,
                                    Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.credential = credential;
        this.userProfile = userProfile;
        
        setAuthenticated(true);
    }
    
    public Object getCredentials() {
        return credential;
    }
    
    public Object getPrincipal() {
        return userProfile.getTypedId();
    }
    
    public UserProfile getUserProfile() {
        return userProfile;
    }
}
