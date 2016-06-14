package org.pac4j.springframework.security;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * Specific security logic for Spring Security to save the user profiles returned by direct clients into the session.
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class SpringSecurityLogic extends DefaultSecurityLogic<Object, J2EContext> {

    @Override
    protected boolean saveProfileInSession(final J2EContext context, final List<Client> currentClients, final DirectClient directClient, final CommonProfile profile) {
        return true;
    }
}
