package org.pac4j.springframework.security.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Specific profile manager for Spring Security reactive.
 *
 * @author Jerome LELEU
 * @since 8.0.0
 */
public class SpringSecurityReactiveProfileManager extends ProfileManager {

    public SpringSecurityReactiveProfileManager(final WebContext context, final SessionStore sessionStore) {
        super(context, sessionStore);
    }

    @Override
    protected void saveAll(LinkedHashMap<String, UserProfile> profiles, final boolean saveInSession) {
        super.saveAll(profiles, saveInSession);

        final Optional<Authentication> authentication = SpringSecurityHelper.computeAuthentication(retrieveAll(saveInSession));
        if (authentication.isPresent()) {
            sessionStore.set(context, WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                    new SecurityContextImpl(authentication.get()));
        }
    }

    @Override
    public void removeProfiles() {
        super.removeProfiles();

        sessionStore.set(context, WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, null);
    }
}
