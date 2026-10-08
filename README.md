<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-security.png" width="300" />
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/org.pac4j/spring-security-pac4j"><img src="https://img.shields.io/maven-central/v/org.pac4j/spring-security-pac4j?label=Maven%20Central" alt="Maven Central" /></a>
  <a href="https://github.com/pac4j/spring-security-pac4j/actions/workflows/ci.yml"><img src="https://github.com/pac4j/spring-security-pac4j/actions/workflows/ci.yml/badge.svg" alt="Build status" /></a>
  <img src="https://img.shields.io/badge/Java-17%2B-blue" alt="Java 17+" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.x%20%7C%207.x-blue" alt="Spring Security 6.x | 7.x" />
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="Apache 2 license" /></a>
</p>

> `spring-security-pac4j` is the Spring Security integration of **[pac4j](https://github.com/pac4j/pac4j)**, the security engine for Java.
> If it is useful to you, please ⭐ **[star pac4j on GitHub](https://github.com/pac4j/pac4j)**: it helps other developers discover it!

The `spring-security-pac4j` project is a **bridge from pac4j to Spring Security (reactive)** to push the pac4j security context into the Spring Security security (reactive) context.  
It's based on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

| spring-security-pac4j | JDK | pac4j | Spring security | Operating philosophy        | Usage of Lombok | Status           |
|-----------------------|-----|-------|-----------------|-----------------------------|-----------------|------------------|
| version >= 10.1       | 17  | v6    | v6 or v7        | Bridge only                 | Yes             | Production ready |
| version 10.0.x        | 17  | v6    | v6              | Bridge only                 | Yes             | Production ready |
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

The current development version is **10.1.1-SNAPSHOT**.

The latest released version is the [![Maven Central](https://img.shields.io/maven-central/v/org.pac4j/spring-security-pac4j.svg)](https://repo1.maven.org/maven2/org/pac4j/spring-security-pac4j). The [next version](https://github.com/pac4j/spring-security-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/spring-security-pac4j/wiki/Release-Notes).

See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide) as well.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
