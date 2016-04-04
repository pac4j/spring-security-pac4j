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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pac4j.core.profile.UserProfile;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Mohd Farid mohd.farid@devfactory.com
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CopyRolesUserDetailsService.class, ClientAuthenticationToken.class})
public class CopyRolesUserDetailsServiceTest {

    private CopyRolesUserDetailsService userDetailsService;

    @Mock
    private ClientAuthenticationToken token;

    @Mock
    private UserProfile userProfile;

    @Test
    public void testLoadUserDetails() throws Exception {
        //given
        userDetailsService = new CopyRolesUserDetailsService();

        List<String> roles = new ArrayList<String>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        Mockito.when(userProfile.getRoles()).thenReturn(roles);
        Mockito.when(userProfile.getId()).thenReturn("user-id");

        Mockito.when(token.getUserProfile()).thenReturn(userProfile);

        //when
        UserDetails userDetails = userDetailsService.loadUserDetails(token);

        //then
        assertEquals("user-id", userDetails.getUsername());
        assertEquals(2, userDetails.getAuthorities().size());

        Iterator<? extends GrantedAuthority> iterator = userDetails.getAuthorities().iterator();
        assertEquals("ROLE_USER", iterator.next().getAuthority());
        assertEquals("ROLE_ADMIN", iterator.next().getAuthority());
    }
}
