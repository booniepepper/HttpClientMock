# HttpClientMock

HttpClientMock is a library for mocking [Java HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html).
Its goal is to provide an intuitive API for defining and verifying client behaviour.

* [Installation](#installation)
* [Requirements](#requirements)
* [Usage](#usage)
* [Request matching](#request-matching)
* [Define response](#define-response)
* [Verification](#verification)
* [Examples](#examples)
* [License](#license)
* [About](#about)


## Installation

HttpClientMock 2.0.0 is planned to be available in Maven Central Repository before September 2023.

#### Bld

```
scope(test).include(dependency("so.dang.cool", "http-client-mock", version(2, 0, 0)));
```

#### Maven

```
<dependency>
    <groupId>so.dang.cool</groupId>
    <artifactId>http-client-mock</artifactId>
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```

#### Gradle

```
dependencies {
    testImplementation("so.dang.cool:http-client-mock:2.0.0")
}
```


## Requirements

* Java 11 or higher


## Usage

#### Record
Define client behavior to begin working with HttpClientMock. Before code under
test starts, HttpClientMock must be told how to respond to each request. It'll
record all traffic it receives.

```
HttpClientMock httpClientMock = new HttpClientMock();
httpClientMock.onGet("http://localhost/login")
  .withParameter("user","john")
  .doReturn("Ok");
httpClientMock.onPost("http://localhost/login").doReturnStatus(501);
```

#### Replay
Code under test starts and uses HttpClientMock with its defined behaviour.
```
var get = HttpRequest.newBuilder(URI.create("http://localhost/login?user:john")).GET().build();
var post = HttpRequest.newBuilder(URI.create("http://localhost/login")).POST(noBody()).build();
httpClient.send(get, ofString()); // returns response with body "Ok"
httpClient.send(post, ofString()); // returns response with status 501
```

#### Verify
When code under test finishes, HttpClientMock allows to verify the exact number
of requests made. (Or not!) The fluent interface for verification resembles the
interface for defining mock behaviour.
```
httpClientMock.verify().get("http://localhost/login").withParameter("user","john").called()
httpClientMock.verify().post("http://localhost/login").notCalled()
```

## Request matching

### HTTP methods

HttpClientMock supports all HTTP methods.

```
httpClientMock.onGet().doReturn("get");
httpClientMock.onPost().doReturn("post");
httpClientMock.onPut().doReturn("put");
httpClientMock.onDelete().doReturn("delete");
httpClientMock.onOptions().doReturn("options");
httpClientMock.onHead().doReturn("head");
```

### URL

Every `onGet()`, `onPost()`, .... method accept URL. It is possible to write:

```
httpClientMock.onGet("http://localhost/login?user=john").doReturnStatus(200);
```

...which is equal to:

```
httpClientMock.onGet()
  .withHost("httt://locahost")
  .withPath("/login")
  .withParameter("user","john")
  .doReturnStatus(200);
```

It's possible to define a default host using HttpClientMock's constructor so
later methods can accept relative URLs.

```
HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
httpClientMock.onGet("/login").doReturn("ok");
httpClientMock.onPost("/edit?user=john").doReturnStatus(200);

httpClientMock.onGet("http://www.google.com").doReturn("Google") // Absolute paths still work.
```

### Host, path, parameters, and reference conditions

Each part of a URL can be defined separately.

```
httpClientMock.onGet()
  .withHost("httt://locahost")
  .withPath("/login")
  .withParameter("user","john")
  .withReference("edit")
  .doReturnStatus(200);
```

### Header conditions

```
httpClientMock.onGet("http://localhost/login")
  .withHeader("tracking","123")
  .doReturn("ok");
```

### Body conditions

```
httpClientMock.onGet("http://localhost/login")
  .withBody("tracking",containsString(123))
  .doReturn("ok");
```

### Custom conditions

```
Condition fooCondition = request -> request.getUri().contains("foo");
httpClientMock.onGet("http://localhost/foo/bar")
  .with(fooCondition)
  .doReturn("yes");
```

### Matchers

Every condition method accepts [Hamcrest Matcher](https://github.com/hamcrest/JavaHamcrest)
which allows the definition of custom conditions on requests.

```
httpClientMock.onGet("http://localhost")
  .withPath(containsString("login"))
  .withParameter("user",equalToIgnoringCase("John)")
  .reference(not(equalTo("edit")));
```

_Note: To prevent unnecessary dependencies for those that don't need or want
Hamcrest, this will be changed to an optional dependency in a future version of
HttpClientMock. It will continue to be supported, though._

### Multiple matching rules

If a request matches more then one rule, the request will be routed to the rule
that was defined most recently.

### No rule matches

If request doesn't match any rule, HttpClientMock returns a response with a 404
status code.


## Define response

### Response

Response with explicit body and implicit 200 status code.

```
httpClientMock.onGet("http://localhost").doReturn("my response")
```

### Status

Response with explicit status code and an implicit empty body.

```
httpClientMock.onGet("http://localhost").doReturnStatus(300)
httpClientMock.onGet("http://localhost").doReturn("Overloaded").withStatus("500");
```

### Exception

Instead of returning a response, you can also throw an exception.

```
httpClientMock.onGet("http://localhost").doThrowException(new IOException());
```

### Custom actions

```
Action echo r -> {
  HttpEntity entity = ((HttpEntityEnclosingRequestBase) r.getHttpRequest()).getEntity();
  BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), 200, "ok");
  response.setEntity(entity);
  return response;
};
httpClientMock.onGet("http://localhost").doAction(echo);
```

### Response headers

```
httpClientMock.onPost("/login").doReturn("foo").withHeader("tracking", "123")
```

### Response status

```
httpClientMock.onPost("/login?user=bar").doReturn("Wrong user").withStatus(403)
```

### JSON

Response with explicit body, content type "application/json", and 200 status code.

```
httpClientMock.onPost("/login").doReturnJSON("{foo:1}");
```

### XML

Response with explicit body, content type "application/xml", and 200 status
code.

```
httpClientMock.onPost("/login").doReturnXML("<foo>bar</foo>");
```

### Multiple actions

It's possible to add multiple actions to one rule. Every call will use next
action until last is reached.

```
httpClientMock.onPut("/addUser")
  .doReturn("ok");
  .doReturnStatus(500);

var req = HttpRequest.newBuilder(URI.create("http://localhost/addUser")).PUT(noBody()).build();
httpClientMock.send(req, ofString()); //returns "ok"
httpClientMock.send(req, ofString()); //returns status 500
httpClientMock.send(req, ofString()); //returns status 500
```


## Verification

You can check how many calls were made to HttpClientMock. Verification follows
a similar fluent interface as rule definition.

```
httpClientMock.verify().get("http://localhost").called();

httpClientMock.verify().get("http://localhost/login")
  .withParameter("user","john")
  .called();

httpClientMock.verify().get("http://localhost/login")
  .withParameter("user","Ben")
  .notCalled();

httpClientMock.verify().delete().notCalled();

httpClientMock.verify().get().called(greaterThanOrEqualTo(1));
```


# Examples

## Example 1

```
// DEFINE BEHAVIOUR
HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
httpClientMock.onGet("/login?user=john").doReturnJSON("{permission:1}");
httpClientMock.onPost("/edit")
  .withParameter("user","John")
  .doReturn("ok")
  .doReturnStatus(503);

// EXECUTION
// request to http://localhost:8080/login?user=john returns JSON {permission:1}
// first request to http://localhost:8080/edit?user=john returns message "ok"
// second request to http://localhost:8080/edit?user=john returns request with status 503

// VERIFICATION
httpClientMock.verify().get("/login?user=john").called();
httpClientMock.verify().post("/edit?user=john").called(2);
httpClientMock.verify().delete().notCalled();
```


## Example 2

```
// DEFINE BEHAVIOUR
HttpClientMock httpClientMock = new HttpClientMock();
httpClientMock.onGet("http://localhost:8080/login").doReturn("Missing parameter user").withStatus(400);
httpClientMock.onGet("http://localhost:8080/login")
  .withParameter("user","JJohn")
  .doReturn("Wrong user name").withStatus(403);
httpClientMock.onGet("http://localhost:8080/login")
  .withParameter("user","John")
  .doReturn("ok");

// EXECUTION
// request to http://localhost:8080/login?user=john returns message "ok"

// VERIFICATION
httpClientMock.verify().get("/login?user=john").called();
```

## License

The project is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).

## About

Maintenance and development is now lead by [J.R. Hill](https://so.dang.cool).

The project was originally created in 2019 by Paweł Adamski and Radosław
Skupnik, and licensed to a Polish consultancy called PGS Software that no
longer exists. It was forked in 2023 because the project was still deemed
useful even though the original project was [abandoned](https://github.com/PGSSoft/HttpClientMock/issues/3).
