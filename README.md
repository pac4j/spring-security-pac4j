<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

The `spring-security-pac4j` project is a **bridge from pac4j to Spring Security (reactive)** to push the pac4j security context into the Spring Security security (reactive) context.  
It's based on Java 17 (or 11), Spring Security 6 (or 5) and on the **[pac4j security engine](https://github.com/pac4j/pac4j) v5**. It's available under the Apache 2 license.

**It must be used with a [pac4j security library](https://www.pac4j.org/implementations.html)**:
- the [jakartaee-pac4j](https://github.com/pac4j/jee-pac4j) (Spring 6) or [javaee-pac4j](https://github.com/pac4j/jee-pac4j) (Spring 5) implementation (which has similar filters as `spring-security-pac4j` version <= 7.x)
- if you use Spring MVC, the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) implementation version >= 7 (Spring 6) or version < 7 (Spring 5)
- if you use Spring Webflux, the [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j) implementation version >= 2 (Spring 6) or version < 2 (Spring 5)

While **it is always better to directly use a pac4j security library alone**, this bridge can be used to keep legacy software and avoid full migration.


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/spring-security-pac4j/wiki/Dependencies)

### 2) [Install the bridge for a Spring webapp without Spring Boot](https://github.com/pac4j/spring-security-pac4j/wiki/Bridge)

### 3) Install, configure and use the pac4j security library

You must refer to the documentation of the pac4j security library you use: [jakartaee-pac4j](https://github.com/pac4j/jee-pac4j) or [javaee-pac4j](https://github.com/pac4j/jee-pac4j) or [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) or [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j).


## Demos

Spring security boot demo with pac4j JEE filters: `spring-security-pac4j` + `javaee-pac4j`: [spring-security-jee-pac4j-boot-demo](https://github.com/pac4j/spring-security-jee-pac4j-boot-demo).

Spring Security boot demo with pac4j SpringMVC: `spring-security-pac4j` + `spring-webmvc-pac4j`: [spring-security-webmvc-pac4j-boot-demo](https://github.com/pac4j/spring-security-webmvc-pac4j-boot-demo).

Spring Security reactive boot demo with pac4j Spring Webflux: `spring-security-pac4j` + `spring-webflux-pac4j`: [spring-security-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-security-webflux-pac4j-boot-demo).


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/pac4j/spring-security-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [pac4j documentation](https://www.javadoc.io/doc/org.pac4j/pac4j-core/5.4.6/index.html) and the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/8.0.0).

See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide) as well.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
