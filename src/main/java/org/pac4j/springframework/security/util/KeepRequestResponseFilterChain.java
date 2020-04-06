package org.pac4j.springframework.security.util;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Keep the request and response from the filter chain.
 *
 * @author Jerome LELEU
 * @since 5.1.0
 */
public class KeepRequestResponseFilterChain implements FilterChain {

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp) throws IOException, ServletException {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) resp;
    }

    public boolean hasRequest() {
        return request != null;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public boolean hasResponse() {
        return response != null;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
