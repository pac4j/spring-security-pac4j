<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

If you already use the [Spring Security](http://projects.spring.io/spring-security) security library, you need to use the `spring-security-pac4j` security extension which makes multi-protocol (CAS, OAuth, SAML...) support really easy and consistent. It successfully replaces the spring-security-cas, spring-security-oauth, spring-security-saml2-core, spring-security-openid and Spring Social libraries and also adds missing protocols.

If you are looking for a full security library, you can directly use a pac4j implementation for your environment: [`spring-webmvc-pac4j`](https://github.com/pac4j/spring-webmvc-pac4j) for Spring MVC / Boot, [`j2e-pac4j`](https://github.com/pac4j/j2e-pac4j) for J2E, [`play-pac4j`](https://github.com/pac4j/play-pac4j) for Play framework v2... See all the frameworks and tools supported by [pac4j](http://www.pac4j.org).

`spring-security-pac4` supports many authentication mechanisms, called [**clients**](https://github.com/pac4j/pac4j/wiki/Clients):

- **indirect / stateful clients** are for UI when the user authenticates once at an external provider (like Facebook, a CAS server...) or via a local form (or basic auth popup).

See the [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows).

| The authentication mechanism you want | The `pac4j-*` submodule(s) you must use
|---------------------------------------|----------------------------------------
| OAuth (1.0 & 2.0): Facebook, Twitter, Google, Yahoo, LinkedIn, Github... | `pac4j-oauth`
| CAS (1.0, 2.0, 3.0, SAML, logout, proxy) | `pac4j-cas`
| SAML (2.0) | `pac4j-saml`
| OpenID Connect (1.0) | `pac4j-oidc`
| HTTP (form, basic auth)<br />+ LDAP<br />or Relational DB<br />or MongoDB<br />or Stormpath<br />or CAS REST API| `pac4j-http`<br />+ `pac4j-ldap`<br />or `pac4j-sql`<br />or `pac4j-mongo`<br />or `pac4j-stormpath`<br />or `pac4j-cas`
| Google App Engine UserService | `pac4j-gae`
| OpenID | `pac4j-openid`


## How to use it?

First, you need to add a dependency on this library as well as on the appropriate `pac4j` submodules. Then, you must define the [**clients**](https://github.com/pac4j/pac4j/wiki/Clients) for authentication.

Define the `ClientAuthenticationFilter` and `ClientAuthenticationProvider` to finish authentication processes.

Use the `ClientAuthenticationEntryPoint` entry point (using the `clientName` property for authentication) to start login at identity providers for the secured urls of your web application.

Just follow these easy steps:


### Add the required dependencies (`spring-security-pac4j-*` + `pac4j-*` libraries)

You need to add a dependency on `spring-security-pac4j` (<em>groupId</em>: **org.pac4j**, *version*: **1.4.0-SNAPSHOT**) as well as on the appropriate `pac4j` submodules (<em>groupId</em>: **org.pac4j**, *version*: **1.8.1-SNAPSHOT**): the `pac4j-oauth` dependency for OAuth support, the `pac4j-cas` dependency for CAS support, the `pac4j-ldap` module for LDAP authentication, ...


### Define the configuration (`Clients` + `XXXClient`)

Each authentication mechanism (Facebook, Twitter, a CAS server...) is defined by a client (implementing the `org.pac4j.core.client.Client` interface). All clients must be gathered in a `org.pac4j.core.client.Clients` class.
You have to define all the clients in your Spring context XML file:

    <bean id="facebookClient" class="org.pac4j.oauth.client.FacebookClient">
        <property name="key" value="fbKey" />
        <property name="secret" value="fbSecret" />
    </bean>

    <bean id="twitterClient" class="org.pac4j.oauth.client.TwitterClient">
        <property name="key" value="twKey" />
        <property name="secret" value="twSecret" />
    </bean>

	<bean id="usernamePasswordAuthenticator" class="org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator" />

    <bean id="formClient" class="org.pac4j.http.client.indirect.FormClient">
        <property name="loginUrl" value="http://localhost:8080/loginForm.jsp" />
        <property name="authenticator" ref="usernamePasswordAuthenticator" />
    </bean>


    <bean id="casClient" class="org.pac4j.cas.client.CasClient">
        <property name="casLoginUrl" value="http://mycasserverurl" />
    </bean>

    <bean id="clients" class="org.pac4j.core.client.Clients">
        <property name="callbackUrl" value="http://localhost:8080/callback" />
        <property name="clients">
        	<list>
        		<ref bean="facebookClient" />
        		<ref bean="twitterClient" />
        		<ref bean="formClient" />
        		<ref bean="casClient" />
        	</list>
        </property>
    </bean>

"http://localhost:8080/callback" is the url of the callback endpoint (see below).


### Define the callback endpoint

Indirect clients rely on external identity providers (like Facebook) and thus require to define a callback endpoint in the application where the user will be redirected after login at the identity provider.  
Thus, you need to define the appropriate `ClientAuthenticationFilter` in your Spring context XML file:

    <bean id="clientFilter" class="org.pac4j.springframework.security.web.ClientAuthenticationFilter">
        <property name="clients" ref="clients" />
        <property name="authenticationManager" ref="authenticationManager" />
    </bean>

    <bean id="clientProvider" class="org.pac4j.springframework.security.authentication.ClientAuthenticationProvider">
        <property name="clients" ref="clients" />
    </bean>

Notice you have one additional element for Spring Security:
- the provider: `ClientAuthenticationProvider` with uses by default the `CopyRolesUserDetailsService` user details service to set the roles from the ones in the user profile (granted by any `AuthorizationGenerator` attached to the client used).


### Protect an url

You can protect an url and require the user to be authenticated by a client by using the appropriate security configuration with entry points. For example :

    <security:http pattern="/facebook/**" entry-point-ref="facebookEntryPoint">
        <security:intercept-url pattern="/facebook/**" access="isAuthenticated()" />
    </security:http>
    <security:http pattern="/twitter/**" entry-point-ref="twitterEntryPoint">
        <security:intercept-url pattern="/twitter/**" access="isAuthenticated()" />
    </security:http>
    ...
    <security:http pattern="/**" entry-point-ref="casEntryPoint">
        <security:csrf disabled="true"/>
        <security:headers disabled="true" />
        <security:custom-filter after="CAS_FILTER" ref="clientFilter" />
        <security:intercept-url pattern="/cas/**" access="isAuthenticated()" />
        <security:intercept-url pattern="/**" access="permitAll()" />
        <security:logout logout-success-url="/" />
    </security:http>
    ...
    <bean id="facebookEntryPoint" class="org.pac4j.springframework.security.web.ClientAuthenticationEntryPoint">
        <property name="client" ref="facebookClient" />
    </bean>
    <bean id="twitterEntryPoint" class="org.pac4j.springframework.security.web.ClientAuthenticationEntryPoint">
        <property name="client" ref="twitterClient" />
    </bean>
    ...
    <bean id="casEntryPoint" class="org.pac4j.springframework.security.web.ClientAuthenticationEntryPoint">
        <property name="client" ref="casClient" />
    </bean>

For SAML support which requires Javascript POST (POST binding), notice the `<security:headers disabled="true" />`.


### Get redirection urls

You can also explicitly compute a redirection url to a provider by using the `getRedirectAction` method of the client, in order to create an explicit link for login. For example with Facebook:

    <%
    	WebContext context = new J2EContext(request, response);
        Clients clients = (Clients) application.getAttribute("clients");
        FacebookClient fbClient = (FacebookClient) clients.findClient(context, "FacebookClient");
	    String redirectionUrl = fbClient.getRedirectAction(context, false).getLocation();
	%>


### Get the user profile


After a successful authentication, you can get the identity of the user using the regular `SecurityContextHolder` class :

    ClientAuthenticationToken token = (ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    // user profile
    UserProfile userProfile = token.getUserProfile();
    CommonProfile commonProfile = (CommonProfile) userProfile;

The retrieved profile is at least a `CommonProfile`, from which you can retrieve the most common properties that all profiles share. But you can also cast the user profile to the appropriate profile according to the provider used for authentication. For example, after a Facebook authentication:
 
    FacebookProfile facebookProfile = (FacebookProfile) commonProfile;


### Logout

For logout, like for any other Spring Security webapp, use the default logout filter (in your Spring context XML file):

    <security:logout logout-success-url="/" />


## Demo

The demo webapp: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo) is available for tests and implement many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML...


## Release notes

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/1.4.0) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/1.8.1/index.html).


## Need help?

If you have any question, please use the following mailing lists:

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)


## Development

The current version 1.4.0-SNAPSHOT is under development.

Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/spring-security-pac4j.png?branch=master)](https://travis-ci.org/pac4j/spring-security-pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). This repository must be added in the Maven *pom.xml* file for example:

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
