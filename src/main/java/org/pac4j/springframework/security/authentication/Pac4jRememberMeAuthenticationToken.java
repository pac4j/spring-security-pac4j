package org.pac4j.springframework.security.authentication;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
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
public class Pac4jRememberMeAuthenticationToken extends RememberMeAuthenticationToken implements Pac4jAuthentication {

    private final List<UserProfile> profiles;

    public Pac4jRememberMeAuthenticationToken(final List<UserProfile> profiles) {
        super("rme", ProfileHelper.flatIntoOneProfile(profiles).get(), SpringSecurityHelper.buildAuthorities(profiles));
        this.profiles = profiles;
        setAuthenticated(true);
    }

    @Override
    public String getName() {
        return ((CommonProfile) getPrincipal()).getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final Pac4jRememberMeAuthenticationToken that = (Pac4jRememberMeAuthenticationToken) o;

        return profiles != null ? profiles.equals(that.profiles) : that.profiles == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (profiles != null ? profiles.hashCode() : 0);
        return result;
    }

    @Override
    public List<UserProfile> getProfiles() {
        return this.profiles;
    }
}
