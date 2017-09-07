<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

The `spring-security-pac4j` project is an **easy and powerful security library for Spring Security** (with or without Spring Boot) web applications. It supports authentication and authorization, but also advanced features like session fixation and CSRF protection.
It's based on Java 8, Spring Security 4.2 and on the **[pac4j security engine](https://github.com/pac4j/pac4j) v2**. It's available under the Apache 2 license.

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - OpenID - Google App Engine - Kerberos - LDAP - SQL - JWT - MongoDB - CouchDB - IP address - REST API

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles/permissions - Anonymous/remember-me/(fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) The `SecurityFilter` protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

4) The `CallbackFilter` finishes the login process for an indirect client

5) The `LogoutFilter` handles the logout process.


Just follow these easy steps to secure your Spring Security web application:

### 1) Add the required dependencies (`spring-security-pac4j` + `pac4j-*` libraries)

You need to add a dependency on:
 
- the `spring-security-pac4j` library (<em>groupId</em>: **org.pac4j**, *version*: **3.0.0**)
- the appropriate `pac4j` [submodules](http://www.pac4j.org/docs/clients.html) (<em>groupId</em>: **org.pac4j**, *version*: **2.1.0**): `pac4j-oauth` for OAuth support (Facebook, Twitter...), `pac4j-cas` for CAS support, `pac4j-ldap` for LDAP authentication, etc.

All released artifacts are available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j).

---

### 2) Define the configuration (`Config` + `Client` + `Authorizer`)

The configuration (`org.pac4j.core.config.Config`) contains all the clients and authorizers required by the application to handle security.

It can be defined in the `securityContext.xml` file:

