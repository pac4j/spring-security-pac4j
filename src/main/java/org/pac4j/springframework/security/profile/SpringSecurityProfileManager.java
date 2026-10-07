package org.pac4j.springframework.security.profile;

import jakarta.servlet.http.HttpSession;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Specific profile manager for Spring Security.
 *
 * @author Jerome Leleu
 * @since 2.0.1
 */
public class SpringSecurityProfileManager extends ProfileManager {

    /**
     * The session attribute read by Spring Security. Its value is the same as the reactive
     * {@code WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME},
     * so the session store fallback also works with a WebFlux session store.
     */
    protected static final String SPRING_SECURITY_CONTEXT_KEY = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

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
                saveSecurityContext(securityContext);
            }
        } else {
            // no more profiles (expired and not renewable...): keep Spring Security in sync with pac4j
            SecurityContextHolder.clearContext();
            if (saveInSession) {
                saveSecurityContext(null);
            }
        }
    }

    @Override
    public void removeProfiles() {
        super.removeProfiles();

        SecurityContextHolder.clearContext();

        saveSecurityContext(null);
    }

    /**
     * Save (or remove if {@code null}) the Spring Security context in the web session.
     * The attribute is written directly in the servlet session when possible: the pac4j session store may prefix its keys
     * while Spring Security reads the exact {@code SPRING_SECURITY_CONTEXT} attribute.
     * A tracked servlet session takes precedence over the request session for back-channel logout.
     *
     * @param securityContext the security context or {@code null} to remove it
     */
    protected void saveSecurityContext(final SecurityContext securityContext) {
        if (context instanceof JEEContext jeeContext) {
            final HttpSession session = sessionStore.getTrackableSession(context)
                .filter(HttpSession.class::isInstance)
                .map(HttpSession.class::cast)
                .orElseGet(() -> jeeContext.getNativeRequest().getSession(securityContext != null));
            if (session != null) {
                if (securityContext == null) {
                    session.removeAttribute(SPRING_SECURITY_CONTEXT_KEY);
                } else {
                    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
                }
            }
        } else {
            sessionStore.set(context, SPRING_SECURITY_CONTEXT_KEY, securityContext);
        }
    }
}
