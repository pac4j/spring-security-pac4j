package org.pac4j.springframework.security.util;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.authentication.Pac4jAuthenticationToken;
import org.pac4j.springframework.security.authentication.Pac4jRememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * Helper for Spring Security.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class SpringSecurityHelper {

    private final static Authorizer IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer();

    private final static Authorizer IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer();

    /**
     * Build a list of authorities from a list of profiles.
     *
     * @param profiles a map of profiles
     * @return a list of authorities
     */
    public static List<GrantedAuthority> buildAuthorities(final List<UserProfile> profiles) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final UserProfile profile : profiles) {
            final Set<String> roles = profile.getRoles();
            for (final String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorities;
    }

    /**
     * Compute the Spring Security authentication from the pac4j profiles.
     *
     * @param profiles the linked hashmap of profiles
     * @return the optional Spring Security authentication
     */
    public static Optional<Authentication> computeAuthentication(final LinkedHashMap<String, UserProfile> profiles) {
        if (profiles != null && profiles.size() > 0) {
            final List<UserProfile> listProfiles = ProfileHelper.flatIntoAProfileList(profiles);
            if (IS_FULLY_AUTHENTICATED_AUTHORIZER.isAuthorized(null, null, listProfiles)) {
                return Optional.of(new Pac4jAuthenticationToken(listProfiles));
            } else if (IS_REMEMBERED_AUTHORIZER.isAuthorized(null, null, listProfiles)) {
                return Optional.of(new Pac4jRememberMeAuthenticationToken(listProfiles));
            }
        }
        return Optional.empty();
    }

    /**
     * Populate the authenticated user profiles in the Spring Security context.
     *
     * @param profiles the linked hashmap of profiles
     */
    public static void populateAuthentication(final LinkedHashMap<String, UserProfile> profiles) {
        final Optional<Authentication> authentication = computeAuthentication(profiles);
        if (authentication.isPresent()) {
            try {
                SecurityContextHolder.getContext().setAuthentication(authentication.get());
            } catch (final HttpAction e) {
                throw new TechnicalException(e);
            }
        }
    }
}