```xml
<bean id="roleAdminAuthGenerator" class="org.pac4j.demo.spring.RoleAdminAuthGenerator" />

<bean id="googleConfigurationClient" class="org.pac4j.oidc.config.OidcConfiguration">
    <property name="clientId" value="167480702619-8e1lo80dnu8bpk3k0lvvj27noin97vu9.apps.googleusercontent.com" />
    <property name="secret" value="MhMme_Ik6IH2JMnAT6MFIfee" />
</bean>
<bean id="googleOidcClient" class="org.pac4j.oidc.client.GoogleOidcClient">
    <constructor-arg ref="googleConfigurationClient"/>
    <property name="authorizationGenerator" ref="roleAdminAuthGenerator" />
</bean>

<bean id="mitreidConfigurationClient" class="org.pac4j.oidc.config.OidcConfiguration">
    <property name="clientAuthenticationMethod" value="NONE"/>
    <property name="clientId" value="21c0da5f-40f6-4ff0-bfdb-6803d3b8cee3" />
    <property name="secret" value="AKJsHgTaQt71BgXb0apln934_vs0aSeczucxG0H5-RvDOt199fDEDTCxWF3XWDDOsTAtlectw429LEizC0wcKTI" />
    <property name="scope" value="address phone openid email profile" />
    <property name="discoveryURI" value="https://mitreid.org/.well-known/openid-configuration" />
    <property name="preferredJwsAlgorithm" value="RS256"/>
</bean>
<bean id="mitreIdClient" class="org.pac4j.oidc.client.OidcClient">
    <constructor-arg ref="mitreidConfigurationClient"/>
</bean>

<bean id="samlConfig" class="org.pac4j.saml.client.SAML2ClientConfiguration">
    <property name="keystorePath" value="resource:samlKeystore.jks" />
    <property name="keystorePassword" value="pac4j-demo-passwd" />
    <property name="privateKeyPassword" value="pac4j-demo-passwd" />
    <property name="identityProviderMetadataPath" value="resource:metadata-okta.xml" />
    <property name="maximumAuthenticationLifetime" value="3600" />
    <property name="serviceProviderEntityId" value="http://localhost:8080/callback?client_name=SAML2Client" />
    <property name="serviceProviderMetadataPath" value="sp-metadata.xml" />
</bean>
<bean id="samlClient" class="org.pac4j.saml.client.SAML2Client">
    <constructor-arg name="configuration" ref="samlConfig" />
</bean>

<bean id="googleClient" class="org.pac4j.oauth.client.Google2Client">
    <property name="key" value="167480702619-0k2ikl9v3ph44u6i6hid1b160v4fggua.apps.googleusercontent.com" />
    <property name="secret" value="i1jYBya-bxIyEqkLw7MaJ12A" />
</bean>
<bean id="facebookClient" class="org.pac4j.oauth.client.FacebookClient">
    <property name="key" value="145278422258960" />
    <property name="secret" value="be21409ba8f39b5dae2a7de525484da8" />
</bean>
<bean id="twitterClient" class="org.pac4j.oauth.client.TwitterClient">
    <property name="key" value="CoxUiYwQOSFDReZYdjigBA" />
    <property name="secret" value="2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs" />
</bean>

<bean id="usernamePasswordAuthenticator" class="org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator" />

<bean id="formClient" class="org.pac4j.http.client.indirect.FormClient">
    <property name="loginUrl" value="http://localhost:8080/loginForm.jsp" />
    <property name="authenticator" ref="usernamePasswordAuthenticator" />
</bean>
<bean id="indirectBasicAuthClient" class="org.pac4j.http.client.indirect.IndirectBasicAuthClient">
    <property name="authenticator" ref="usernamePasswordAuthenticator" />
</bean>

<bean id="casConfigurationClient" class="org.pac4j.cas.config.CasConfiguration">
    <constructor-arg value="https://casserverpac4j.herokuapp.com/login"/>
</bean>
<bean id="casClient" class="org.pac4j.cas.client.CasClient">
    <constructor-arg ref="casConfigurationClient"/>
</bean>

<bean id="parameterClient" class="org.pac4j.http.client.direct.ParameterClient">
    <constructor-arg name="parameterName" value="token" />
    <constructor-arg name="tokenAuthenticator">
        <bean class="org.pac4j.jwt.credentials.authenticator.JwtAuthenticator">
            <constructor-arg name="signatureConfiguration">
                <bean class="org.pac4j.jwt.config.signature.SecretSignatureConfiguration">
                    <constructor-arg name="secret" value="#{T(org.pac4j.demo.spring.Constants).JWT_SALT}" />
                </bean>
            </constructor-arg>
        </bean>
    </constructor-arg>
    <property name="supportGetRequest" value="true" />
    <property name="supportPostRequest" value="false" />
</bean>

<bean id="directBasicAuthClient" class="org.pac4j.http.client.direct.DirectBasicAuthClient">
    <constructor-arg name="usernamePasswordAuthenticator" ref="usernamePasswordAuthenticator" />
</bean>

<bean id="anonymousClient" class="org.pac4j.core.client.direct.AnonymousClient" />

<bean id="clients" class="org.pac4j.core.client.Clients">
    <property name="callbackUrl" value="http://localhost:8080/callback" />
    <property name="clients">
        <list>
            <ref bean="googleOidcClient" />
            <ref bean="facebookClient" />
            <ref bean="twitterClient" />
            <ref bean="googleClient" />
            <ref bean="mitreIdClient" />
            <ref bean="formClient" />
            <ref bean="indirectBasicAuthClient" />
            <ref bean="casClient" />
            <ref bean="samlClient" />
            <ref bean="parameterClient" />
            <ref bean="directBasicAuthClient" />
            <ref bean="anonymousClient" />
        </list>
    </property>
</bean>

<bean id="config" class="org.pac4j.core.config.Config">
    <property name="clients" ref="clients" />
    <property name="authorizers">
        <map>
            <entry key="admin">
                <bean class="org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer">
                    <constructor-arg name="roles" value="ROLE_ADMIN" />
                </bean>
            </entry>
            <entry key="custom">
                <bean class="org.pac4j.demo.spring.CustomAuthorizer" />
            </entry>
        </map>
    </property>
    <property name="matchers">
        <map>
            <entry key="excludedPath">
                <bean class="org.pac4j.core.matching.PathMatcher">
                    <constructor-arg name="regexpPath" value="^/facebook/notprotected\.jsp$" />
                </bean>
            </entry>
        </map>
    </property>
</bean>
```

or via a Java configuration class:

