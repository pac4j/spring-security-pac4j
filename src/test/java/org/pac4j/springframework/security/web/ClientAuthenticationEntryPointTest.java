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
import org.mockito.Mockito;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mohd Farid mohd.farid@devfactory.com
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientAuthenticationEntryPoint.class})
public class ClientAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private J2EContext j2EContext;

    @Mock
    private Client client;

    private ClientAuthenticationEntryPoint clientAuthenticationEntryPoint;

    @Before
    public void setup() throws Exception {
        clientAuthenticationEntryPoint = new ClientAuthenticationEntryPoint();
        clientAuthenticationEntryPoint.setClient(client);
        PowerMockito.whenNew(J2EContext.class).withAnyArguments().thenReturn(j2EContext);
    }

    @Test
    public void testCommence() throws Exception {
        //when
        clientAuthenticationEntryPoint.commence(request, response, null);

        //then
        Mockito.verify(client).redirect(j2EContext);
    }

    @Test
    public void testCommence_ForExceptionScenario() throws Exception {
        //given
        HttpAction requiresHttpAction = mock(HttpAction.class);
        doThrow(requiresHttpAction).when(client).redirect(j2EContext);

        //when
        clientAuthenticationEntryPoint.commence(request, response, null);

        //then
        Mockito.verify(client).redirect(j2EContext);
    }

    @Test
    public void testAfterPropertiesSet_ForClientAlreadySet() throws Exception {
        //given
        assertNotNull("Client is already set in the setup", clientAuthenticationEntryPoint.getClient());

        try {
            //when
            clientAuthenticationEntryPoint.afterPropertiesSet();

        } catch (Throwable th) {
            //then
            fail("No exception must have been thrown since client was set already");
        }
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        //given
        clientAuthenticationEntryPoint.setClient(null);

        //when
        try {
            clientAuthenticationEntryPoint.afterPropertiesSet();
        } catch (Throwable th) {
            assertEquals("client cannot be null", th.getMessage());
        }
    }

    @Test
    public void testGetterSetters() throws Exception {
        Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
        validator.validate(PojoClassFactory.getPojoClass(ClientAuthenticationEntryPoint.class));
    }
}
