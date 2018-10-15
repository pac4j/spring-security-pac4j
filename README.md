<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

**WARNING: if you start with a fresh Spring Boot or Spring MVC webapp, you should certainly use [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) instead of this library, which offers similar capabilities, but is a lot easier!**

The `spring-security-pac4j` project is an **easy and powerful security library for Spring Security** (with or without Spring Boot) web applications. It supports authentication and authorization, but also advanced features like session fixation and CSRF protection.
It's based on Java 8, Spring Security 5 and on the **[pac4j security engine](https://github.com/pac4j/pac4j) v3**. It's available under the Apache 2 license.

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - OpenID - Google App Engine - Kerberos - LDAP - SQL - JWT - MongoDB - CouchDB - IP address - REST API

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles/permissions - Anonymous/remember-me/(fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) The `SecurityFilter` protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

4) The `CallbackFilter` finishes the login process for an indirect client

5) The `LogoutFilter` handles the logout process.


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/spring-security-pac4j/wiki/Dependencies)

### 2) Define:

### - the [security configuration](https://github.com/pac4j/spring-security-pac4j/wiki/Security-configuration)
### - the [callback configuration](https://github.com/pac4j/spring-security-pac4j/wiki/Callback-configuration), only for web applications
### - the [logout configuration](https://github.com/pac4j/spring-security-pac4j/wiki/Logout-configuration)

### 3) [Apply security](https://github.com/pac4j/spring-security-pac4j/wiki/Apply-security)

### 4) [Get the authenticated user profiles](https://github.com/pac4j/spring-security-pac4j/wiki/Get-the-authenticated-user-profiles)


## Demo

The demo webapps for Spring Security without Spring Boot: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo) or with Spring Boot: [spring-security-pac4j-boot-demo](https://github.com/pac4j/spring-security-pac4j-boot-demo) are available for tests and implement many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...


## Release notes

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/4.1.0) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/3.3.0/index.html).

See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide) as well.


## Need help?

If you need commercial support (premium support or new/specific features), contact us at [info@pac4j.org](mailto:info@pac4j.org).

If you have any question, please use the following mailing lists:

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)


## Development

The version 4.1.0-SNAPSHOT is under development.

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
