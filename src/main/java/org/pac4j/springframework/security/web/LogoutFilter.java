package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.J2ENopHttpActionAdapter;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This filter handles the (application + identity provider) logout process, based on the {@link #logoutLogic}.</p>
 *
 * <p>The configuration can be provided via setters: {@link #setConfig(Config)} (security configuration), {@link #setDefaultUrl(String)} (default logourl url),
 * {@link #setLogoutUrlPattern(String)} (pattern that logout urls must match), {@link #setLocalLogout(Boolean)} (whether the application logout must be performed),
 * {@link #setDestroySession(Boolean)} (whether we must destroy the web session during the local logout) and {@link #setCentralLogout(Boolean)} (whether the centralLogout must be performed).</p>
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class LogoutFilter implements Filter {

    private LogoutLogic<Object, J2EContext> logoutLogic;

    private Config config;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean destroySession;

    private Boolean centralLogout;

    public LogoutFilter() {
        logoutLogic = new DefaultLogoutLogic<>();
        ((DefaultLogoutLogic<Object, J2EContext>) logoutLogic).setProfileManagerFactory(SpringSecurityProfileManager::new);
    }

    public LogoutFilter(final Config config) {
        this();
        this.config = config;
    }

    public LogoutFilter(final Config config, final String defaultUrl) {
        this(config);
        this.defaultUrl = defaultUrl;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {

        assertNotNull("logoutLogic", logoutLogic);

        final Config config = getConfig();
        assertNotNull("config", config);
        final J2EContext context = new J2EContext((HttpServletRequest) req, (HttpServletResponse) resp, config.getSessionStore());

        logoutLogic.perform(context, config, J2ENopHttpActionAdapter.INSTANCE, this.defaultUrl, this.logoutUrlPattern, this.localLogout, this.destroySession, this.centralLogout);

    }

    @Override
    public void destroy() { }

    public LogoutLogic<Object, J2EContext> getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(final LogoutLogic<Object, J2EContext> logoutLogic) {
        this.logoutLogic = logoutLogic;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getLogoutUrlPattern() {
        return logoutUrlPattern;
    }

    public void setLogoutUrlPattern(final String logoutUrlPattern) {
        this.logoutUrlPattern = logoutUrlPattern;
    }

    public Boolean getLocalLogout() {
        return localLogout;
    }

    public void setLocalLogout(final Boolean localLogout) {
        this.localLogout = localLogout;
    }

    public Boolean getDestroySession() {
        return destroySession;
    }

    public void setDestroySession(final Boolean destroySession) {
        this.destroySession = destroySession;
    }

    public Boolean getCentralLogout() {
        return centralLogout;
    }

    public void setCentralLogout(final Boolean centralLogout) {
        this.centralLogout = centralLogout;
    }
}
