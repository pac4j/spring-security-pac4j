<h2>What is Spring Security OAuth client ?</h2>

If you want to "do" Spring, Security and OAuth, you could use the Spring Security OAuth library or one of the Spring Social libraries.

But I think that in the vast majority of the use cases, you JUST want to :
- protect your application by authenticating at the OAuth provider (like Facebook for example)
- get your OAuth profile (first name, last name, birthdate...) after being authenticated.

<b>That's exactly what this Spring Security OAuth client library does.</b> It's based on my <a href="https://github.com/leleuj/scribe-up">scribe-up</a> library (which deals with OAuth authentication and user profile retrieval).

<h2>OAuth providers supported</h2>

<table>
<tr><td>DropBox</td><td>Facebook</td><td>Github</td><td>Google</td><td>LinkedIn</td><td>Twitter</td><td>Windows Live</td><td>WordPress</td><td>Yahoo</td></tr>
<tr><td>OAuth 1.0</td><td>OAuth 2.0</td><td>OAuth 2.0</td><td>OAuth 1.0 & 2.0</td><td>OAuth 1.0</td><td>OAuth 1.0</td><td>OAuth 2.0</td><td>OAuth 2.0</td><td>OAuth 1.0</td></tr>
</table>

Look at the <a href="https://github.com/leleuj/scribe-up/wiki/Description-of-providers-and-profiles">description of the providers and profiles</a> or follow the guide to <a href="https://github.com/leleuj/scribe-up/wiki/Extend-or-add-a-new-provider">extend or add a new provider</a>.

<h2>Technical description</h2>

This library has <b>ONLY 4 classes</b> :
<ol>
<li>the <b>OAuthAuthenticationFilter</b> class is called after OAuth authentication (on the /j_spring_oauth_security_check url) : it creates the OAuthAuthenticationToken token and calls the authentication manager to do the authentication</li>
<li>the <b>OAuthAuthenticationToken</b> class is the token for an OAuth authentication (= OAuth credentials + the user profile)</li>
<li>the <b>OAuthAuthenticationProvider</b> class is the provider responsible for authenticating OAuthAuthenticationToken tokens : it calls the OAuth provider to get the access token and the user profile and computes the authorities.</li>
<li>the <b>OAuthAuthenticationEntryPoint</b> class redirects the user to the OAuth provider if needed</li>
</ol>

<h2>Code sample</h2>

If you want to authenticate at Facebook, you have to define the Facebook provider :
<pre><code>&lt;bean id="facebookProvider" class="org.scribe.up.provider.impl.FacebookProvider"&gt;
  &lt;property name="key" value="xxxx" /&gt;
  &lt;property name="secret" value="yyyy" /&gt;
  &lt;property name="callbackUrl" value="http://localhost:8080/demo/j_spring_oauth_security_check" /&gt;
&lt;/bean&gt;</code></pre>
You first have to define a filter to handle callback from Facebook :
<pre><code>&lt;bean id="oauthFilter" class="com.github.leleuj.ss.oauth.client.web.OAuthAuthenticationFilter">
  &lt;property name="provider" ref="facebookProvider" />
  &lt;property name="authenticationManager" ref="authenticationManager" />
&lt;/bean&gt;</code></pre>
Then the dedicated Facebook provider for authentication and user profile retrieval :
<pre><code>&lt;bean id="oauthProvider" class="com.github.leleuj.ss.oauth.client.authentication.OAuthAuthenticationProvider"&gt;
  &lt;property name="provider" ref="facebookProvider" /&gt;
&lt;/bean&gt;</code></pre>
Finally, only if needed, you may want to define an entry point to trigger redirection to Facebook when the user tries to access a protected area of the application :
<pre><code>&lt;bean id="oauthEntryPoint" class="com.github.leleuj.ss.oauth.client.web.OAuthAuthenticationEntryPoint"&gt;
  &lt;property name="provider" ref="facebookProvider" /&gt;
&lt;/bean&gt;</code></pre>
With the following "security configuration" :
<pre><code>&lt;security:authentication-manager alias="authenticationManager"&gt;
  &lt;security:authentication-provider ref="oauthProvider" /&gt;
