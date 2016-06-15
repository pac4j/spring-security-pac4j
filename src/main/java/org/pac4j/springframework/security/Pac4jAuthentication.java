package org.pac4j.springframework.security;

import org.pac4j.core.profile.AbstractProfileManager;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

/**
 * Pac4j authentication containing the user profiles.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jAuthentication<U extends CommonProfile> extends AbstractProfileManager<U> implements Authentication {

    private final LinkedHashMap<String, U> profiles;

    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public Pac4jAuthentication(final LinkedHashMap<String, U> profiles) {
        super(null);
        this.profiles = profiles;
        final List<U> listProfiles = getProfiles();
        for (final U profile : listProfiles) {
            final Set<String> roles = profile.getRoles();
            for (final String role : roles) {
                this.authorities.add(new SimpleGrantedAuthority(role));
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getName() {
        final Optional<U> profile = retrieve(true);
        if (profile.isPresent()) {
            return profile.get().getId();
        } else {
            return null;
        }
    }

    /**
     * Return the first Pac4j user profile.
     *
     * @return the first user profile
     */
    public Optional<U> getProfile() {
        return retrieve(false);
    }

    /**
     * Return all Pac4j user profiles.
     *
     * @return all user profiles
     */
    public List<U> getProfiles() {
        return retrieveAll(false);
    }

    @Override
    protected LinkedHashMap<String, U> retrieveAllAsLinkedMap(final boolean readFromSession) {
        return this.profiles;
    }

    /**
     * Return the user profiles as a linked hash map.
     *
     * @return the user profiles.
     */
    protected LinkedHashMap<String, U> getLinkedMapProfiles() {
        return this.profiles;
    }

    @Override
    public Object getPrincipal() {
        return getProfile();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot call setAuthenticated on Pac4jAuthentication");
    }
}