```java
@onfiguration
public class Pac4jConfig {

    ...

    @Bean
    public Config config() {
        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("167480702619-8e1lo80dnu8bpk3k0lvvj27noin97vu9.apps.googleusercontent.com");
        oidcConfiguration.setSecret("MhMme_Ik6IH2JMnAT6MFIfee");
        oidcConfiguration.setPreferredJwsAlgorithm(JWSAlgorithm.PS384);
        oidcConfiguration.addCustomParam("prompt", "consent");
        final GoogleOidcClient oidcClient = new GoogleOidcClient(oidcConfiguration);
        oidcClient.setAuthorizationGenerator( (ctx, profile) -> {
            profile.addRole("ROLE_ADMIN");
            return profile;
        });

        final SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks",
                "pac4j-demo-passwd",
                "pac4j-demo-passwd",
                "resource:metadata-okta.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("http://localhost:8080/callback?client_name=SAML2Client");
        cfg.setServiceProviderMetadataPath("sp-metadata.xml");
        final SAML2Client saml2Client = new SAML2Client(cfg);

        final FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
        final TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA",
                "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");

        final SimpleTestUsernamePasswordAuthenticator simpleTestUsernamePasswordAuthenticator = new SimpleTestUsernamePasswordAuthenticator();
        final FormClient formClient = new FormClient("http://localhost:8080/loginForm.jsp", simpleTestUsernamePasswordAuthenticator);
        final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient(simpleTestUsernamePasswordAuthenticator);

        final CasConfiguration casConfiguration = new CasConfiguration("https://casserverpac4j.herokuapp.com/login");
        final CasClient casClient = new CasClient(casConfiguration);

        ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(new SecretSignatureConfiguration(salt), new SecretEncryptionConfiguration(salt)));
        parameterClient.setSupportGetRequest(true);
        parameterClient.setSupportPostRequest(false);

        final DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(simpleTestUsernamePasswordAuthenticator);

        final AnonymousClient anonymousClient = new AnonymousClient();

        final Clients clients = new Clients("http://localhost:8080/callback", oidcClient, saml2Client, facebookClient,
                twitterClient, formClient, indirectBasicAuthClient, casClient, parameterClient, directBasicAuthClient, anonymousClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.html$"));
        return config;
    }
}
```

`http://localhost:8080/callback` is the url of the callback endpoint, which is only necessary for indirect clients.

Notice that you can define specific [matchers](http://www.pac4j.org/docs/matchers.html) via the `matchers` map.

---

### 3) Protect urls (`SecurityFilter`)

You can protect (authentication + authorizations) the urls of your Spring Security application by using the `SecurityFilter` and declaring the filter in the appropriate `security:http` section. It has the following behaviour:

1) If the HTTP request matches the `matchers` configuration (or no `matchers` are defined), the security is applied. Otherwise, the user is automatically granted access.

2) First, if the user is not authenticated (no profile) and if some clients have been defined in the `clients` parameter, a login is tried for the direct clients.

3) Then, if the user has a profile, authorizations are checked according to the `authorizers` configuration. If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed.

4) Finally, if the user is still not authenticated (no profile), he is redirected to the appropriate identity provider if the first defined client is an indirect one in the `clients` configuration. Otherwise, a 401 error page is displayed.


The following parameters are available:

1) `config`: the security configuration previously defined

2) `clients` (optional): the list of client names (separated by commas) used for authentication:
- in all cases, this filter requires the user to be authenticated. Thus, if the `clients` is blank or not defined, the user must have been previously authenticated
- if the `client_name` request parameter is provided, only this client (if it exists in the `clients`) is selected.

3) `authorizers` (optional): the list of authorizer names (separated by commas) used to check authorizations:
- if the `authorizers` is blank or not defined, no authorization is checked
- the following authorizers are available by default (without defining them in the configuration):
  * `isFullyAuthenticated` to check if the user is authenticated but not remembered, `isRemembered` for a remembered user, `isAnonymous` to ensure the user is not authenticated, `isAuthenticated` to ensure the user is authenticated (not necessary by default unless you use the `AnonymousClient`)
  * `hsts` to use the `StrictTransportSecurityHeader` authorizer, `nosniff` for `XContentTypeOptionsHeader`, `noframe` for `XFrameOptionsHeader `, `xssprotection` for `XSSProtectionHeader `, `nocache` for `CacheControlHeader ` or `securityHeaders` for the five previous authorizers
  * `csrfToken` to use the `CsrfTokenGeneratorAuthorizer` with the `DefaultCsrfTokenGenerator` (it generates a CSRF token and saves it as the `pac4jCsrfToken` request attribute and in the `pac4jCsrfToken` cookie), `csrfCheck` to check that this previous token has been sent as the `pac4jCsrfToken` header or parameter in a POST request and `csrf` to use both previous authorizers.

