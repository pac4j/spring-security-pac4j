package org.pac4j.springframework.security.authentication;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;

import java.util.List;

/**
 * Pac4j authentication interface.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public interface Pac4jAuthentication {

    /**
     * Get the main profile of the authenticated user.
     *
     * @return the main profile
     */
    default UserProfile getProfile() {
        return SpringSecurityHelper.getMainProfile(getProfiles());
    }

    /**
     * Get all the profiles of the authenticated user.
     *
     * @return the list of profiles
     */
    List<UserProfile> getProfiles();
}
