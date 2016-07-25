package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.springframework.security.context.SpringSecurityContext;

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

    private DefaultSecurityLogic<Object, SpringSecurityContext> securityLogic = new DefaultSecurityLogic<>();

    private Config config;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain filterChain) throws IOException, ServletException {

        assertNotNull("securityLogic", this.securityLogic);
        assertNotNull("config", this.config);

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        final SpringSecurityContext context = new SpringSecurityContext(request, response, config.getSessionStore());

        securityLogic.perform(context, getConfig(), (ctx, parameters) -> {

            filterChain.doFilter(request, response);
            return null;

        }, (code, ctx) -> null, this.clients, this.authorizers, this.matchers, this.multiProfile);
    }

    @Override
    public void destroy() { }

    public DefaultSecurityLogic<Object, SpringSecurityContext> getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(final DefaultSecurityLogic<Object, SpringSecurityContext> securityLogic) {
        this.securityLogic = securityLogic;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
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
