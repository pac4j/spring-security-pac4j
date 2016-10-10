package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.J2ERenewSessionCallbackLogic;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isBlank;

/**
 * <p>This filter finishes the login process for an indirect client, based on the {@link #callbackLogic}.</p>
 *
 * <p>The configuration can be provided via setter methods: {@link #setConfig(Config)} (security configuration), {@link #setDefaultUrl(String)} (default url after login if none was requested),
 * {@link #setMultiProfile(Boolean)} (whether multiple profiles should be kept) and ({@link #setRenewSession(Boolean)} (whether the session must be renewed after login).</p>
 *
 * <p>This filter only applies if the suffix is blank or if the current request URL ends with the suffix (by default: <code>/callback</code>).</p>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CallbackFilter implements Filter {

    public final static String DEFAULT_CALLBACK_SUFFIX = "/callback";

    private final static Logger logger = LoggerFactory.getLogger(CallbackFilter.class);

    private CallbackLogic<Object, J2EContext> callbackLogic;

    private Config config;

    private String defaultUrl;

    private Boolean multiProfile;

    private Boolean renewSession;

    private String suffix = DEFAULT_CALLBACK_SUFFIX;

    public CallbackFilter() {
        callbackLogic = new J2ERenewSessionCallbackLogic<>();
        ((J2ERenewSessionCallbackLogic<J2EContext>) callbackLogic).setProfileManagerFactory(SpringSecurityProfileManager::new);
    }

    public CallbackFilter(final Config config) {
        this();
        this.config = config;
    }

    public CallbackFilter(final Config config, final String defaultUrl) {
        this(config);
        this.defaultUrl = defaultUrl;
    }

    public CallbackFilter(final Config config, final String defaultUrl, final boolean multiProfile) {
        this(config, defaultUrl);
        this.multiProfile = multiProfile;
    }

    public CallbackFilter(final Config config, final String defaultUrl, final boolean multiProfile, final boolean renewSession) {
        this(config, defaultUrl, multiProfile);
        this.renewSession = renewSession;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {

        assertNotNull("config", this.config);
        final J2EContext context = new J2EContext((HttpServletRequest) req, (HttpServletResponse) resp, config.getSessionStore());

        final String path = context.getPath();
        logger.debug("path: {} | suffix: {}", path, suffix);
        final boolean pathEndsWithSuffix = path != null && path.endsWith(suffix);

        if (isBlank(suffix) || pathEndsWithSuffix) {
            assertNotNull("callbackLogic", this.callbackLogic);
            callbackLogic.perform(context, this.config, (code, ctx) -> null, this.defaultUrl, this.multiProfile, this.renewSession);
        } else {
            chain.doFilter(req, resp);
        }
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

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
}
