package org.pac4j.springframework.security.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.util.SpringSecurityHelper;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;

/**
 * Specific profile manager for Spring Security.
 *
 * @author Jerome Leleu
 * @since 2.0.1
 */
public class SpringSecurityProfileManager extends ProfileManager {

    public SpringSecurityProfileManager(final WebContext context) {
        super(context);
    }

    @Override
    protected void saveAll(LinkedHashMap<String, UserProfile> profiles, final boolean saveInSession) {
        super.saveAll(profiles, saveInSession);

        SpringSecurityHelper.populateAuthentication(retrieveAll(saveInSession));
    }

    @Override
    public void removeProfiles() {
        super.removeProfiles();

        SecurityContextHolder.clearContext();
    }
}