4) `matchers` (optional): the list of matcher names (separated by commas) that the request must satisfy to check authentication/authorizations

5) `multiProfile` (optional): it indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time (`false` by default).

You can define it in the `securityContext.xml` file:

```xml
<security:authentication-manager />
<bean id="noEntryPoint" class="org.pac4j.springframework.security.web.Pac4jEntryPoint" />

<bean id="facebookSecurityFilter" class="org.pac4j.springframework.security.web.SecurityFilter">
    <property name="config" ref="config" />
    <property name="clients" value="FacebookClient" />
</bean>
<security:http create-session="always" pattern="/facebook/**" entry-point-ref="noEntryPoint">
    <security:custom-filter position="BASIC_AUTH_FILTER" ref="facebookSecurityFilter" />
</security:http>
```

Or via Java configuration:

```java
@EnableWebSecurity
public class SecurityConfig {

    ...

    @Autowired
    private Config config;

    protected void configure(final HttpSecurity http) throws Exception {

        final SecurityFilter filter = new SecurityFilter(config, "FacebookClient");
        filter.setMatchers("excludedPath");

        http
               .antMatcher("/facebook/**")
               .addFilterBefore(filter, BasicAuthenticationFilter.class)
               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }
        
    ...
```

You don't need any authentication provider (thus the empty authentication manager).

If you use the `SecurityFilter`, it will handle all the login process and authorization checks for you. So the defined entry point (`noEntryPoint`) of the corresponding `security:http` section should never be called.

You can still perform additional authorization checks with Spring Security AFTER the user has been authenticated and authorized by *pac4j*:

```xml
<security:http create-session="always" pattern="/saml2/**" entry-point-ref="noEntryPoint">
    <security:custom-filter position="BASIC_AUTH_FILTER" ref="samlSecurityFilter" />
    <security:intercept-url pattern="/saml2/admin.jsp" access="hasRole('ADMIN')" />
    <security:intercept-url pattern="/saml2/**" access="isAuthenticated()" />
</security:http>
```

or

```java
http
.antMatcher("/saml/**")
    .authorizeRequests()
        .antMatchers("/saml/admin.html").hasRole("ADMIN")
        .antMatchers("/saml/**").authenticated()
    .and()
    .addFilterBefore(new SecurityFilter(config, "Saml2Client"), BasicAuthenticationFilter.class);
```

If you use the *pac4j* `AnonymousClient` or no `SecurityFilter` at all, the user transmitted to Spring Security will be anonymous and the entry point will be called for protected URLs.
So you can use the `Pac4jEntryPoint` with a specific client to start the login process with the corresponding identity provider:

```xml
<bean id="casEntryPoint" class="org.pac4j.springframework.security.web.Pac4jEntryPoint">
    <property name="config" ref="config" />
    <property name="clientName" value="CasClient" />
</bean>
<security:http pattern="/**" entry-point-ref="casEntryPoint">
    <security:custom-filter position="BASIC_AUTH_FILTER" ref="callbackFilter" />
    <security:intercept-url pattern="/cas/**" access="isAuthenticated()" />
</security:http>
```

or

```java
http
.authorizeRequests()
    .antMatchers("/cas/**").authenticated()
    .anyRequest().permitAll()
.and()
.exceptionHandling().authenticationEntryPoint(new Pac4jEntryPoint(config, "CasClient"))
.and()
.addFilterBefore(callbackFilter, BasicAuthenticationFilter.class);
```


---

### 4) Define the callback endpoint only for indirect clients (`CallbackFilter`)

For indirect clients (like Facebook), the user is redirected to an external identity provider for login and then back to the application.
Thus, a callback endpoint is required in the application. It is managed by the `CallbackFilter` which has the following behaviour:

