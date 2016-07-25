package org.pac4j.springframework.security.util;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.springframework.security.authentication.Pac4jAuthenticationToken;
import org.pac4j.springframework.security.authentication.Pac4jRememberMeAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Helper for Spring Security.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class SpringSecurityHelper {

    private final static Authorizer<CommonProfile> IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer<>();

    private final static Authorizer<CommonProfile> IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer<>();

    /**
     * Build a list of authorities from a map of profiles.
     *
     * @param profiles a map of profiles
     * @return a list of authorities
     */
    public static List<GrantedAuthority> buildAuthorities(final LinkedHashMap<String, CommonProfile> profiles) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final List<CommonProfile> listProfiles = ProfileHelper.flatIntoAProfileList(profiles);
        for (final CommonProfile profile : listProfiles) {
            final Set<String> roles = profile.getRoles();
            for (final String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorities;
    }

    /**
     * Populate the authenticated user profiles in the Spring Security context.
     *
     * @param profiles the linked hashmap of profiles
     */
    public static void populateAuthentication(final LinkedHashMap<String, CommonProfile> profiles) {
        if (profiles != null && profiles.size() > 0) {
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
        }
    }
}
