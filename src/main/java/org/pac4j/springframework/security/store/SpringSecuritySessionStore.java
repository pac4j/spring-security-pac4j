package org.pac4j.springframework.security.store;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.J2ESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.springframework.security.authentication.Pac4jAuthentication;
import org.pac4j.springframework.security.authentication.Pac4jAuthenticationToken;
import org.pac4j.springframework.security.authentication.Pac4jRememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Specific session store to use the {@link org.springframework.security.core.context.SecurityContextHolder}
 * to store / get user profiles and make them naturally available to Spring Security
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SpringSecuritySessionStore implements SessionStore<J2EContext> {

    private final Authorizer<CommonProfile> IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer<>();

    private final Authorizer<CommonProfile> IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer<>();

    private SessionStore<J2EContext> internalSessionStore = new J2ESessionStore();

    @Override
    public String getOrCreateSessionId(final J2EContext context) {
        return internalSessionStore.getOrCreateSessionId(context);
    }

    @Override
    public Object get(final J2EContext context, final String key) {
        if (Pac4jConstants.USER_PROFILES.equals(key)) {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth instanceof Pac4jAuthentication) {
                return ((Pac4jAuthentication) auth).getInternalProfilesMap();
            } else {
                return null;
            }
        } else {
            return internalSessionStore.get(context, key);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(final J2EContext context, final String key, final Object value) {
        if (Pac4jConstants.USER_PROFILES.equals(key)) {
            final LinkedHashMap<String, CommonProfile> profiles = (LinkedHashMap<String, CommonProfile>) value;
            final List<CommonProfile> listProfiles = ProfileHelper.flatIntoAProfileList(profiles);
            try {
                if (IS_FULLY_AUTHENTICATED_AUTHORIZER.isAuthorized(null, listProfiles)) {
                    SecurityContextHolder.getContext().setAuthentication(new Pac4jAuthenticationToken(profiles));
                } else if (IS_REMEMBERED_AUTHORIZER.isAuthorized(null, listProfiles)) {
                    SecurityContextHolder.getContext().setAuthentication(new Pac4jRememberMeAuthenticationToken(profiles));
                }
            } catch (final HttpAction e) {
                throw new TechnicalException(e);
            }

        } else {
            internalSessionStore.set(context, key, value);
        }
    }

    public SessionStore<J2EContext> getInternalSessionStore() {
        return internalSessionStore;
    }

    public void setInternalSessionStore(SessionStore<J2EContext> internalSessionStore) {
        CommonHelper.assertNotNull("internalSessionStore", internalSessionStore);
        this.internalSessionStore = internalSessionStore;
    }
}
