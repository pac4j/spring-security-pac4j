package org.pac4j.springframework.security.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Specific profile manager for Spring Security.
 *
 * @author Jerome Leleu
 * @since 2.0.1
 */
public class SpringSecurityProfileManager extends ProfileManager {

    public SpringSecurityProfileManager(final WebContext context, final SessionStore sessionStore) {
        super(context, sessionStore);
    }

    @Override
    protected void saveAll(LinkedHashMap<String, UserProfile> profiles, final boolean saveInSession) {
        super.saveAll(profiles, saveInSession);

        final Optional<Authentication> authentication = SpringSecurityHelper.computeAuthentication(profiles);
        if (authentication.isPresent()) {
            try {
                SecurityContextHolder.getContext().setAuthentication(authentication.get());
            } catch (final HttpAction e) {
                throw new TechnicalException(e);
            }
        }
    }

    @Override
    public void removeProfiles() {
        super.removeProfiles();

        SecurityContextHolder.clearContext();
    }
}
