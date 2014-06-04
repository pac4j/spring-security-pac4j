## What is the spring-security-pac4j library ? [![Build Status](https://travis-ci.org/leleuj/spring-security-pac4j.png?branch=master)](https://travis-ci.org/leleuj/spring-security-pac4j)

The **spring-security-pac4j** library is a web multi-protocols client for [Spring Security](http://static.springsource.org/spring-security/site/index.html).

It supports these 5 protocols on client side : 

1. OAuth (1.0 & 2.0)
2. CAS (1.0, 2.0, SAML, logout & proxy)
3. HTTP (form & basic auth authentications)
4. OpenID
5. SAML (2.0) (*still experimental*).

It's available under the Apache 2 license and based on my [pac4j](https://github.com/leleuj/pac4j) library.


## Providers supported

<table>
<tr><th>Provider</th><th>Protocol</th><th>Maven dependency</th><th>Client class</th><th>Profile class</th></tr>
<tr><td>CAS server</td><td>CAS</td><td>pac4j-cas</td><td>CasClient & CasProxyReceptor</td><td>CasProfile</td></tr>
<tr><td>CAS server using OAuth Wrapper</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>CasOAuthWrapperClient</td><td>CasOAuthWrapperProfile</td></tr>
<tr><td>DropBox</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>DropBoxClient</td><td>DropBoxProfile</td></tr>
<tr><td>Facebook</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>FacebookClient</td><td>FacebookProfile</td></tr>
<tr><td>GitHub</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>GitHubClient</td><td>GitHubProfile</td></tr>
<tr><td>Google</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>Google2Client</td><td>Google2Profile</td></tr>
<tr><td>LinkedIn</td><td>OAuth 1.0 & 2.0</td><td>pac4j-oauth</td><td>LinkedInClient & LinkedIn2Client</td><td>LinkedInProfile & LinkedIn2Profile</td></tr>
<tr><td>Twitter</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>TwitterClient</td><td>TwitterProfile</td></tr>
<tr><td>Windows Live</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WindowsLiveClient</td><td>WindowsLiveProfile</td></tr>
<tr><td>WordPress</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WordPressClient</td><td>WordPressProfile</td></tr>
<tr><td>Yahoo</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>YahooClient</td><td>YahooProfile</td></tr>
<tr><td>PayPal</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>PayPalClient</td><td>PayPalProfile</td></tr>
<tr><td>Vk</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>VkClient</td><td>VkProfile</td></tr>
<tr><td>Foursquare</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>FoursquareClient</td><td>FoursquareProfile</td></tr>
<tr><td>Bitbucket</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>BitbucketClient</td><td>BitbucketProfile</td></tr>
<tr><td>Web sites with basic auth authentication</td><td>HTTP</td><td>pac4j-http</td><td>BasicAuthClient</td><td>HttpProfile</td></tr>
<tr><td>Web sites with form authentication</td><td>HTTP</td><td>pac4j-http</td><td>FormClient</td><td>HttpProfile</td></tr>
<tr><td>Google</td><td>OpenID</td><td>pac4j-openid</td><td>GoogleOpenIdClient</td><td>GoogleOpenIdProfile</td></tr>
<tr><td>SAML Identity Provider</td><td>SAML 2.0</td><td>pac4j-saml</td><td>Saml2Client</td><td>Saml2Profile</td></tr>
</table>

## Technical description

This library has **only 4 classes** :

1. the **ClientAuthenticationFilter** class is called after authentication at the provider (on the /j_spring_pac4j_security_check url by default) : it creates the ClientAuthenticationToken token and calls the authentication manager to finish the authentication
2. the **ClientAuthenticationToken** class is the token for an authentication (= provider's credentials + the user profile)
3. the **ClientAuthenticationProvider** class is the provider responsible for authenticating ClientAuthenticationToken tokens : it calls the provider to get the access token and the user profile and computes the authorities
4. the **ClientAuthenticationEntryPoint** class redirects the user to the provider for authentication.

and is based on the <i>pac4j-*</i> libraries.

Learn more by browsing the [spring-security-pac4j Javadoc](http://www.pac4j.org/apidocs/spring-security-pac4j/index.html) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/index.html).


## How to use it ?

### Add the required dependencies

If you want to use a specific client support, you need to add the appropriate Maven dependency in the *pom.xml* file :

* for OAuth support, the *pac4j-oauth* dependency is required
* for CAS support, the *pac4j-cas* dependency is required
* for HTTP support, the *pac4j-http* dependency is required
* for OpenID support, the *pac4j-openid* dependency is required
* for SAML support, the *pac4j-saml* dependency is required.

For example, to add OAuth support, add the following XML snippet :

    <dependency>
      <groupId>org.pac4j</groupId>
      <artifactId>pac4j-oauth</artifactId>
      <version>1.5.1</version>
    </dependency>

As these snapshot dependencies are only available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j/), the appropriate repository must be added in the *pom.xml* file also :

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

### Define the clients

All the clients used to communicate with various providers (Facebook, Twitter, a CAS server...) must be defined in your Spring security context file. For example :

    <bean id="facebookClient" class="org.pac4j.oauth.client.FacebookClient">
        <property name="key" value="fbkey" />
        <property name="secret" value="fbsecret" />
    </bean>
    <bean id="twitterClient" class="org.pac4j.oauth.client.TwitterClient">
        <property name="key" value="twkey" />
        <property name="secret" value="twsecret" />
    </bean>
	<bean id="usernamePasswordAuthenticator" class="org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator" />
    <bean id="formClient" class="org.pac4j.http.client.FormClient">
        <property name="loginUrl" value="http://localhost:8080/theForm.jsp" />
        <property name="usernamePasswordAuthenticator" ref="usernamePasswordAuthenticator" />
    </bean>
    <bean id="basicAuthClient" class="org.pac4j.http.client.BasicAuthClient">
        <property name="usernamePasswordAuthenticator" ref="usernamePasswordAuthenticator" />
    </bean>
    <bean id="casClient" class="org.pac4j.cas.client.CasClient">
        <property name="casLoginUrl" value="http://localhost:8888/cas/login" />
    </bean>
    <bean id="clients" class="org.pac4j.core.client.Clients">
        <property name="callbackUrl" value="http://localhost:8080/callback" />
        <property name="clients">
        	<list>
        		<ref bean="facebookClient" />
        		<ref bean="twitterClient" />
        		<ref bean="formClient" />
        		<ref bean="basicAuthClient" />
        		<ref bean="casClient" />
        	</list>
        </property>
    </bean>

### Define the appropriate filter and provider

To authenticate/get the profile of the users in your application after they have been successfully authenticated at the providers, you need to define a filter to receive callbacks (with credentials from the providers) and a provider to finish the authentication process :

    <bean id="clientFilter" class="org.pac4j.springframework.security.web.ClientAuthenticationFilter">
    	<constructor-arg value="/callback"/>
        <property name="clients" ref="clients" />
        <property name="authenticationManager" ref="authenticationManager" />
    </bean>
    <bean id="clientProvider" class="org.pac4j.springframework.security.authentication.ClientAuthenticationProvider">
        <property name="clients" ref="clients" />
    </bean>

### Protect the urls

You can protect your urls and force the user to be authenticated by a client by using the defining the appropriate security configuration with entry points. For example :

    <security:http pattern="/facebook/**" entry-point-ref="facebookEntryPoint">
        <security:custom-filter after="CAS_FILTER" ref="clientFilter" />
        <security:intercept-url pattern="/facebook/**" access="IS_AUTHENTICATED_FULLY" />
    </security:http>
    <security:http pattern="/twitter/**" entry-point-ref="twitterEntryPoint">
        <security:custom-filter after="CAS_FILTER" ref="clientFilter" />
        <security:intercept-url pattern="/twitter/**" access="IS_AUTHENTICATED_FULLY" />
    </security:http>
    ...
    <security:http pattern="/**" entry-point-ref="casEntryPoint">
        <security:custom-filter after="CAS_FILTER" ref="clientFilter" />
        <security:intercept-url pattern="/cas/**" access="IS_AUTHENTICATED_FULLY" />
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_ANONYMOUSLY" />
        <security:logout />
    </security:http>
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

### Get redirection urls

You can also explicitely compute a redirection url to a provider for authentication by using the *getRedirectionUrl* method of the client. For example with Facebook :  

    <%
    	WebContext context = new J2EContext(request, response); 
	    FacebookClient fbClient = (FacebookClient) application.getAttribute("FacebookClient");
	    String redirectionUrl = fbClient.getRedirectionUrl(context, false, false);
	%>

### Get the user profile

After successful authentication, you can get the authentication of the user using the well-known *SecurityContextHolder* class :

    ClientAuthenticationToken token = (ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    // user profile
    UserProfile userProfile = token.getUserProfile();
    CommonProfile commonProfile = (CommonProfile) userProfile;
    
The profile returned is a *CommonProfile*, from which you can retrieve the most common properties that all profiles share. 
But you can also cast the user profile to the appropriate profile according to the provider used for authentication.
For example, after a Facebook authentication :
 
    // facebook profile
    FacebookProfile facebookProfile = (FacebookProfile) commonProfile;

Or for all the OAuth 1.0/2.0 profiles, to get the access token :
    
    OAuth10Profile oauthProfile = (OAuth10Profile) commonProfile
    String accessToken = oauthProfile.getAccessToken();
    // or
    String accessToken = facebookProfile.getAccessToken();

### Demo

A demo with Facebook, Twitter, CAS, form authentication and basic auth authentication providers is available at [spring-security-pac4j-demo](https://github.com/leleuj/spring-security-pac4j-demo).


## Versions

The current version **1.2.4-SNAPSHOT** is under development. It's available on the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j) as a Maven dependency :

The last released version is the **1.2.3** :

    <dependency>
        <groupId>org.pac4j</groupId>
        <artifactId>spring-security-pac4j</artifactId>
        <version>1.2.3</version>
    </dependency>

See the [release notes](https://github.com/leleuj/spring-security-pac4j/wiki/Release-Notes).


## Contact

If you have any question, please use the following mailing lists :
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)

