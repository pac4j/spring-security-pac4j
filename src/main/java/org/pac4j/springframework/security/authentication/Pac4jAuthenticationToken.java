package org.pac4j.springframework.security.authentication;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.LinkedHashMap;

/**
 * Pac4j authentication token when the user is authenticated.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jAuthenticationToken extends AbstractAuthenticationToken implements Pac4jAuthentication {

    private final LinkedHashMap<String, CommonProfile> profiles;

    public Pac4jAuthenticationToken(final LinkedHashMap<String, CommonProfile> profiles) {
        super(SpringSecurityHelper.buildAuthorities(profiles));
        this.profiles = profiles;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return ProfileHelper.flatIntoOneProfile(profiles);
    }

    @Override
    public LinkedHashMap<String, CommonProfile> getInternalProfilesMap() {
        return this.profiles;
    }
}
