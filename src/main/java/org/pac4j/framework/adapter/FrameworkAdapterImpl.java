package org.pac4j.framework.adapter;

import org.pac4j.core.config.Config;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jee.adapter.JEEFrameworkAdapter;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;

/**
 * Spring Security adapter.
 *
 * @author Jerome LELEU
 * @since 10.0.0
 */
public class FrameworkAdapterImpl extends JEEFrameworkAdapter {

    @Override
    public void applyDefaultSettingsIfUndefined(final Config config) {
        CommonHelper.assertNotNull("config", config);
        config.setProfileManagerFactoryIfUndefined((ctx, session) -> new SpringSecurityProfileManager(ctx, session));

        super.applyDefaultSettingsIfUndefined(config);
    }

    @Override
    public String toString() {
        return "Spring Security";
    }
}
