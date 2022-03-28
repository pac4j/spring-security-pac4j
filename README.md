<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

The `spring-security-pac4j` project is an **easy and powerful security library for Spring Security web applications and web services (with or without Spring Boot)**. It supports authentication and authorization, but also advanced features like session fixation and CSRF protection.
It's based on Java 11, Spring Security 5 and on the **[pac4j security engine](https://github.com/pac4j/pac4j) v5**. It's available under the Apache 2 license.

<img src="https://pac4j.github.io/pac4j/img/warning_sign.png" width="60" /> <b>For a new Spring Boot or Spring MVC project or if you intend to migrate your whole webapp to <i>pac4j</i>, you should use the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) library instead of this one, which offers similar capabilities, but is easier!</b>

[**Main concepts and components:**](https://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](https://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for web applications authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - Google App Engine - Kerberos - LDAP - SQL - JWT - MongoDB - CouchDB - IP address - REST API

2) An [**authorizer**](https://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles/permissions - Anonymous/remember-me/(fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) A [**matcher**](https://www.pac4j.org/docs/matchers.html) defines whether the `SecurityFilter` must be applied and can be used for additional web processing

4) The `SecurityFilter` protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

5) The `CallbackFilter` finishes the login process for an indirect client

6) The `LogoutFilter` logs out the user from the application and triggers the logout at the identity provider level

7) The `Pac4jEntryPoint` handles when the user is not authenticated

8) The `JEEContext` and the `ProfileManager` components can be injected (for a Spring Boot/MVC webapp and using the `spring-webmvc-pac4j` library)

9) The `@RequireAnyRole` and `@RequireAllRoles` annotations check the user roles (for a Spring Boot/MVC webapp and using the `spring-webmvc-pac4j` library).


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/spring-security-pac4j/wiki/Dependencies)

### 2) Define:

### - the [security configuration](https://github.com/pac4j/spring-security-pac4j/wiki/Security-configuration)
### - the [callback configuration](https://github.com/pac4j/spring-security-pac4j/wiki/Callback-configuration), only for web applications
### - the [logout configuration](https://github.com/pac4j/spring-security-pac4j/wiki/Logout-configuration)

### 3) [Apply security](https://github.com/pac4j/spring-security-pac4j/wiki/Apply-security)

### 4) [Get the authenticated user profiles](https://github.com/pac4j/spring-security-pac4j/wiki/Get-the-authenticated-user-profiles)


## Demos

The demo webapps for Spring Security without Spring Boot: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo) or with Spring Boot: [spring-security-pac4j-boot-demo](https://github.com/pac4j/spring-security-pac4j-boot-demo) are available for tests and implement many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/pac4j/spring-security-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [pac4j documentation](https://www.javadoc.io/doc/org.pac4j/pac4j-core/5.4.0/index.html) and the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/7.0.0).

See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide) as well.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
