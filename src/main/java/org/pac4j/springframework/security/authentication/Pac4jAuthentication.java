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
        return ProfileHelper.flatIntoOneProfile(getInternalProfilesMap()).get();
    }

    /**
     * Get all the profiles of the authenticated user.
     *
     * @return the list of profiles
     */
    default List<CommonProfile> getProfiles() {
        return ProfileHelper.flatIntoAProfileList(getInternalProfilesMap());
    }

    /**
     * Get all the profiles of the authenticated user as a map.
     *
     * @return the profiles map
     */
    LinkedHashMap<String, CommonProfile> getInternalProfilesMap();
}
