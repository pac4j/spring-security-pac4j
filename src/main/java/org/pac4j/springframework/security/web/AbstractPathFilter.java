package org.pac4j.springframework.security.web;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;

import static org.pac4j.core.util.CommonHelper.isBlank;

/**
 * <p>This filter only applies for a specific suffix path if defined or every time otherwise.</p>
 *
 * @author Jerome Leleu
 * @since 4.1.0
 */
public abstract class AbstractPathFilter extends InitializableObject implements Filter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String suffix;

    protected boolean mustApply(final WebContext context) {
        final String path = context.getPath();
        logger.debug("path: {} | suffix: {}", path, suffix);

        if (isBlank(suffix)) {
            return true;
        } else {
            return path != null && path.endsWith(suffix);
        }
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
}
