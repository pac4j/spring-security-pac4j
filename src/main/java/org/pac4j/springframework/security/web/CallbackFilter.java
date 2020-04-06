package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.pac4j.springframework.security.util.KeepActionHttpActionAdapter;
import org.pac4j.springframework.security.util.KeepRequestResponseFilterChain;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This filter finishes the login process for an indirect client.</p>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CallbackFilter extends AbstractPathFilter {

    public final static String DEFAULT_CALLBACK_SUFFIX = "/callback";

    private CallbackLogic<Object, JEEContext> callbackLogic;

    private Config config;

    private String defaultUrl;

    private Boolean saveInSession;

    private Boolean multiProfile;

    private Boolean renewSession;

    private String defaultClient;

    private ApplicationContext applicationContext;

    private List<Filter> additionalFilters = new ArrayList<>();

    public CallbackFilter() {
        setSuffix(DEFAULT_CALLBACK_SUFFIX);
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

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        init();

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final KeepActionHttpActionAdapter keepActionHttpActionAdapter = new KeepActionHttpActionAdapter();
        final CallbackLogic<Object, JEEContext> bestLogic = FindBest.callbackLogic(callbackLogic, config, DefaultCallbackLogic.INSTANCE);

        final JEEContext context = new JEEContext(request, response, bestSessionStore);
        final boolean mustApply = mustApply(context);
        if (mustApply) {
            bestLogic.perform(context, this.config, keepActionHttpActionAdapter, this.defaultUrl, this.saveInSession,
                    this.multiProfile, this.renewSession, this.defaultClient);

            HttpServletRequest newRequest = request;
            HttpServletResponse newResponse = response;
            for (final Filter additionalFilter : additionalFilters) {
                final KeepRequestResponseFilterChain filterChain = new KeepRequestResponseFilterChain();
                additionalFilter.doFilter(newRequest, newResponse, filterChain);
                if (filterChain.hasRequest()) {
                    newRequest = filterChain.getRequest();
                }
                if (filterChain.hasResponse()) {
                    newResponse = filterChain.getResponse();
                }
            }

            final JEEContext newContext = new JEEContext(newRequest, newResponse, bestSessionStore);
            final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
            bestAdapter.adapt(keepActionHttpActionAdapter.getAction(), newContext);
        } else {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() { }

    @Override
    protected void internalInit() {
        if (applicationContext != null) {
            final FilterChainProxy springSecurityFilterChain = (FilterChainProxy) applicationContext.getBean("springSecurityFilterChain");
            if (springSecurityFilterChain != null) {
                final List<SecurityFilterChain> chains = springSecurityFilterChain.getFilterChains();
                for (final SecurityFilterChain chain : chains) {
                    final List<Filter> filters = chain.getFilters();
                    boolean process = false;
                    for (final Filter filter : filters) {
                        if (process) {
                            additionalFilters.add(filter);
                        } else if (filter == this) {
                            process = true;
                        }
                    }
                    if (process) {
                        break;
                    }
                }
            }
        }
    }

    public CallbackLogic<Object, JEEContext> getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic<Object, JEEContext> callbackLogic) {
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

    public Boolean getSaveInSession() {
        return saveInSession;
    }

    public void setSaveInSession(final Boolean saveInSession) {
        this.saveInSession = saveInSession;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(final String defaultClient) {
        this.defaultClient = defaultClient;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
