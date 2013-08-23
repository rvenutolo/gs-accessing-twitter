<#assign project_id="gs-accessing-twitter">
This guide walks you through the process of creating a simple web application that fetches data from Twitter. 

What you'll build
-----------------

You'll learn how to build a Spring application that accesses profile data from a Twitter user and from people whom the user follows on Twitter.

What you'll need
----------------

 - About 15 minutes
 - An application ID and secret obtained from [registering an application with Twitter][gs-register-twitter-app].
 - <@prereq_editor_jdk_buildtools/>

## <@how_to_complete_this_guide jump_ahead='Enable Twitter'/>

<a name="scratch"></a>
Set up the project
------------------
<@build_system_intro/>

<@create_directory_structure_hello/>


<@create_both_builds/>

<@bootstrap_starter_pom_disclaimer/>

<a name="initial"></a>
Enable Twitter
----------------
Before you can fetch a user's data from Twitter, you need to set up a few things in the Spring configuration. This configuration class contains everything you need to enable Twitter in your application:

    <@snippet path="src/main/java/hello/TwitterConfig.java" prefix="complete"/>

Because the application will be accessing Twitter data, `TwitterConfig` is annotated with [`@EnableTwitter`][@EnableTwitter]. As shown here, the `appId` and `appSecret` attributes have fake values. These correspond to your application's consumer key and secret you obtain when you [register the application with Twitter][gs-register-twitter-app]. For the code to work, substitute the real values given to you by Twitter in place of the fake values.

Notice that `TwitterConfig` is also annotated with `@EnableInMemoryConnectionRepository`. After a user authorizes your application to access their Twitter data, Spring Social will create a connection. That connection will need to be saved in a connection repository for long-term use.

For the purposes of this guide's sample application, an in-memory connection repository is sufficient. Although an in-memory connection repository is fine for testing and small sample applications, you'll want to select a more persistent
option for real applications. You can use [`@EnableJdbcConnectionRepository`][@EnableJdbcConnectionRepository] to persist connections to a relational database.

Within the `TwitterConfig`'s body, two beans are declared: a `ConnectController` bean and a `UserIdSource` bean.

Obtaining user authorization from Twitter involves a "dance" of redirects between the application and Twitter. This "dance" is formally known as [OAuth][u-oauth]'s _Resource Owner Authorization_. Don't worry if you don't know much about OAuth. Spring Social's [`ConnectController`][ConnectController] takes care of the OAuth dance for you.

Notice that `ConnectController` is created by injecting a [`ConnectionFactoryLocator`][ConnectionFactoryLocator] and a [`ConnectionRepository`][ConnectionRepository] via the constructor. You won't need to explicitly declare these beans, however. The `@EnableTwitter` annotation ensures that a `ConnectionFactoryLocator` bean is created, and the `@EnableInMemoryConnectionRepository` annotation creates an in-memory implementation of `ConnectionRepository`.

Connections represent a three-way agreement between a user, an application, and an API provider such as Twitter. Although Twitter and the application itself are readily identifiable, you'll need a way to identify the current user. That's what the `UserIdSource` bean is for. 

