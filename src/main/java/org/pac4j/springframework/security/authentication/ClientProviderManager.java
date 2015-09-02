package org.pac4j.springframework.security.authentication;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

/**
 * Modeled after Spring Security's {@link ProviderManager}. Provides an implementation of
 * {@link AuthenticationManager} conducive (but not exclusive) to Pac4j authentication
 * mechanisms.
 *
 * @author Jacob Severson
 * @since  1.3.0
 */
public class ClientProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {

    private final Set<AuthenticationProvider> providers;
    private final AuthenticationManager parent;
    private final boolean eraseCredentialsAfterAuthentication;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();

    /**
     * Constructs a ClientProviderManager with no parent {@link AuthenticationManager} erasing of
     * credentials after successful authentication is turned on.
     *
     * @param providers providers used to attempt authentication
     */
    public ClientProviderManager(Set<AuthenticationProvider> providers) {
        this(providers, null);
    }

    /**
     * Constructs a ClientProviderManager with a parent {@link AuthenticationManager}. Authentication
     * will be delegated to the parent if the incoming {@link Authentication} is not compatible with
     * any of the providers or if the compatible provider failed. Erasing of credentials after successful
     * authentication is turned on.
     *
     * @param providers providers used to attempt authentication
     * @param parent    parent AuthenticationManager to try authentication upon an unsuccessful attempt by this one
     */
    public ClientProviderManager(Set<AuthenticationProvider> providers,
                                 AuthenticationManager parent) {
        this(providers, parent, true);
    }

    /**
     * Constructs a ClientProviderManager and allows control over erasing of credentials. Upon successful
     * authentication if eraseCredentialsAfterAuthentication is set to true, the "credentials" held in
     * the authenticated {@link Authentication} object are removed before being placed into the
     * security context.
     *
     * @param providers                             providers used to attempt authentication
     * @param parent                                parent AuthenticationManager to try authentication upon an
     *                                              unsuccessful attempt by this one
     * @param eraseCredentialsAfterAuthentication   dictates whether the credentials of the authenticated object
     *                                              are removed
     */
    public ClientProviderManager(Set<AuthenticationProvider> providers,
                                 AuthenticationManager parent,
                                 boolean eraseCredentialsAfterAuthentication) {
        this.providers = providers;
        this.parent = parent;
        this.eraseCredentialsAfterAuthentication = eraseCredentialsAfterAuthentication;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationException lastException = null;
        Authentication result = null;

        for (AuthenticationProvider provider : providers) {
            if (!provider.supports(authentication.getClass())) {
                continue;
            }

            try {
                result = provider.authenticate(authentication);

                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            }
            catch (AccountStatusException e) {
                eventPublisher.publishAuthenticationFailure(e, authentication);
                throw e;
            }
            catch (InternalAuthenticationServiceException e) {
                eventPublisher.publishAuthenticationFailure(e, authentication);
                throw e;
            }
            catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (result == null && parent != null) {
            try {
                result = parent.authenticate(authentication);
            }
            catch (ProviderNotFoundException e) {
                // No-op. If authentication with this manager failed
                // with an exception it could get squashed by this one
                // even though we may expect this one to be thrown. If this
                // exception is the ultimate culprit it will get thrown at the end
                // of this method.
            }
            catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (result != null) {
            if (eraseCredentialsAfterAuthentication) {
                result = eraseCredentials(result);
            }
            eventPublisher.publishAuthenticationSuccess(result);
            return result;
        }

        if (lastException == null) {
            lastException = new ProviderNotFoundException(messages.getMessage(
                    "ProviderManager.providerNotFound",
                    new Object[] { authentication.getName() },
                    "No AuthenticationProvider found for {0}"));
        }

        eventPublisher.publishAuthenticationFailure(lastException, authentication);
        throw lastException;
    }

    /**
     * Copies details of the authentication request into the authenticated object.
     *
     * @param source    Authentication created from a request to authenticate
     * @param dest      Authentication created by successful authentication
     *
     */
    private void copyDetails(Authentication source, Authentication dest) {

        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

            token.setDetails(source.getDetails());
        }
    }

    /**
     * Erases the credentials of the {@link Authentication} object depending on the
     * subtype. This method takes into account the {@link ClientAuthenticationToken}
     * type which cannot have credentials erased in the default {@link ProviderManager}.
     *
     * @param authentication    authenticated Authentication object
     * @return                  the Authentication object with erased credentials
     */
    private Authentication eraseCredentials(Authentication authentication) {

        if (authentication instanceof ClientAuthenticationToken) {
            ClientAuthenticationToken clientAuthenticationToken = ((ClientAuthenticationToken)authentication);
            return new ClientAuthenticationToken (
                    null,
                    clientAuthenticationToken.getClientName(),
                    clientAuthenticationToken.getUserProfile(),
                    clientAuthenticationToken.getAuthorities(),
                    clientAuthenticationToken.getUserDetails()
            );

        } else if (authentication instanceof CredentialsContainer) {
            ((AbstractAuthenticationToken) authentication).eraseCredentials();
            return authentication;

        }

        // Not expecting this, but just in case it is an implementation that
        // is neither Pac4j specific nor of type CredentialsContainer
        return authentication;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (parent == null && providers.isEmpty()) {
            throw new IllegalArgumentException("At least one parent AuthenticationManager or " +
                    "AuthenticationProvider is required.");
        }
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAuthenticationEventPublisher(AuthenticationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    private static final class NullEventPublisher implements AuthenticationEventPublisher {
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {}
        public void publishAuthenticationSuccess(Authentication authentication) {}
    }
}
