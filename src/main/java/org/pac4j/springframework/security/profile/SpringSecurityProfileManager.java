package org.pac4j.springframework.security.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

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
            final SecurityContext securityContext = new SecurityContextImpl(authentication.get());
            SecurityContextHolder.setContext(securityContext);
            if (saveInSession) {
                sessionStore.set(context, WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                        securityContext);
            }
        }
    }

    @Override
    public void removeProfiles() {
        super.removeProfiles();

        SecurityContextHolder.clearContext();

        sessionStore.set(context, WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, null);
    }
}
