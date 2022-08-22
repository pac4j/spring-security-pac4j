<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

The `spring-security-pac4j` project is a **bridge from pac4j to Spring Security** to push the pac4j security context into the Spring Security security context.  
It's based on Java 11, Shiro 1.9 and on the **[pac4j security engine](https://github.com/pac4j/pac4j) v5**. It's available under the Apache 2 license.

**It must be used with a [pac4j security library](https://www.pac4j.org/implementations.html)**:
- certainly, the [javaee-pac4j](https://github.com/pac4j/jee-pac4j) implementation (which has similar filters than `spring-security-pac4j` version <= 7.x)
- or maybe, if you use Spring MVC, the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) implementation.

While **it is always better to directly use a pac4j security library alone**, this bridge can be used to keep legacy software and avoid full migration.


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/spring-security-pac4j/wiki/Dependencies)

### 2) [Install and use the bridge](https://github.com/pac4j/spring-security-pac4j/wiki/Bridge)

### 3) Install, configure and use the pac4j security library

You must refer to the documentation of the pac4j security library you use: [javaee-pac4j](https://github.com/pac4j/jee-pac4j) or [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j).


## Demos

Spring security demo: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo).

Spring Security boot demo: [spring-security-pac4j-boot-demo](https://github.com/pac4j/spring-security-pac4j-boot-demo).

Spring Security reactive boot demo: [spring-security-reactive-pac4j-boot-demo](https://github.com/pac4j/spring-security-reactive-pac4j-boot-demo).


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/pac4j/spring-security-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [pac4j documentation](https://www.javadoc.io/doc/org.pac4j/pac4j-core/5.4.6/index.html) and the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/8.0.0).

See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide) as well.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