1) the credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in the web session

2) finally, the user is redirected back to the originally requested url (or to the `defaultUrl`).


The following parameters are available:

1) `config`: the security configuration previously defined

2) `defaultUrl` (optional): it's the default url after login if no url was originally requested (`/` by default)

3) `multiProfile` (optional): it indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time (`false` by default)

4) `renewSession` (optional): it indicates whether the web session must be renewed after login, to avoid session hijacking (`true` by default)

5) `suffix` (optional): it defines on which endpoint the filter applies (`/callback` by default).


The callback endpoint must not be protected.


You can define it in the `securityContext.xml` file:

```xml
<bean id="callbackFilter" class="org.pac4j.springframework.security.web.CallbackFilter">
    <property name="config" ref="config" />
    <property name="multiProfile" value="true" />
</bean>
<security:http pattern="/**" entry-point-ref="noEntryPoint">
    ...
    <security:custom-filter position="BASIC_AUTH_FILTER" ref="callbackFilter" />
    ...
</security:http>
```

Or via Java configuration:

```java
@EnableWebSecurity
public class SecurityConfig {

    ...

    @Autowired
    private Config config;

    protected void configure(HttpSecurity http) throws Exception {

        CallbackFilter callbackFilter = new CallbackFilter(config);
        callbackFilter.setMultiProfile(true);

        http
            .antMatcher("/**")
            .addFilterBefore(callbackFilter, BasicAuthenticationFilter.class)
            ...
    }
    
    ...
}
```


---

### 5) Get the user profile

Like for any Spring Security web application, you can get the authenticated user via the `SecurityContextHolder.getContext().getAuthentication()`.
If the user is authenticated or remembered, the appropriate token will be stored in the context: `Pac4jAuthenticationToken` or `Pac4jRememberMeAuthenticationToken`.
As both implement the same interface: `Pac4jAuthentication` you should use it and get the main profile (`getProfile` method) or all profiles (`getProfiles` method) of the authenticated user: 

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
if (auth != null && auth instanceof Pac4jAuthentication) {
    Pac4jAuthentication token = (Pac4jAuthentication) auth;
    CommonProfile profile = token.getProfile();
}
```

The retrieved profile is at least a `CommonProfile`, from which you can retrieve the most common attributes that all profiles share. But you can also cast the user profile to the appropriate profile according to the provider used for authentication. For example, after a Facebook authentication:

```java
FacebookProfile facebookProfile = (FacebookProfile) commonProfile;
```

---

### 6) Logout

Like for any Spring Security webapp, you can use the default logout filter (in your Spring context XML file) for application logout:

```xml
<security:logout logout-success-url="/" />
```

But you can also use the *pac4j* `LogoutFilter` which can handle:
 
- the local logout by removing the pac4j profiles from the session (it can be used for the front-channel logout from the identity provider in case of a central logout)
- the central logout by calling the identity provider logout endpoint.


It has the following behaviour:

1) If the `localLogout` property is `true`, the pac4j profiles are removed from the web session (and the web session is destroyed if the `destroySession` property is `true`)

2) A post logout action is computed as the redirection to the `url` request parameter if it matches the `logoutUrlPattern` or to the `defaultUrl` if it is defined or as a blank page otherwise

3) If the `centralLogout` property is `true`, the user is redirected to the identity provider for a central logout and
then optionally to the post logout redirection URL (if it's supported by the identity provider and if it's an absolute URL).
If no central logout is defined, the post logout action is performed directly.


The following parameters are available:

1) `defaultUrl` (optional): the default logout url if no `url` request parameter is provided or if the `url` does not match the `logoutUrlPattern` (not defined by default)

2) `logoutUrlPattern` (optional): the logout url pattern that the `url` parameter must match (only relative urls are allowed by default)

3) `localLogout` (optional): whether a local logout must be performed (`true` by default)

4) `destroySession` (optional):  whether we must destroy the web session during the local logout (`false` by default)

5) `centralLogout` (optional): whether a central logout must be performed (`false` by default).


You can define it in the `securityContext.xml` file:

```xml
<bean id="pac4jLogoutFilter" class="org.pac4j.springframework.security.web.LogoutFilter">
    <property name="config" ref="config" />
    <property name="defaultUrl" value="/?defaulturlafterlogout" />
    <property name="destroySession" value="true" />
