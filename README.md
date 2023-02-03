<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

The `spring-security-pac4j` project is a **bridge from pac4j to Spring Security (reactive)** to push the pac4j security context into the Spring Security security (reactive) context.  
It's based on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

| spring-security-pac4j | JDK | pac4j | Spring security | Operating philosophy        | Usage of Lombok | Status           |
|-----------------------|-----|-------|-----------------|-----------------------------|-----------------|------------------|
| version >= 10         | 17  | v6    | v6              | Bridge only                 | Yes             | In development   |
| version >= 9          | 17  | v5    | v6              | Bridge only                 | No              | Production ready |
| version >= 8          | 11  | v5    | v5              | Standalone security library | No              | Production ready |
| version >= 6          | 11  | v5    | v5              | Standalone security library | No              | Production ready |
| version >= 5          | 8   | v4    | v5              | Standalone security library | No              | Production ready |

**Since version 8 (working as a bridge only), it must be used with a [pac4j security library](https://www.pac4j.org/implementations.html)**:
- the [jakartaee-pac4j](https://github.com/pac4j/jee-pac4j) (Spring 6) or [javaee-pac4j](https://github.com/pac4j/jee-pac4j) (Spring 5) implementation (which has similar filters as `spring-security-pac4j` version <= 7.x)
- if you use Spring MVC, the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) implementation version >= 7 (Spring 6) or version < 7 (Spring 5)
- if you use Spring Webflux, the [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j) implementation version >= 2 (Spring 6) or version < 2 (Spring 5)

While **it is always better to directly use a pac4j security library alone**, this bridge can be used to keep legacy software and avoid full migration.


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/spring-security-pac4j/wiki/Dependencies)

### 2) The bridge is automatically installed

### 3) Install, configure and use the pac4j security library

You must refer to the documentation of the pac4j security library you use: [jakartaee-pac4j](https://github.com/pac4j/jee-pac4j) or [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) or [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j).


## Demos

Spring security boot demo with pac4j JEE filters: `spring-security-pac4j` + `jakartaee-pac4j`: [spring-security-jee-pac4j-boot-demo](https://github.com/pac4j/spring-security-jee-pac4j-boot-demo).

Spring Security boot demo with pac4j SpringMVC: `spring-security-pac4j` + `spring-webmvc-pac4j`: [spring-security-webmvc-pac4j-boot-demo](https://github.com/pac4j/spring-security-webmvc-pac4j-boot-demo).

Spring Security reactive boot demo with pac4j Spring Webflux: `spring-security-pac4j` + `spring-webflux-pac4j`: [spring-security-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-security-webflux-pac4j-boot-demo).


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/spring-security-pac4j), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/pac4j/spring-security-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes). Learn more by browsing the [pac4j documentation](https://www.javadoc.io/doc/org.pac4j/pac4j-core/5.7.0/index.html) and the [spring-security-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/spring-security-pac4j/9.0.0).

See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide) as well.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
