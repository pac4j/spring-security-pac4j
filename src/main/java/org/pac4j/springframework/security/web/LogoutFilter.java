package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.util.FindBest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>This filter handles the (application + identity provider) logout process.</p>
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class LogoutFilter extends AbstractPathFilter {

    private LogoutLogic<Object, JEEContext> logoutLogic;

    private Config config;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean destroySession;

    private Boolean centralLogout;

    public LogoutFilter() {}

    public LogoutFilter(final Config config) {
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

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final LogoutLogic<Object, JEEContext> bestLogic = FindBest.logoutLogic(logoutLogic, config, DefaultLogoutLogic.INSTANCE);

        final JEEContext context = new JEEContext((HttpServletRequest) req, (HttpServletResponse) resp, bestSessionStore);
        if (mustApply(context)) {
            bestLogic.perform(context, config, bestAdapter, this.defaultUrl, this.logoutUrlPattern, this.localLogout, this.destroySession, this.centralLogout);
        } else {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() { }

    @Override
    protected void internalInit() { }

    public LogoutLogic<Object, JEEContext> getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(final LogoutLogic<Object, JEEContext> logoutLogic) {
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
