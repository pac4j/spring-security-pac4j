package org.pac4j.springframework.security.authentication;

import lombok.EqualsAndHashCode;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;

import java.util.List;

/**
 * Pac4j authentication token in case of remember-me.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@EqualsAndHashCode(callSuper = true)
public class Pac4jRememberMeAuthenticationToken extends RememberMeAuthenticationToken implements Pac4jAuthentication {

    private final List<UserProfile> profiles;

    public Pac4jRememberMeAuthenticationToken(final List<UserProfile> profiles) {
        super("rme", SpringSecurityHelper.getMainProfile(profiles), SpringSecurityHelper.buildAuthorities(profiles));
        this.profiles = profiles;
        setAuthenticated(true);
    }

    @Override
    public String getName() {
        return ((UserProfile) getPrincipal()).getId();
    }

    @Override
    public List<UserProfile> getProfiles() {
        return this.profiles;
    }
}
