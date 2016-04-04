package org.pac4j.springframework.config;

import java.io.IOException;
import java.io.InputStream;

import org.pac4j.core.io.Resource;

/**
 * A wrapper that adapts a spring {@link org.springframework.core.io.Resource} to a pac4j {@link Resource}.
 * 
 * @author Keith Garry Boyce
 * @since 1.4.3
 */
public class ResourceWrapper implements Resource {
	protected org.springframework.core.io.Resource springResource;

	public ResourceWrapper(org.springframework.core.io.Resource springResource) {
		this.springResource = springResource;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return springResource.getInputStream();
	}

	@Override
	public boolean exists() {
		return springResource.exists();
	}

	@Override
	public String getFilename() {
		return springResource.getFilename();
	}

}
