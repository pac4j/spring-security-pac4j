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
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Mohd Farid mohd.farid@devfactory.com
 * @since 1.4.2
 */
public class ClientUserDetailsTest {

    ClientUserDetails clientUserDetails;

    @Before
    public void setup() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority userAuthority = Mockito.mock(GrantedAuthority.class);
        Mockito.when(userAuthority.getAuthority()).thenReturn("ROLE_USER");

        GrantedAuthority adminAuthority = Mockito.mock(GrantedAuthority.class);
        Mockito.when(adminAuthority.getAuthority()).thenReturn("ROLE_ADMIN");
        authorities.add(userAuthority);
        authorities.add(adminAuthority);

        clientUserDetails = new ClientUserDetails("my-user-name", authorities);
    }

    @Test
    public void testGetterSetters() throws Exception {
        Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
        validator.validate(PojoClassFactory.getPojoClass(ClientUserDetails.class));
    }

    @Test
    public void testGetPassword() throws Exception {
        assertNull("Password must always be null as it is hardcoded", clientUserDetails.getPassword());
    }

    @Test
    public void testIsAccountNonExpired() throws Exception {
        assertTrue("isAccountNonExpired must always return true as it is hardcoded" , clientUserDetails.isAccountNonExpired());
    }

    @Test
    public void testIsAccountNonLocked() throws Exception {
        assertTrue("isAccountNonLocked must always return true as it is hardcoded" , clientUserDetails.isAccountNonLocked());
    }

    @Test
    public void testIsCredentialsNonExpired() throws Exception {
        assertTrue("isCredentialsNonExpired must always return true as it is hardcoded" , clientUserDetails.isCredentialsNonExpired());
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertTrue("isEnabled must always return true as it is hardcoded" , clientUserDetails.isEnabled());
    }
}
