package org.pac4j.springframework.security.authentication

import org.pac4j.core.credentials.Credentials
import org.pac4j.core.profile.UserProfile
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import spock.lang.Specification

class ClientProviderManagerSpec extends Specification {

    AuthenticationProvider providerMock
    AuthenticationManager parentAuthenticationManagerMock
    AuthenticationEventPublisher eventPublisherMock

    void setup() {
        providerMock = Mock(AuthenticationProvider)
        parentAuthenticationManagerMock = Mock(AuthenticationManager)
        eventPublisherMock = Mock(AuthenticationEventPublisher)
    }

    void 'a valid ClientAuthenticationToken is authenticated by a provider'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        result instanceof ClientAuthenticationToken
        ((ClientAuthenticationToken) result).userDetails
        ((ClientAuthenticationToken) result).userProfile
        ((ClientAuthenticationToken) result).clientName
    }

    void 'a valid and authenticated ClientAuthenticationToken gets credentials erased if flag is set true'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        !result.getCredentials()
    }

    void 'a valid and authenticated ClientAuthenticationToken does not erase credentials if flag is set false'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set, null, false)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        result.getCredentials()
    }

    void 'attempt at authentication causes an AccountStatusException'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set, null, false)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> {
            throw new CredentialsExpiredException("test")
        }
        thrown(AccountStatusException)
        1 * eventPublisherMock.publishAuthenticationFailure(_ as AccountStatusException, _ as Authentication)
        0 * _
    }

    void 'attempt at authentication causes an InternalAuthenticationServiceException'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set, null, false)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> {
            throw new InternalAuthenticationServiceException("test")
        }
        thrown(InternalAuthenticationServiceException)
        1 * eventPublisherMock.publishAuthenticationFailure(_ as InternalAuthenticationServiceException,
                                                            _ as Authentication)
        0 * _
    }

    void 'attempt at authentication causes an unexpected AuthenticationException'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set, null, false)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> {
            throw new  SessionAuthenticationException("test")
        }
        thrown(SessionAuthenticationException)
        1 * eventPublisherMock.publishAuthenticationFailure(_ as SessionAuthenticationException, _ as Authentication)
        0 * _
    }

    void 'successful auth via parent manager when no valid providers are given to ClientAuthenticationProvider'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> false
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        result instanceof ClientAuthenticationToken
        ((ClientAuthenticationToken) result).userDetails
        ((ClientAuthenticationToken) result).userProfile
        ((ClientAuthenticationToken) result).clientName
    }

    void 'successful auth via parent manager when no providers are given to ClientAuthenticationProvider'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        result instanceof ClientAuthenticationToken
        ((ClientAuthenticationToken) result).userDetails
        ((ClientAuthenticationToken) result).userProfile
        ((ClientAuthenticationToken) result).clientName
    }

    void 'successful auth via parent manager after unsuccessful provider auth with no exceptions'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> null
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        result instanceof ClientAuthenticationToken
        ((ClientAuthenticationToken) result).userDetails
        ((ClientAuthenticationToken) result).userProfile
        ((ClientAuthenticationToken) result).clientName
    }

    void 'successful auth via parent manager after unsuccessful provider auth with exception'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(buildClientToken())

        then:
        1 * providerMock.supports(ClientAuthenticationToken) >> true
        1 * providerMock.authenticate(_ as Authentication) >> {
            throw new RememberMeAuthenticationException("test")
        }
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> buildClientToken()
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        0 * _
        result instanceof ClientAuthenticationToken
        ((ClientAuthenticationToken) result).userDetails
        ((ClientAuthenticationToken) result).userProfile
        ((ClientAuthenticationToken) result).clientName
    }

    void 'auth via parent manager that causes a ProviderNotFoundException'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        manager.authenticate(buildClientToken())

        then:
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> {
            throw new ProviderNotFoundException("test")
        }
        1 * eventPublisherMock.publishAuthenticationFailure(_ as ProviderNotFoundException, _ as Authentication)
        0 * _
        thrown(ProviderNotFoundException)
    }

    void 'auth via parent manager that causes unexpected AuthenticationException'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        manager.authenticate(buildClientToken())

        then:
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> {
            throw new InsufficientAuthenticationException("test")
        }
        1 * eventPublisherMock.publishAuthenticationFailure(_ as InsufficientAuthenticationException,
                                                            _ as Authentication)
        0 * _
        thrown(InsufficientAuthenticationException)
    }

    void 'auth via parent manager that causes a ProviderNotFoundException when auth fails but no exception thrown'() {
        given:
        ClientProviderManager manager = new ClientProviderManager([] as Set,
                parentAuthenticationManagerMock)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        manager.authenticate(buildClientToken())

        then:
        1 * parentAuthenticationManagerMock.authenticate(_ as Authentication) >> null
        1 * eventPublisherMock.publishAuthenticationFailure(_ as ProviderNotFoundException, _ as Authentication)
        0 * _
        thrown(ProviderNotFoundException)
    }

    void 'a valid non-client token is authenticated by a provider'() {
        given:
        UsernamePasswordAuthenticationToken sourceTokenMock = Mock(UsernamePasswordAuthenticationToken)
        UsernamePasswordAuthenticationToken destTokenMock = Mock(UsernamePasswordAuthenticationToken)
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set)
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(sourceTokenMock)

        then:
        1 * providerMock.supports(_) >> true
        1 * providerMock.authenticate(_ as Authentication) >> destTokenMock
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        1 * destTokenMock.getDetails()
        1 * sourceTokenMock.getDetails() >> {
            return Stub(UserDetails)
        }
        1 * destTokenMock.setDetails(_ as UserDetails)
        1 * destTokenMock.eraseCredentials()
        0 * _
        result instanceof UsernamePasswordAuthenticationToken
    }

    void 'a valid, authenticated, non-client token does not erase credentials if flag is set false'() {
        given:
        UsernamePasswordAuthenticationToken sourceTokenMock = Mock(UsernamePasswordAuthenticationToken)
        UsernamePasswordAuthenticationToken destTokenMock = Mock(UsernamePasswordAuthenticationToken)
        ClientProviderManager manager = new ClientProviderManager([providerMock] as Set,
                null,
                false
                                                                  )
        manager.setAuthenticationEventPublisher(eventPublisherMock)

        when:
        Authentication result = manager.authenticate(sourceTokenMock)

        then:
        1 * providerMock.supports(_) >> true
        1 * providerMock.authenticate(_ as Authentication) >> destTokenMock
        1 * eventPublisherMock.publishAuthenticationSuccess(_ as Authentication)
        1 * destTokenMock.getDetails()
        1 * sourceTokenMock.getDetails() >> {
            return Stub(UserDetails)
        }
        1 * destTokenMock.setDetails(_ as UserDetails)
        0 * _
        result instanceof UsernamePasswordAuthenticationToken
    }

    private static ClientAuthenticationToken buildClientToken() {
        return new ClientAuthenticationToken (
                buildClientCredentials(),
                'testClient',
                buildUserProfile(),
                [],
                buildUserDetails()
        )
    }

    private static Credentials buildClientCredentials() {
        return new TestCredentials()
    }

    private static UserProfile buildUserProfile() {
        return new UserProfile()
    }

    private static UserDetails buildUserDetails() {
        return new TestUserDetails()
    }
}
