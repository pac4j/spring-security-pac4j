package org.pac4j.springframework.security.authentication;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

import java.util.*;

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
    default CommonProfile getProfile() {
        return ProfileHelper.flatIntoOneProfile(getProfiles()).get();
    }

    /**
     * Get all the profiles of the authenticated user.
     *
     * @return the list of profiles
     */
    List<CommonProfile> getProfiles();
}
