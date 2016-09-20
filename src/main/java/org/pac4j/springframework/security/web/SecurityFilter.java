package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>This filter protects an url, based on the {@link #securityLogic}.</p>
 *
 * <p>The configuration can be provided via setter methods: {@link #setConfig(Config)} (security configuration), {@link #setClients(String)} (list of clients for authentication),
 * {@link #setAuthorizers(String)} (list of authorizers), {@link #setMatchers(String)} (list of matchers) and {@link #setMultiProfile(Boolean)} (whether multiple profiles should be kept).</p>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SecurityFilter implements Filter {

    private DefaultSecurityLogic<Object, J2EContext> securityLogic;

    private Config config;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    public SecurityFilter() {
        securityLogic = new DefaultSecurityLogic<>();
        ((DefaultSecurityLogic<Object, J2EContext>) securityLogic).setProfileManagerFactory(SpringSecurityProfileManager::new);
    }

    public SecurityFilter(final Config config) {
        this();
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

        assertNotNull("securityLogic", this.securityLogic);
        assertNotNull("config", this.config);

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        final J2EContext context = new J2EContext(request, response, config.getSessionStore());

        securityLogic.perform(context, this.config, (ctx, parameters) -> {

            filterChain.doFilter(request, response);
            return null;

        }, (code, ctx) -> null, this.clients, this.authorizers, this.matchers, this.multiProfile);
    }

    @Override
    public void destroy() { }

    public DefaultSecurityLogic<Object, J2EContext> getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(final DefaultSecurityLogic<Object, J2EContext> securityLogic) {
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
