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
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.web.ClientAuthenticationEntryPoint;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;

/**
 * @author Mohd Farid mohd.farid@devfactory.com
 * @since 1.4.2
 */
public final class ClientAuthenticationTokenTest {

    private final static String CLIENT_NAME = "SomeClientName";
    private final static String ROLE_USER = "ROLE_USER";
    private final static String ROLE_ADMIN = "ROLE_ADMIN";
    private final static String ID = "SomeUserId";

    private Credentials credentials;

    private UserProfile userProfile;

    private UserDetails userDetails;

    private WebContext webContext;

    private List<GrantedAuthority> authorities;

    @Before
    public void setUp() throws Exception {
        userDetails = mock(UserDetails.class, withSettings().extraInterfaces(CredentialsContainer.class));
        userProfile = mock(UserProfile.class);
        credentials = mock(Credentials.class);
        webContext = mock(WebContext.class);

        authorities = new ArrayList<>();
        GrantedAuthority userAuthority = mock(GrantedAuthority.class);
        when(userAuthority.getAuthority()).thenReturn(ROLE_USER);

        GrantedAuthority adminAuthority = mock(GrantedAuthority.class);
        when(adminAuthority.getAuthority()).thenReturn(ROLE_ADMIN);
        authorities.add(userAuthority);
        authorities.add(adminAuthority);
    }

    @Test
    public void testGetterSetters() throws Exception {
        Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
        validator.validate(PojoClassFactory.getPojoClass(ClientAuthenticationEntryPoint.class));
    }

    @Test
    public void testConstructor_credentials_clientName() {
        //when
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext);

        //then
        assertEquals(credentials, token.getCredentials());
        assertEquals(CLIENT_NAME, token.getClientName());
        assertEquals(null, token.getUserDetails());
        assertFalse(token.isAuthenticated());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructor_credentials_clientName_userProfile_authorities() {
        //when
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext, userProfile, authorities);

        //then
        assertEquals(credentials, token.getCredentials());
        assertEquals(userProfile, token.getUserProfile());
        assertEquals(CLIENT_NAME, token.getClientName());
        assertEquals(null, token.getUserDetails());
        assertTrue(token.isAuthenticated());
        List<GrantedAuthority> authoritiesFound = (List<GrantedAuthority>) getInternalState(token, "authorities");
        assertEquals(2, authoritiesFound.size());
        assertEquals(ROLE_USER, authoritiesFound.get(0).getAuthority());
        assertEquals(ROLE_ADMIN, authoritiesFound.get(1).getAuthority());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructor_credentials_clientName_userProfile_authorities_userDetails() {
        //when
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext, userProfile, authorities, userDetails);

        //then
        assertEquals(credentials, token.getCredentials());
        assertEquals(userProfile, token.getUserProfile());
        assertEquals(userDetails, token.getUserDetails());
        assertEquals(CLIENT_NAME, token.getClientName());
        assertTrue(token.isAuthenticated());
        List<GrantedAuthority> authoritiesFound = (List<GrantedAuthority>) getInternalState(token, "authorities");
        assertEquals(2, authoritiesFound.size());
        assertEquals(ROLE_USER, authoritiesFound.get(0).getAuthority());
        assertEquals(ROLE_ADMIN, authoritiesFound.get(1).getAuthority());
    }


    @Test
    public void testGetPrincipal_WhenUserDetailsAndProfileNotSet() throws Exception {
        //when
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext);

        //when
        Object principal = token.getPrincipal();

        //then
        assertNull(principal);
    }

    @Test
    public void testGetPrincipal_WhenUserDetailsIsSet() throws Exception {
        //when
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext, userProfile, authorities, userDetails);

        //when
        Object principal = token.getPrincipal();

        //then
        assertNotNull(principal);
        assertEquals(userDetails, principal);
    }

    @Test
    public void testGetPrincipal_WhenUserDetailsNotSet_ButUserProfileIsSet() throws Exception {
        //given
        when(userProfile.getTypedId()).thenReturn(ID);
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext, userProfile, authorities);

        //when
        Object principal = token.getPrincipal();

        //then
        assertEquals(ID, principal);
    }

    @Test
    public void testEraseCredentials() throws Exception {
        //given
        when(userProfile.getTypedId()).thenReturn(ID);
        ClientAuthenticationToken token = new ClientAuthenticationToken(credentials, CLIENT_NAME, webContext, userProfile, authorities, userDetails);

        //when
        token.eraseCredentials();

        //then
        // verify(credentials).clear();
        // verify(userProfile).clear();
        verify((CredentialsContainer) userDetails).eraseCredentials();
    }
}
