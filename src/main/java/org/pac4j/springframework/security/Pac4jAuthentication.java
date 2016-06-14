package org.pac4j.springframework.security;

import org.pac4j.core.profile.AbstractProfileManager;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

/**
 * Pac4j authentication containing the user profiles.
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class Pac4jAuthentication<U extends CommonProfile> extends AbstractProfileManager<U> implements Authentication {

    private final LinkedHashMap<String, U> profiles;

    public Pac4jAuthentication(final LinkedHashMap<String, U> profiles) {
        super(null);
        this.profiles = profiles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final List<U> profiles = getProfiles();
        for (final U profile : profiles) {
            final Set<String> roles = profile.getRoles();
            for (final String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorities;
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
    public boolean isAuthenticated() {
        final Optional<U> profile = retrieve(true);
        return profile.isPresent() && !(profile.get() instanceof AnonymousProfile);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot call setAuthenticated on Pac4jAuthentication");
    }
}
