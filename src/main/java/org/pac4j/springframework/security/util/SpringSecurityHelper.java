package org.pac4j.springframework.security.util;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Helper to build Spring Security authorities.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class SpringSecurityHelper {

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
}
