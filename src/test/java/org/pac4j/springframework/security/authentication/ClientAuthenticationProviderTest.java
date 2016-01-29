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

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mohd Farid mohd.farid@devfactory.com
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientAuthenticationToken.class})
public class ClientAuthenticationProviderTest {

    private Authentication authentication;

    @Before
    public void setUp() throws Exception {
        authentication = mock(Authentication.class);
    }

    @Test
    public void testGetterSetters() throws Exception {
        Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
        validator.validate(PojoClassFactory.getPojoClass(ClientAuthenticationProvider.class));
    }

    @Test
    public void testAuthenticate() throws Exception {
        //given
        ClientAuthenticationProvider authenticationProvider = new ClientAuthenticationProvider();

        //when
        Authentication resultAuthentication = authenticationProvider.authenticate(authentication);

        //then
        assertNull(resultAuthentication);
    }

    @Test
    public void testAuthenticate_WithClientAuthenticationToken() throws Exception {
        //given
        ClientAuthenticationToken clientAuthenticationToken = PowerMockito.mock(ClientAuthenticationToken.class);
        Credentials credentials = mock(Credentials.class);
        when(clientAuthenticationToken.getCredentials()).thenReturn(credentials);
        when(clientAuthenticationToken.getClientName()).thenReturn("SimpleClientName");

        ClientAuthenticationProvider authenticationProvider = new ClientAuthenticationProvider();
        Clients clients = mock(Clients.class);
        Client client = mock(Client.class);
        UserProfile userProfile = mock(UserProfile.class);
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        when(userProfile.getRoles()).thenReturn(roles);
        when(client.getUserProfile(credentials, null)).thenReturn(userProfile);
        when(clients.findClient("SimpleClientName")).thenReturn(client);
        authenticationProvider.setClients(clients);

        //when
        Authentication resultAuthentication = authenticationProvider.authenticate(clientAuthenticationToken);

        //then
        assertNotNull(resultAuthentication);
        assertEquals(credentials, resultAuthentication.getCredentials());
        assertEquals(userProfile, Whitebox.getInternalState(resultAuthentication, "userProfile"));
        assertEquals("SimpleClientName", Whitebox.getInternalState(resultAuthentication, "clientName"));

        List<? extends GrantedAuthority> authoritiesFound = (List<? extends GrantedAuthority>) resultAuthentication.getAuthorities();
        assertEquals(2, authoritiesFound.size());
        assertEquals("ROLE_USER", authoritiesFound.get(0).getAuthority());
        assertEquals("ROLE_ADMIN", authoritiesFound.get(1).getAuthority());

        assertTrue(resultAuthentication.isAuthenticated());
    }

    @Test
    public void testSupports() throws Exception {
        //given
        ClientAuthenticationProvider authenticationProvider = new ClientAuthenticationProvider();

        //then
        assertTrue(authenticationProvider.supports(mock(ClientAuthenticationToken.class).getClass()));
        assertFalse(authenticationProvider.supports(mock(AnonymousAuthenticationToken.class).getClass()));
        assertFalse(authenticationProvider.supports(mock(UsernamePasswordAuthenticationToken.class).getClass()));
        assertFalse(authenticationProvider.supports(mock(RememberMeAuthenticationToken.class).getClass()));
        assertFalse(authenticationProvider.supports(mock(AbstractAuthenticationToken.class).getClass()));
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        //given
        ClientAuthenticationProvider authenticationProvider = new ClientAuthenticationProvider();
        Clients clients = mock(Clients.class);
        authenticationProvider.setClients(clients);

        //when
        authenticationProvider.afterPropertiesSet();

        //then
        verify(clients).init();
    }

    @Test
    public void testAfterPropertiesSet_WithClientsNotSet() throws Exception {
        //given
        ClientAuthenticationProvider authenticationProvider = new ClientAuthenticationProvider();

        try {
            //when
            authenticationProvider.afterPropertiesSet();

            fail("An assertion error should have occurred as clients variable was not set");
        } catch (Throwable throwable) {

            assertEquals("clients cannot be null", throwable.getMessage());
        }
    }
}
