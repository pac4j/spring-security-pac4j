package org.pac4j.springframework.config;

import java.io.IOException;
import java.io.OutputStream;

import org.pac4j.core.io.WritableResource;
import org.springframework.core.io.Resource;

/**
 * A wrapper that adapts a spring {@link org.springframework.core.io.WritableResource} to a pac4j {@link WritableResource}.
 * 
 * @author Keith Garry Boyce
 * @since 1.4.3
 */
public class WritableResourceWrapper extends ResourceWrapper implements WritableResource {

	public WritableResourceWrapper(Resource springResource) {
		super(springResource);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return ((org.springframework.core.io.WritableResource) springResource).getOutputStream();
	}

}
