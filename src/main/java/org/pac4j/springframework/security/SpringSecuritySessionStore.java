package org.pac4j.springframework.security;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.J2ESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;

/**
 * Specific session store to use the {@link org.springframework.security.core.context.SecurityContextHolder}
 * to store / get user profiles and make them naturally available to Spring Security
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class SpringSecuritySessionStore implements SessionStore<J2EContext> {

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
                return ((Pac4jAuthentication) auth).getLinkedMapProfiles();
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
            SecurityContextHolder.getContext().setAuthentication(new Pac4jAuthentication(profiles));
        } else {
            internalSessionStore.set(context, key, value);
        }
    }

    public SessionStore<J2EContext> getInternalSessionStore() {
        return internalSessionStore;
    }

    public void setInternalSessionStore(SessionStore<J2EContext> internalSessionStore) {
        this.internalSessionStore = internalSessionStore;
    }
}
