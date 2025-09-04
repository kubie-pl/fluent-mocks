package pl.kubie.fluentmocks.http.scenarios;

import pl.kubie.fluentmocks.http.api.HttoMockSpec;

import java.util.function.Consumer;

import static pl.kubie.fluentmocks.http.api.http.HttpMethod.GET;
import static pl.kubie.fluentmocks.http.api.http.HttpMethod.POST;

public class TestStubbing {

  public static Consumer<HttoMockSpec> stubPostEndpoint() {
    return test()
        .andThen(stub -> stub.when(request -> request.method(POST)));
  }

  public static Consumer<HttoMockSpec> stubGetEndpoint() {
    return test()
        .andThen(stub -> stub.when(request -> request.method(GET)));
  }

  private static Consumer<HttoMockSpec> test() {
    return stub -> stub
        .when(request -> request.url("/test"))
        .respond(response -> response.statusCode(200));
  }

}
