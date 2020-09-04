# Ideal Spring Boot microservice
## Introduction
In this article you can read how to build a Spring Boot application to boost your productivity and save money because you will write less business code, less tests and you will know how to avoid typo mistakes.

## Problematic

We all love microservices, this is small and clear programs one or two developers may support. In the Java world we have a bunch of frameworks to build microservices: Spring Framework (with Spring Boot), Quarkus, Micronaut and others or we can build it from scratch. There are frameworks and libraries for almost everything: to build blocking and reactive applications, to work with relational and NoSQL databases, to send messages into message brokers, to build REST services.

But in our project we often need to do many things manually. We may have more or less overhead but we always have this feeling that things should be much simpler than is.

## Solution
Let’s go through the whole development process from a primitive REST provider and client when we are configuring it manually to Spring Boot starter artefact.
We will use Lombok to avoid code boilerplate

### Stage 1: Simple provider
Let’s build a simple provider or server who will return a REST response. This handler implemented with Spring reactive web applications framework :

```java
@RestController
@RequestMapping("/hello")
public class HelloWorldHandler {
   @GetMapping
   public Mono<HelloWorldResponse> getHelloWorldResponse(@RequestParam(name = "name", required = false) String name) {
       return Mono.just(new HelloWorldResponse(String.format("Hello, %s", name)));
   }
}

@Data
@RequiredArgsConstructor
public class HelloWorldResponse {
   private final String message;
}
```

Nothing special in this code: we receive the request with a path parameter and return the response object with message.

### Stage 2: WebFlux REST client
Now let’s build a simple REST client with Spring reactive library (WebFlux). We need copy/paste HelloWorldResponse.class from provider project

```java
public Mono<String> getServiceProviderResponse(String username) {
   WebClient client = WebClient.create(serviceProviderURL);
   return client.get().uri("/hello?name={name}", Map.of("name", username))
           .retrieve()
           .bodyToMono(HelloWorldResponse.class)
           .map(HelloWorldResponse::getMessage);
}
```

This is very simple code: send request, receive response.

### Step 3: JaxRS REST client
Now let’s build a REST client with JaxRS. Many enterprise developers love this library because they have a JaxRS library in their classpath. We will implement the async GET request.

```java
public Mono<String> getServiceProviderResponse(String username) {
   return Mono.fromFuture(() ->
           CompletableFuture.supplyAsync(() -> {
               Future<HelloWorldResponse> future = client
                       .target(serviceProviderURL)
                       .path("/hello")
                       .queryParam("name", username)
                       .request(MediaType.APPLICATION_JSON_TYPE)
                       .buildGet()
                       .submit(HelloWorldResponse.class);
               while (!future.isDone()) {
                   Thread.yield();
               }
               try {
                   return future.get();
               } catch (InterruptedException | ExecutionException e) {
                   throw new RuntimeException(e.getMessage(), e);
               }
           }))
           .map(HelloWorldResponse::getMessage);
}
```

#### Notes:

Both solutions work great but both have a lot of boilerplate and error prone code: you need to manually describe the path, add query parameters without any compile time check. You need to keep your personal copy of the response class, if something will be changed in the next version provider’s API you will know about this when integration tests will run (if you have good integration tests) or in production. You will need to create tests for these methods again and again or exclude these classes from tests coverage report.

Let’s fix the problem with manual REST client creation.

### Step 4: JaxRS proxy client
We will use org.glassfish.jersey.ext:jersey-proxy-client to generate REST client based on interface with JaxRS annotations.
Interface:

```java
@Path("/hello")
public interface HelloWorldHandler {
   @GET
   HelloWorldResponse getHelloWorldResponse(@QueryParam("name") String name);
}
```

Client configuration:

```java
@Bean
public HelloWorldHandler helloWorldHandler() {
   return WebResourceFactory.newResource(HelloWorldHandler.class, ClientBuilder.newClient().target(serviceProviderURL));
}
```

Client usage:

```java
@Service
@RequiredArgsConstructor
public class HelloWorldJaxRsProxy {
   private final HelloWorldHandler helloWorldHandler;

   public String getServiceProviderResponse(String username) {
       return helloWorldHandler.getHelloWorldResponse(username).getMessage();
   }
}
```

This code will fix problems with code boilerplate, we no need to create tests for clients configured by library (by default we trust libraries).

