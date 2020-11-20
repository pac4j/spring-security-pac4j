package org.pac4j.springframework.security.util;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;

/**
 * Keep the requested action.
 *
 * @author Jerome LELEU
 * @since 5.1.0
 */
public class KeepActionHttpActionAdapter implements HttpActionAdapter {

    private HttpAction action;

    @Override
    public Object adapt(final HttpAction action, final WebContext context) {
        this.action = action;
        return null;
    }

    public HttpAction getAction() {
        return this.action;
    }
}
