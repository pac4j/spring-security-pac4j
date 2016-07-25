package org.pac4j.springframework.security.context;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

/**
 * A specific Spring Security web context where the authenticated profiles are saved into the Spring security context.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SpringSecurityContext extends J2EContext {

    public SpringSecurityContext(final HttpServletRequest request, final HttpServletResponse response) {
        super(request, response);
    }

    public SpringSecurityContext(final HttpServletRequest request, final HttpServletResponse response, final SessionStore<J2EContext> sessionStore) {
        super(request, response, sessionStore);
    }

    @Override
    public void setRequestAttribute(final String name, final Object value) {
        if (Pac4jConstants.USER_PROFILES.equals(name)) {
            SpringSecurityHelper.populateAuthentication((LinkedHashMap<String, CommonProfile>) value);
        }
        super.setRequestAttribute(name, value);
    }

    @Override
    public void setSessionAttribute(final String name, final Object value) {
        if (Pac4jConstants.USER_PROFILES.equals(name)) {
            SpringSecurityHelper.populateAuthentication((LinkedHashMap<String, CommonProfile>) value);
        }
        super.setSessionAttribute(name, value);
    }
}