### Step 5: Split provider to API and implementation

To solve the problem with provider’s endpoints we will need to split the provider project to two modulest: API module and implementation module. Our goal is two java artefacts.

Implementation module will depends on api:

```xml
<dependency>
   <groupId>com.dim4tech</groupId>
   <artifactId>service-provider-api</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Let’s create API interface with endpoint description in service-provider-api

```java
@Path("/hello")
public interface HelloWorldHandler {
   @GET
   HelloWorldResponse getHelloWorldResponse(@QueryParam("name") String name);
}
```

We will add in same module and package our response object

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloWorldResponse {
   private String message;
}
```
Now we need create implementation for our  interface in service-provider-impl module (just copy/paste old implementation)

```java
@RestController
@RequestMapping("/hello")
public class HelloWorldHandler {
   @GetMapping
   public Mono<HelloWorldResponse> getHelloWorldResponse(@RequestParam(name = "name", required = false) String name) {
       return Mono.just(new HelloWorldResponse(String.format("Hello, %s", name)));
   }
}
```
### Step 6: Add API artefact into dependencies
Let’s add provider API artefact into client  project dependencies, delete HelloWorldResponse and HelloWorldHandler classes from the client project and update all imports in existing classes.

```xml
<dependency>
   <groupId>com.dim4tech</groupId>
   <artifactId>service-provider-api</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Now we solved all problems regarding API and code compile time verification.
Also we are using a third party library to auto generate REST clients based on JaxRS annotations.

Looks like we have solved all problems and increased development speed. To build a new REST client all we need is to configure the client bean. But now each project will have a lot of template code to create a new REST client.

How can we solve this?

### Step 7: Spring Boot starter
This part is specific for Spring Framework based applications. All previous steps you can reproduce with any DI framework or with plain Java code.
Let’s create a new module in provider project service-provider-spring-boot-starter
This starter will autoconfigure all rest clients.

Let’s create the configuration properties bean. We must declare application.serviceprovider.url property in our client project to inject it.

```java
@Data
@ConfigurationProperties(prefix = "application.serviceprovider")
public class ServiceProviderConfigurationProperties {
   private String url;
}
``` 

Now we are creating Spring boot starter bean auto-configuration class:

```java
@RequiredArgsConstructor
@Configuration
@ConditionalOnClass(HelloWorldReactiveHandler.class)
@EnableConfigurationProperties(ServiceProviderConfigurationProperties.class)
public class ServiceProviderStarterConfiguration {
   private final ServiceProviderConfigurationProperties serviceProviderConfigurationProperties;

   @Bean
   public HelloWorldReactiveHandler helloWorldClient() {
       return WebReactiveFeign.<HelloWorldReactiveHandler>builder()
               .contract(new JAXRSContract())
               .target(HelloWorldReactiveHandler.class, serviceProviderConfigurationProperties.getUrl());
   }
}
```

This configuration will be run only if we will have HelloWorldReactiveHandler.class in our classpath.
Last step. Create file META-INF/spring.factories in project resources folder with context:
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.dim4tech.serviceprovider.configuration.ServiceProviderStarterConfiguration

Add new dependency into client’s project pom.xml file:

```java
<dependency>
   <groupId>com.dim4tech</groupId>
   <artifactId>service-provider-spring-boot-starter</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>
```

and inject the REST client from the starter:

```java
@Service
@RequiredArgsConstructor
public class HelloWorldFromStarter {

   private final HelloWorldReactiveHandler helloWorldReactiveClient;

   public Mono<String> getServiceProviderResponse(String username) {
       return helloWorldReactiveClient.getHelloWorldResponse(username)
               .map(HelloWorldResponse::getMessage);
   }
}
```

Now we can build integration tests in the starter project to be sure the starter works as we expect.

## Conclusion

To reduce your code usage complexity and boilerplate, split your service projects to API and implementation modules. This step will help you support different versions of API, your clients will know about API incompatibility issues in compile time.

Use libraries to configure REST clients, this will save you from trivial typo or copy/paste mistakes in your client project code.

If you are using Spring Framework, it will be nice  to provide packages to provide autoconfiguration for REST clients in your services, this will increase client application development speed. If you build enterprise applications with dozens or even hundreds of microservices it will be great if you provide the same expertise to build microservices and interact with them.
