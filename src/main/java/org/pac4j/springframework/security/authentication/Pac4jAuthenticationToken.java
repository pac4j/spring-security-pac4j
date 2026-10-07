package org.pac4j.springframework.security.authentication;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

/**
 * Pac4j authentication token when the user is authenticated.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jAuthenticationToken extends AbstractAuthenticationToken implements Pac4jAuthentication {

    /** The pac4j profiles represented by this authentication. */
    private final List<UserProfile> profiles;

    /** The main profile used as the Spring Security principal. */
    private final UserProfile profile;

    /**
     * Create an authenticated token with the roles of the supplied profiles.
     *
     * @param profiles the non-empty list of authenticated profiles
     */
    public Pac4jAuthenticationToken(final List<UserProfile> profiles) {
        super(SpringSecurityHelper.buildAuthorities(profiles));
        this.profiles = profiles;
        this.profile = SpringSecurityHelper.getMainProfile(profiles);
        setAuthenticated(true);
    }

    @Override
    public String getName() {
        return profile.getId();
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return profile;
    }

    @Override
    public List<UserProfile> getProfiles() {
        return this.profiles;
    }
}
