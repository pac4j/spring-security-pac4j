package org.pac4j.springframework.security.authentication;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;

import java.util.LinkedHashMap;

/**
 * Pac4j authentication token in case of remember-me.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jRememberMeAuthenticationToken extends RememberMeAuthenticationToken implements Pac4jAuthentication {

    private final LinkedHashMap<String, CommonProfile> profiles;

    public Pac4jRememberMeAuthenticationToken(final LinkedHashMap<String, CommonProfile> profiles) {
        super("rme", ProfileHelper.flatIntoOneProfile(profiles).get(), SpringSecurityHelper.buildAuthorities(profiles));
        this.profiles = profiles;
    }

    @Override
    public LinkedHashMap<String, CommonProfile> getInternalProfilesMap() {
        return this.profiles;
    }
}