</bean>
<security:http create-session="never" pattern="/pac4jLogout*" entry-point-ref="noEntryPoint">
    <security:custom-filter position="BASIC_AUTH_FILTER" ref="pac4jLogoutFilter" />
</security:http>

<bean id="pac4jCentralLogoutFilter" class="org.pac4j.springframework.security.web.LogoutFilter">
    <property name="config" ref="config" />
    <property name="defaultUrl" value="http://localhost:8080/?defaulturlafterlogoutafteridp" />
    <property name="localLogout" value="false" />
    <property name="centralLogout" value="true" />
    <property name="logoutUrlPattern" value="http://localhost:8080/.*" />
</bean>
<security:http create-session="never" pattern="/pac4jCentralLogout*" entry-point-ref="noEntryPoint">
    <security:custom-filter position="BASIC_AUTH_FILTER" ref="pac4jCentralLogoutFilter" />
</security:http>
```

Or via Java configuration:

```java
@EnableWebSecurity
public class SecurityConfig {

    ...

    @Configuration
    @Order(13)
    public static class Pac4jLogoutWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private Config config;

        protected void configure(final HttpSecurity http) throws Exception {

            final LogoutFilter filter = new LogoutFilter(config, "/?defaulturlafterlogout");
            filter.setDestroySession(true);

            http
                    .antMatcher("/pac4jLogout")
                    .addFilterBefore(filter, BasicAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        }
    }

    @Configuration
    @Order(14)
    public static class Pac4jCentralLogoutWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private Config config;

        protected void configure(final HttpSecurity http) throws Exception {

            final LogoutFilter filter = new LogoutFilter(config, "http://localhost:8080/?defaulturlafterlogoutafteridp");
            filter.setLocalLogout(false);
            filter.setCentralLogout(true);
            filter.setLogoutUrlPattern("http://localhost:8080/.*");

            http
                    .antMatcher("/pac4jCentralLogout")
                    .addFilterBefore(filter, BasicAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        }
    }
    
    ...
}
```


## Migration guide

### 2.1 -> 3.0

You can use the pac4j `LogoutFilter` to handle both local (application) and remote (identity server) logout processes.

### 2.0 -> 2.1

The `CallbackFilter` only applies on `/callback` by default so if you need a different callback endpoint (another value) or to apply it globally (empty value), this needs to be changed with the `setSuffix` method.

The `Pac4jEntryPoint` can be defined with the `config` and `clientName` parameters to redirect to an identity provider for login.

### 1.4 - > 2.0

The `spring-security-pac4j` library has strongly changed in version 2:

- the `ClientAuthenticationProvider` has been removed as the authentication happens in the `SecurityFilter` (for direct clients) or in the `CallbackFilter` (for indirect clients)
- the `ClientAuthenticationEntryPoint` is replaced by the `Pac4jEntryPoint` which should never be called
- the `ClientAuthenticationToken` is replaced by the `Pac4jAuthenticationToken` and `Pac4jRememberMeAuthenticationToken` (depending on the use case)
- the security is ensured by the `SecurityFilter` (as usually in the pac4j world)
- the `CallbackFilter` finishes the login process for indirect clients (as usually in the pac4j world) and replaces the `ClientAuthenticationFilter`.


## Demo

The demo webapps for Spring Security without Spring Boot: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo) or with Spring Boot: [spring-security-pac4j-boot-demo](https://github.com/pac4j/spring-security-pac4j-boot-demo) are available for tests and implement many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...


## Release notes

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/3.0.0) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/2.1.0/index.html).


## Need help?

If you need commercial support (premium support or new/specific features), contact us at [info@pac4j.org](mailto:info@pac4j.org).

If you have any question, please use the following mailing lists:

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)


## Development

The version 3.0.1-SNAPSHOT is under development.

Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/spring-security-pac4j.png?branch=master)](https://travis-ci.org/pac4j/spring-security-pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). This repository must be added in the Maven `pom.xml` file for example:

```xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <name>Sonatype Nexus Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
