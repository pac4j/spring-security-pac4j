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
package org.pac4j.springframework.security.web;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.pac4j.springframework.security.exception.AuthenticationCredentialsException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mohd Farid mohd.farid@devfactory.com
 * @since 1.4.2
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({J2EContext.class, ClientAuthenticationToken.class, ClientAuthenticationFilter.class})
public class ClientAuthenticationFilterTest {

    ClientAuthenticationFilter clientAuthenticationFilter;

    @Mock
    private Clients clients;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private J2EContext j2EContext;

    @Mock
    private Client client;

    @Mock
    private Credentials credentials;

    @Mock
    private ClientAuthenticationToken clientAuthenticationToken;

    @Mock
    private Authentication authentication;

    @Before
    public void setup() throws Exception {
        clientAuthenticationFilter = new ClientAuthenticationFilter();
        clientAuthenticationFilter.setClients(clients);
        clientAuthenticationFilter.setAuthenticationManager(authenticationManager);
        PowerMockito.whenNew(J2EContext.class).withAnyArguments().thenReturn(j2EContext);
        when(client.getName()).thenReturn("CLIENT_NAME");
        when(clients.findClient(j2EContext)).thenReturn(client);
        clientAuthenticationFilter.setClients(clients);
        PowerMockito.whenNew(ClientAuthenticationToken.class).withArguments(credentials, "CLIENT_NAME").thenReturn(clientAuthenticationToken);
        when(authenticationManager.authenticate(clientAuthenticationToken)).thenReturn(authentication);
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        //when
        clientAuthenticationFilter.afterPropertiesSet();

        //then
        verify(clients).init();
    }

    @Test
    public void testAttemptAuthentication() throws Exception {
        //given
        when(client.getCredentials(j2EContext)).thenReturn(credentials);

        //when
        Authentication authenticationResult = clientAuthenticationFilter.attemptAuthentication(request, response);

        //then
        assertEquals(authentication, authenticationResult);
    }

    @Test
    public void testAttemptAuthentication_ForExceptionScenario() throws Exception {
        //given
        CredentialsException credentialsException = mock(CredentialsException.class);
        when(client.getCredentials(j2EContext)).thenThrow(credentialsException);

        //when
        try {
            clientAuthenticationFilter.attemptAuthentication(request, response);
            fail("An AuthenticationCredentialsException must have occurred as the getCredentials() " +
                    "method had thrown CredentialsException exception.");
        } catch (AuthenticationCredentialsException ex) {
            assertEquals("Error retrieving credentials", ex.getMessage());
            assertEquals(credentialsException, ex.getCause());
        }
    }

    @Test
    public void testAttemptAuthentication_ForRequiresHttpActionScenario() throws Exception {
        //given
        RequiresHttpAction requiresHttpAction = mock(RequiresHttpAction.class);
        when(client.getCredentials(j2EContext)).thenThrow(requiresHttpAction);

        //when
        Authentication authenticationResult = clientAuthenticationFilter.attemptAuthentication(request, response);

        //then
        assertNull("authentication result must be null when getCredentials() has thrown RequiresHttpAction exception",
                authenticationResult);
    }

    @Test
    public void testAttemptAuthentication_ForGetCredentialsReturningNull() throws Exception {
        //given
        when(client.getCredentials(j2EContext)).thenReturn(null);

        //when
        Authentication authenticationResult = clientAuthenticationFilter.attemptAuthentication(request, response);

        //then
        assertNull("authentication result must be null when getCredentials() returns null",
                authenticationResult);
        verify(j2EContext).setSessionAttribute(Pac4jConstants.REQUESTED_URL, "");
        verify(j2EContext).setSessionAttribute("CLIENT_NAME" + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "");
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
        validator.validate(PojoClassFactory.getPojoClass(ClientAuthenticationFilter.class));
    }

    @Test
    public void testConstructor() {
        //when
        ClientAuthenticationFilter clientAuthenticationFilter = new ClientAuthenticationFilter("suffix-url");

        //then
        assertEquals(new AntPathRequestMatcher("suffix-url"), Whitebox.getInternalState(clientAuthenticationFilter, "requiresAuthenticationRequestMatcher"));
    }
}
