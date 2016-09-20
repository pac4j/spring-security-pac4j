package org.pac4j.springframework.security.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.springframework.security.util.SpringSecurityHelper;

/**
 * Specific profile manager for Spring Security.
 *
 * @author Jerome Leleu
 * @since 2.0.1
 */
public class SpringSecurityProfileManager extends ProfileManager<CommonProfile> {

    public SpringSecurityProfileManager(final WebContext context) {
        super(context);
    }

    @Override
    public void save(final boolean saveInSession, final CommonProfile profile, final boolean multiProfile) {
        super.save(saveInSession, profile, multiProfile);

        SpringSecurityHelper.populateAuthentication(retrieveAll(saveInSession));
    }
}