&lt;/security:authentication-manager&gt;
&lt;security:http entry-point-ref="facebookEntryPoint"&gt;
  &lt;security:custom-filter before="CAS_FILTER" ref="oauthFilter" /&gt;
  &lt;security:intercept-url pattern="/protected/*" access="IS_AUTHENTICATED_FULLY" /&gt;
  &lt;security:intercept-url pattern="/**" access="IS_AUTHENTICATED_ANONYMOUSLY" /&gt;
  &lt;security:logout /&gt;
&lt;/security:http&gt;</code></pre>

If you have multiple providers, you can multiply filters and providers by setting specific endpoint url :
<pre><code>&lt;bean id="facebookFilter" class="com.github.leleuj.ss.oauth.client.web.OAuthAuthenticationFilter"&gt;
  &lt;constructor-arg index="0" value="/j_spring_facebook_security_check" /&gt;
  &lt;property name="provider" ref="facebookProvider" /&gt;
  &lt;property name="authenticationManager" ref="authenticationManager" /&gt;
&lt;/bean&gt;</code></pre>
But a better solution (<b>new feature of version 1.1.0</b>) is to define a providers definition to group providers :
<pre><code>&lt;bean id="facebookProvider" class="org.scribe.up.provider.impl.FacebookProvider"&gt;
  &lt;property name="key" value="xxxx" /&gt;
  &lt;property name="secret" value="yyyy" /&gt;
&lt;/bean&gt;
&lt;bean id="twitterProvider" class="org.scribe.up.provider.impl.TwitterProvider"&gt;
  &lt;property name="key" value="xxxx" /&gt;
  &lt;property name="secret" value="yyyy" /&gt;
&lt;/bean&gt;
&lt;bean id="providersDefinition" class="org.scribe.up.provider.ProvidersDefinition"&gt;
  &lt;property name="baseUrl" value="http://localhost:8080/j_spring_oauth_security_check" /&gt;
  &lt;property name="providers"&gt;
    &lt;list&gt;
      &lt;ref bean="facebookProvider" /&gt;
      &lt;ref bean="twitterProvider" /&gt;
    &lt;/list&gt;
  &lt;/property&gt;
&lt;/bean&gt;</code></pre>
And use this providers definition in filter and provider :
<pre><code>&lt;bean id="oauthFilter" class="com.github.leleuj.ss.oauth.client.web.OAuthAuthenticationFilter">
  &lt;property name="providersDefinition" ref="providersDefinition" />
  &lt;property name="authenticationManager" ref="authenticationManager" />
&lt;/bean&gt;
&lt;bean id="oauthProvider" class="com.github.leleuj.ss.oauth.client.authentication.OAuthAuthenticationProvider"&gt;
  &lt;property name="providersDefinition" ref="providersDefinition" />
&lt;/bean&gt;</code></pre>

After successfull authentication, the user profile can be retrieved from the current OAuth token in security context :
<pre><code>OAuthAuthenticationToken token = (OAuthAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
// dedicated profile
FacebookProfile facebookProfile = (FacebookProfile) token.getUserProfile();
// common profile to all providers
CommonProfile commonProfile = (CommonProfile) token.getUserProfile();</code></pre>

A demo with Facebook and Twitter providers is available with <a href="https://github.com/leleuj/spring-security-oauth-client-demo">spring-security-oauth-client-demo</a>.
The old demo working only with spring-security-oauth-client version 1.0.0 is available with <a href="https://github.com/leleuj/spring-security-oauth-client-demo-1.0.0">spring-security-oauth-client-demo-1.0.0</a>.

<h2>Versions</h2>

The last released version is the <b>1.0.0</b>.

The current version : <i>1.1.0-SNAPSHOT</i> is under development, it's available on <a href="https://oss.sonatype.org/content/repositories/snapshots/org/scribe/scribe-up/">Sonatype snapshots repository</a> as Maven dependency :
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;com.github.leleuj.springframework.security&lt;/groupId&gt;
    &lt;artifactId&gt;spring-security-oauth-client&lt;/artifactId&gt;
    &lt;version&gt;1.1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

<h2>Contact</h2>

Find me on <a href="http://www.linkedin.com/in/jleleu">LinkedIn</a> or by email : leleuj@gmail.com
