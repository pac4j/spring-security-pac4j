package org.pac4j.springframework.security.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.springframework.security.util.SpringSecurityHelper;

import java.util.LinkedHashMap;

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
    protected void saveAll(LinkedHashMap<String, CommonProfile> profiles, final boolean saveInSession) {
        super.saveAll(profiles, saveInSession);

        SpringSecurityHelper.populateAuthentication(retrieveAll(saveInSession));
    }
}