Here, the `userIdSource` bean is defined by an inner-class that always returns "testuser" as the user ID. The sample application has only one user. In a real application, you probably want to create an implementation of `UserIdSource` that determines the user ID from the currently authenticated user (perhaps by consulting with an [`Authentication`][Authentication] obtained from Spring Security's [`SecurityContext`][SecurityContext]).

Create connection status views
------------------------------
Although much of what `ConnectController` does involves redirecting to Twitter and handling a redirect from Twitter, it also shows connection status when a GET request to /connect is made. `ConnectController` defers to a view named connect/{provider ID}Connect when no existing connection is available and to connect/{providerId}Connected when a connection exists for the provider. In this case, {provider ID} is "twitter".

`ConnectController` does not define its own connection views, so you need to create them. First, here's a Thymeleaf view to be shown when no connection to Twitter exists:

    <@snippet path="src/main/resources/templates/connect/twitterConnect.html" prefix="complete"/>

The form on this view will POST to /connect/twitter, which is handled by `ConnectController` and will kick off the OAuth authorization code flow.

Here's the view to be displayed when a connection exists:

    <@snippet path="src/main/resources/templates/connect/twitterConnected.html" prefix="complete"/>


Fetch Twitter data
---------------------
With Twitter configured in your application, you now can write a Spring MVC controller that fetches data for the user who authorized the application and presents it in the browser. `HelloController` is just such a controller:

    <@snippet path="src/main/java/hello/HelloController.java" prefix="complete"/>

`HelloController` is created by injecting a `Twitter` object into its constructor. The `Twitter` object is a reference to Spring Social's Twitter API binding.

The `helloTwitter()` method is annotated with `@RequestMapping` to indicate that it should handle GET requests for the root path (/). The first thing it does is check to see if the user has authorized the application to access the user's Twitter data. If not, then the user is redirected to `ConnectController` with the option to begin the authorization process.

If the user authorizes the application to access the data, the application can fetch almost any data pertaining to the authorizing user. For the purposes of this guide, the application only fetches the user's profile as well as a list of profiles belonging to Twitter users whom the user follows (but not those who follow the user). Both are placed into the model to be displayed by the view identified as "hello".

Speaking of the "hello" view, here it is as a Thymeleaf template:

    <@snippet path="src/main/resources/templates/hello.html" prefix="complete"/>

This template simply displays a greeting to the user and a list of the user's friends.
Note that even though the full user profiles were fetched, only the names from those profiles are used in this template.

Make the application executable
-------------------------------

Although it is possible to package this service as a traditional [WAR][u-war] file for deployment to an external application server, the simpler approach demonstrated below creates a _standalone application_. You package everything in a single, executable JAR file, driven by a good old Java `main()` method. And along the way, you use Spring's support for embedding the [Tomcat][u-tomcat] servlet container as the HTTP runtime, instead of deploying to an external instance.

### Create an Application class

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

The `main()` method defers to the [`SpringApplication`][] helper class, providing `Application.class` as an argument to its `run()` method. This tells Spring to read the annotation metadata from `Application` and to manage it as a component in the [Spring application context][u-application-context].

The `@ComponentScan` annotation tells Spring to search recursively through the `hello` package and its children for classes marked directly or indirectly with Spring's [`@Component`][] annotation. This directive ensures that Spring finds and registers the `GreetingController`, because it is marked with `@Controller`, which in turn is a kind of `@Component` annotation.

The `@Import` annotation tells Spring to import additional Java configuration. Here it is asking Spring to import the `TwitterConfig` class where you enabled Twitter in your application.

The [`@EnableAutoConfiguration`][] annotation switches on reasonable default behaviors based on the content of your classpath. For example, because the application depends on the embeddable version of Tomcat (tomcat-embed-core.jar), a Tomcat server is set up and configured with reasonable defaults on your behalf. And because the application also depends on Spring MVC (spring-webmvc.jar), a Spring MVC [`DispatcherServlet`][] is configured and registered for you — no `web.xml` necessary! Auto-configuration is a powerful, flexible mechanism. See the [API documentation][`@EnableAutoConfiguration`] for further details.

<@build_an_executable_jar_subhead/>

<@build_an_executable_jar_with_both/>

<@run_the_application_with_both module="service"/>

```
... app starts up ...
```

Once the application starts up, you can point your web browser to http://localhost:8080. Because no connection is established yet, you see this screen prompting you to connect with Twitter:

![No connection to Twitter exists yet.](images/connect.png)
 
When you click **Connect to Twitter**, the browser is redircted to Twitter for authorization:

![Twitter needs your permission to allow the application to access your data.](images/twauth.png)

At this point, Twitter asks if you'd like to allow the sample application to read tweets from your profile and see who you follow. Here the screen is misleading, because the application in this case will only read your profile details and the profile details of the people you follow. Click **Authorize app** to grant permission.

Once permission is granted, Twitter redirects the browser to the application. A connection is created and stored in the connection repository. You should see this page indicating that a connection was successful:

![A connection with Twitter has been created.](images/connected.png)

If you click on the link on the connection status page, you are taken to the home page. This time, now that a connection exists, you see your name on Twitter and a list of your friends:

![Guess noone told you life was gonna be this way.](images/friends.png)

Summary
-------
Congratulations! You've developed a simple web application that uses Spring Social to obtain user authorization to fetch data from the user's Twitter profile and from the profiles of people whom the user follows.

<@u_war/>
<@u_tomcat/>
<@u_application_context/>
[`SpringApplication`]: http://static.springsource.org/spring-bootstrap/docs/0.5.0.BUILD-SNAPSHOT/javadoc-api/org/springframework/bootstrap/SpringApplication.html
[`@Component`]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/stereotype/Component.html
[`@EnableAutoConfiguration`]: http://static.springsource.org/spring-bootstrap/docs/0.5.0.BUILD-SNAPSHOT/javadoc-api/org/springframework/bootstrap/context/annotation/SpringApplication.html
[`DispatcherServlet`]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html
[gs-register-twitter-app]: /guides/gs/register-twitter-app
[@EnableTwitter]: http://static.springsource.org/spring-social-twitter/docs/1.1.x/api/org/springframework/social/twitter/config/annotation/EnableTwitter.html
[@EnableJdbcConnectionRepository]: http://static.springsource.org/spring-social/docs/1.1.x/api/org/springframework/social/config/annotation/EnableJdbcConnectionRepository.html
<@u_oauth/>
[ConnectController]: http://static.springsource.org/spring-social/docs/1.1.x/api/org/springframework/social/connect/web/ConnectController.html
[ConnectionFactoryLocator]: http://static.springsource.org/spring-social/docs/1.1.x/api/org/springframework/social/connect/ConnectionFactoryLocator.html
[ConnectionRepository]: http://static.springsource.org/spring-social/docs/1.1.x/api/org/springframework/social/connect/ConnectionRepository.html
[Authentication]: http://static.springsource.org/spring-security/site/docs/3.2.x/apidocs/org/springframework/security/core/Authentication.html
[SecurityContext]: http://static.springsource.org/spring-security/site/docs/3.2.x/apidocs/org/springframework/security/core/context/SecurityContext.html

