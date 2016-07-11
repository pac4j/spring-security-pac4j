package org.pac4j.springframework.security.web;

import org.pac4j.core.exception.TechnicalException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * This entry point redirects returns an error if called.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class Pac4jEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {

        throw new TechnicalException("The pac4j entry point should never be called: you must define a pac4j 'SecurityFilter' or the pac4j 'CallbackFilter' in the corresponding security:http section");
    }
}
