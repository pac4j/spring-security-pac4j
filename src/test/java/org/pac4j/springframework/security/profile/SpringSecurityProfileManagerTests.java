package org.pac4j.springframework.security.profile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpringSecurityProfileManagerTests {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "pac4j_"})
    void backChannelLogoutClearsTrackedSessionWithoutCreatingRequestSession(final String prefix) {
        final var request = new MockHttpServletRequest();
        logoutTrackedSession(request, prefix);
        assertNull(request.getSession(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "pac4j_"})
    void backChannelLogoutPreservesUnrelatedRequestSession(final String prefix) {
        final var request = new MockHttpServletRequest();
        final var unrelatedSession = new MockHttpSession();
        final var unrelatedContext = new SecurityContextImpl();
        unrelatedSession.setAttribute(SpringSecurityProfileManager.SPRING_SECURITY_CONTEXT_KEY, unrelatedContext);
        request.setSession(unrelatedSession);

        logoutTrackedSession(request, prefix);

        assertSame(unrelatedContext,
            unrelatedSession.getAttribute(SpringSecurityProfileManager.SPRING_SECURITY_CONTEXT_KEY));
    }

    private void logoutTrackedSession(final MockHttpServletRequest logoutRequest, final String prefix) {
        final var loginRequest = new MockHttpServletRequest();
        final var loginContext = new JEEContext(loginRequest, new MockHttpServletResponse());
        final var store = new JEESessionStore();
        store.setPrefix(prefix);
        final var manager = new SpringSecurityProfileManager(loginContext, store);
        final var profile = new BasicUserProfile();
        profile.setId("alice");
        manager.save(true, profile, false);

        final var trackedSession = loginRequest.getSession(false);
        assertNotNull(trackedSession);
        final var savedContext = (SecurityContext)
            trackedSession.getAttribute(SpringSecurityProfileManager.SPRING_SECURITY_CONTEXT_KEY);
        assertNotNull(savedContext);
        assertTrue(savedContext.getAuthentication().isAuthenticated());

        final var logoutContext = new JEEContext(logoutRequest, new MockHttpServletResponse());
        final var rebuiltStore = store.buildFromTrackableSession(logoutContext, trackedSession).orElseThrow();
        new SpringSecurityProfileManager(logoutContext, rebuiltStore).removeProfiles();

        assertNull(trackedSession.getAttribute(SpringSecurityProfileManager.SPRING_SECURITY_CONTEXT_KEY));
        assertTrue(((Map<?, ?>) trackedSession.getAttribute(prefix + Pac4jConstants.USER_PROFILES)).isEmpty());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void logoutWithoutSessionDoesNotCreateOne() {
        final var request = new MockHttpServletRequest();
        final var context = new JEEContext(request, new MockHttpServletResponse());

        new SpringSecurityProfileManager(context, new JEESessionStore()).removeProfiles();

        assertNull(request.getSession(false));
    }
}
