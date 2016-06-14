package org.pac4j.springframework.security;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.J2ERenewSessionCallbackLogic;
import org.pac4j.core.http.J2ENopHttpActionAdapter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>This filter finishes the login process for an indirect client, based on the {@link #callbackLogic}.</p>
 *
 * <p>The configuration can be provided via setter methods: {@link #setConfig(Config)} (security configuration), {@link #setDefaultUrl(String)} (default url after login if none was requested),
 * {@link #setMultiProfile(Boolean)} (whether multiple profiles should be kept) and ({@link #setRenewSession(Boolean)} (whether the session must be renewed after login).</p>
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class CallbackFilter implements Filter {

    private CallbackLogic<Object, J2EContext> callbackLogic = new J2ERenewSessionCallbackLogic();

    private Config config;

    private String defaultUrl;

    private Boolean multiProfile;

    private Boolean renewSession;

    private SpringSecuritySessionStore internalSessionStore = new SpringSecuritySessionStore();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {

        final J2EContext context = new J2EContext((HttpServletRequest) req, (HttpServletResponse) resp, internalSessionStore);
        callbackLogic.perform(context, config, J2ENopHttpActionAdapter.INSTANCE, this.defaultUrl, this.multiProfile, this.renewSession);
    }

    @Override
    public void destroy() { }

    public CallbackLogic<Object, J2EContext> getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic<Object, J2EContext> callbackLogic) {
        this.callbackLogic = callbackLogic;
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

    public Boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(final Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public Boolean getRenewSession() {
        return renewSession;
    }

    public void setRenewSession(final Boolean renewSession) {
        this.renewSession = renewSession;
    }

    public SpringSecuritySessionStore getInternalSessionStore() {
        return internalSessionStore;
    }

    public void setInternalSessionStore(final SpringSecuritySessionStore internalSessionStore) {
        this.internalSessionStore = internalSessionStore;
    }
}
