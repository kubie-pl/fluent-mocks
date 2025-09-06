package pl.kubie.fluentmocks.http.scenarios;

import pl.kubie.fluentmocks.http.api.HttpMockSpec;

import java.util.function.Consumer;

import static pl.kubie.fluentmocks.http.api.http.HttpMethod.GET;
import static pl.kubie.fluentmocks.http.api.http.HttpMethod.POST;

public class TestStubbing {

  public static Consumer<HttpMockSpec> stubPostEndpoint() {
    return test()
        .andThen(stub -> stub.when(request -> request.method(POST)));
  }

  public static Consumer<HttpMockSpec> stubGetEndpoint() {
    return test()
        .andThen(stub -> stub.when(request -> request.method(GET)));
  }

  private static Consumer<HttpMockSpec> test() {
    return stub -> stub
        .when(request -> request.url("/test"))
        .respond(response -> response.statusCode(200));
  }

}
