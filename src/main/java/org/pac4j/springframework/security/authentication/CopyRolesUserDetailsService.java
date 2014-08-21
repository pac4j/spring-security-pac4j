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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This service copies roles creates user details containing authorities based on the roles from the token's
 * userProfile.
 *
 * @author Karel Vervaeke
 * @since 1.6.0
 */
public class CopyRolesUserDetailsService implements AuthenticationUserDetailsService<ClientAuthenticationToken> {


    public UserDetails loadUserDetails(final ClientAuthenticationToken token) throws UsernameNotFoundException {
        final List<GrantedAuthority> authorities = new ArrayList();
        for (String role: token.getUserProfile().getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new UserDetails() {
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }

            public String getPassword() {
                return null;
            }

            public String getUsername() {
                return token.getUserProfile().getId();
            }

            public boolean isAccountNonExpired() {
                return true;
            }

            public boolean isAccountNonLocked() {
                return true;
            }

            public boolean isCredentialsNonExpired() {
                return true;
            }

            public boolean isEnabled() {
                return true;
            }
        };

    }
}
