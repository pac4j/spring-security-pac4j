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
import org.springframework.util.ClassUtils;

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

    /**
     * Create a profile manager that synchronizes pac4j profiles with Spring Security.
     *
     * @param context the current web context
     * @param sessionStore the store used to access the user session
     */
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
     * Outside a servlet environment (WebFlux), the attribute is written through the pac4j session store.
     *
     * @param securityContext the security context or {@code null} to remove it
     */
    protected void saveSecurityContext(final SecurityContext securityContext) {
        if (SERVLET_AVAILABLE && ServletSupport.saveSecurityContext(context, sessionStore, securityContext)) {
            return;
        }
        sessionStore.set(context, SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    private static final boolean SERVLET_AVAILABLE = isPresent("org.pac4j.jee.context.JEEContext")
        && isPresent("jakarta.servlet.http.HttpSession");

    private static boolean isPresent(final String className) {
        return ClassUtils.isPresent(className, SpringSecurityProfileManager.class.getClassLoader());
    }

    /**
     * Servlet-specific code, kept in a nested class so that it is only loaded
     * when pac4j-jakartaee and the Servlet API are on the classpath.
     */
    private static final class ServletSupport {

        private ServletSupport() {}

        static boolean saveSecurityContext(final WebContext context, final SessionStore sessionStore,
                                           final SecurityContext securityContext) {
            if (!(context instanceof JEEContext jeeContext)) {
                return false;
            }
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
            return true;
        }
    }
}
