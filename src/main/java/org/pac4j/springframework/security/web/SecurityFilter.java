package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>This filter protects an url.</p>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SecurityFilter implements Filter {

    static {
        Config.setProfileManagerFactory("SpringSecurityProfileManager", ctx -> new SpringSecurityProfileManager(ctx));
    }

    private SecurityLogic<Object, JEEContext> securityLogic;

    private Config config;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    public SecurityFilter() {}

    public SecurityFilter(final Config config) {
        this.config = config;
    }

    public SecurityFilter(final Config config, final String clients) {
        this(config);
        this.clients = clients;
    }

    public SecurityFilter(final Config config, final String clients, final String authorizers) {
        this(config, clients);
        this.authorizers = authorizers;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain filterChain) throws IOException, ServletException {

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final SecurityLogic<Object, JEEContext> bestLogic = FindBest.securityLogic(securityLogic, config, DefaultSecurityLogic.INSTANCE);

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        final JEEContext context = new JEEContext(request, response, bestSessionStore);
        bestLogic.perform(context, this.config, (ctx, profiles, parameters) -> {

            filterChain.doFilter(request, response);
            return null;

        }, bestAdapter, this.clients, this.authorizers, this.matchers, this.multiProfile);
    }

    @Override
    public void destroy() { }

    public SecurityLogic<Object, JEEContext> getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(final SecurityLogic<Object, JEEContext> securityLogic) {
        this.securityLogic = securityLogic;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getClients() {
        return clients;
    }

    public void setClients(final String clients) {
        this.clients = clients;
    }

    public String getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(final String authorizers) {
        this.authorizers = authorizers;
    }

    public String getMatchers() {
        return matchers;
    }

    public void setMatchers(final String matchers) {
        this.matchers = matchers;
    }

    public Boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(final Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }
}
