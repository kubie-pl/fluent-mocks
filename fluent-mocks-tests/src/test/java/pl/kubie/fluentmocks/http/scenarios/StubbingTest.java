/**
 *    Copyright 2025 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package pl.kubie.fluentmocks.http.scenarios;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import pl.kubie.fluentmocks.http.api.HttpStubber;
import pl.kubie.fluentmocks.http.testing.Parameter;
import pl.kubie.fluentmocks.http.testing.StubberTest;
import pl.kubie.fluentmocks.http.testing.Times;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.apache.commons.io.IOUtils.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static pl.kubie.fluentmocks.http.api.http.HttpMethod.GET;
import static pl.kubie.fluentmocks.http.scenarios.TestStubbing.stubGetEndpoint;
import static pl.kubie.fluentmocks.http.scenarios.TestStubbing.stubPostEndpoint;
import static pl.kubie.fluentmocks.http.testing.Constants.NOT_FOUND_404;
import static pl.kubie.fluentmocks.http.testing.Constants.OK_200;
import static pl.kubie.fluentmocks.http.testing.Constants.TEST_URL;
import static pl.kubie.fluentmocks.http.testing.Http.call;

public class StubbingTest {

  @StubberTest
  @ParameterizedTest(name = "{0} {1}")
  @Parameter(
      name = "method",
      values = {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE"}
  )
  void should_stub_simple_endpoint(HttpStubber stubber, String method) {
    // when
    stubber.when(
            request -> request
                .method(method)
                .url(TEST_URL)
        )
        .respond(response -> response.statusCode(OK_200))
        .unlimited();

    // expect
    call(stubber)
        .when()
        .request(method, TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200);
  }

  @StubberTest
  void should_stub_for_single_invocation(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .respond()
        .once();

    // expect
    call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200);

    call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(NOT_FOUND_404);
  }

  @StubberTest
  void should_resolve_path_params(HttpStubber stubber) {
    // given
    stubber.when(
            request -> request.method(GET)
                .url(url -> url
                    .url("/test/{contextId}")
                    .pathParameter("contextId", "1")
                )
        )
        .respond(response -> response.statusCode(OK_200))
        .once();

    // expect
    call(stubber)
        .when()
        .get("/test/1")
        .then()
        .assertThat()
        .statusCode(OK_200);

  }

  @StubberTest
  void should_stub_with_query_params(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .when(request -> request.url(url -> url.queryParameter("contextId", "1")))
        .respond()
        .once();

    // expect
    call(stubber)
        .when()
        .get("/test?contextId=1")
        .then()
        .assertThat()
        .statusCode(OK_200);
  }

  @StubberTest
  void should_stub_with_multiple_values_for_query_param(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .when(request -> request.url(url -> url.queryParameter("contextId", List.of("1", "2"))))
        .respond()
        .once();

    // expect
    call(stubber)
        .when()
        .get("/test?contextId=1&contextId=2")
        .then()
        .assertThat()
        .statusCode(OK_200);
  }

  @StubberTest
  void should_fail_when_missing_path_param(HttpStubber stubber) {
    // given
    assertThatThrownBy(
        () -> stubber.when(request -> request
                .method(GET)
                .url("/test/{contextId}")
            )
            .respond(response -> response.statusCode(OK_200))
            .once()
    )
        .isNotNull();

  }

  @StubberTest
  void should_stub_for_multiple_invocations2(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .respond()
        .exactly(3);

    Times.run(3, () -> call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200));
  }

  @StubberTest
  void should_return_not_found_when_stubbed_invocations_exhausted(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .respond()
        .exactly(3);

    Times.run(3, () -> call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200));

    call(stubber)
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(NOT_FOUND_404);

  }

  @StubberTest
  void should_stub_endpoint_for_matching_specified_body(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.raw("test body")))
        .respond()
        .unlimited();

    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(404);

    call(stubber)
        .body("test body")
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @StubberTest
  void should_stub_endpoint_for_matching_specified_body_from_file(HttpStubber stubber) throws Exception {
    // given
    stubber.with(stubPostEndpoint())
        .when(request ->
            request.body(body ->
                body.file("/payloads/sample.json")
            )
        )
        .respond()
        .unlimited();

    // when
    call(stubber)
        .body(IOUtils.resourceToString("/payloads/sample.json", StandardCharsets.UTF_8))
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @StubberTest
  void should_override_properties_in_json_request_body(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .when(request ->
            request.body(
                body -> body.json("""
                        {"message":"original"}
                        """)
                    .override("message", "overridden"))
        )
        .respond()
        .unlimited();

    // expect
    call(stubber)
        .body("""
             {"message":"original"}
            """)
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(404);

    call(stubber)
        .body("""
             {"message":"overridden"}
            """)
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @StubberTest
  void should_stub_endpoint_returning_specified_body(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .body(body -> body.raw("Some body"))
        )
        .unlimited();

    // expect
    call(stubber)
        .body("test body")
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .body(is("Some body"));
  }

  @StubberTest
  void should_stub_endpoint_returning_json_body_with_overridden_property(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .body(body -> body.json(new SomeRecord("original"))
                .override("message", new SomeRecord("overridden"))
            )
        )
        .unlimited();

    // expect
    call(stubber)
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .body("message.message", is("overridden"));
  }

  @StubberTest
  void should_stub_endpoint_returning_raw_binary_data(HttpStubber stubber) throws Exception {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .body(body -> body.file("/payloads/image.png").raw())
        )
        .unlimited();

    // expect
    var body = call(stubber)
        .when()
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .body()
        .asByteArray();

    assertThat(body).isEqualTo(resourceToByteArray("/payloads/image.png"));
  }

  @StubberTest
  void should_override_properties_in_json_response_body(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .body(body -> body
                .json("""
                    {"message":"original"}
                    """)
                .override("message", "overridden")
            )
        )
        .unlimited();

    // when
    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .body("message", equalTo("overridden"));
  }

  @StubberTest
  void should_stub_endpoint_request_body_from_serialized_object(HttpStubber stubber) {
    // given
    var payload = new SomeRecord("world");

    // when
    stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.json(payload)))
        .respond()
        .once();

    // then
    call(stubber)
        .body(payload)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @StubberTest
  void should_stub_endpoint_request_body_from_byte_array(HttpStubber stubber) {
    // given
    var payload = new byte[]{1, 2, 3, 4, 5};

    // when
    stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.raw(payload)))
        .respond()
        .unlimited();

    // then
    call(stubber)
        .body(payload)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @StubberTest
  void should_stub_endpoint_request_body_from_file(HttpStubber stubber) throws Exception {
    // when
    stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.file("/payloads/image.png").raw()))
        .respond()
        .unlimited();

    //when
    var response = call(stubber)
        .body(resourceToByteArray("/payloads/image.png"))
        .post("/test");

    response
        .then()
        .assertThat()
        .statusCode(200);
  }

  @StubberTest
  void should_stub_endpoint_returning_body_from_serialized_object(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .body(body -> body.json(new SomeRecord("hello")))
        )
        .once();

    // when
    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .body("message", equalTo("hello"));
  }

  @StubberTest
  void should_stub_endpoint_matching_request_body_from_file(HttpStubber stubber) throws Exception {
    // given
    stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.file("/payloads/sample.json").json()))
        .respond()
        .unlimited();

    // when
    call(stubber)
        .body(IOUtils.resourceToString("/payloads/sample.json", StandardCharsets.UTF_8))
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);

  }

  @StubberTest
  void should_stub_endpoint_matching_request_body_from_file_overridden_by_object(HttpStubber stubber) throws Exception {
    // given
    stubber.with(stubPostEndpoint())
        .when(request ->
            request.body(body ->
                body.file("/payloads/sample.json")
                    .json()
                    .override("foo", new SomeRecord("hello"))
            )
        )
        .respond()
        .unlimited();

    // when
    call(stubber)
        .body(IOUtils.resourceToString("/payloads/sample-overridden.json", StandardCharsets.UTF_8))
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);

  }

  @StubberTest
  void should_stub_endpoint_returning_body_from_file(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .body(body -> body.file("/payloads/sample.json").json())
        )
        .unlimited();

    // when
    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .body("foo", equalTo("bar"));

  }

  @StubberTest
  void should_stub_endpoint_returning_specific_header(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .header("X-Custom-Header", "value")
        )
        .unlimited();

    // when
    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType("application/json")
        .header("X-Custom-Header", "value");

  }

  @StubberTest
  void should_stub_endpoint_matching_specific_header(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .when(request -> request.header("X-Custom-Header", "valid"))
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
        )
        .once();

    // when
    call(stubber)
        .header("X-Custom-Header", "invalid")
        .post("/test")
        .then()
        .assertThat()
        .statusCode(404);

    call(stubber)
        .header("X-Custom-Header", "valid")
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);

    call(stubber)
        .header("X-Custom-Header", "valid")
        .post("/test")
        .then()
        .assertThat()
        .statusCode(404);

  }

  @StubberTest
  void should_stub_endpoint_matching_specific_cookie(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .when(request -> request.cookie("X-Custom-Cookie", "valid"))
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
        )
        .once();

    // when
    call(stubber)
        .cookie("X-Custom-Cookie", "invalid")
        .post("/test")
        .then()
        .assertThat()
        .statusCode(404);

    call(stubber)
        .cookie("X-Custom-Cookie", "valid")
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200);

    call(stubber)
        .cookie("X-Custom-Cookie", "valid")
        .post("/test")
        .then()
        .assertThat()
        .statusCode(404);

  }

  @StubberTest
  void should_stub_endpoint_setting_specific_cookie(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .cookie("X-Custom-Cookie", "valid")
        )
        .once();

    // when
    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .cookie("X-Custom-Cookie", "valid");
  }

  @StubberTest
  void should_stub_endpoint_setting_multiple_cookies(HttpStubber stubber) {
    // given
    stubber.with(stubPostEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
            .cookie("X-Custom-Cookie-1", "valid")
            .cookie("X-Custom-Cookie-2", "valid")
        )
        .once();

    // when
    call(stubber)
        .post("/test")
        .then()
        .assertThat()
        .statusCode(200)
        .cookie("X-Custom-Cookie-1", "valid")
        .cookie("X-Custom-Cookie-2", "valid");
  }

  @StubberTest
  void should_stub_endpoint_with_delayed_response(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .respond(response -> response
            .statusCode(200)
            .delayed(Duration.ofMillis(500))
            .contentType("application/json")
        )
        .once();

    // when
    var start = Instant.now();
    call(stubber)
        .get("/test")
        .then()
        .assertThat()
        .statusCode(200);
    var stop = Instant.now();

    Duration duration = Duration.between(start, stop);
    assertThat(duration.toMillis()).isGreaterThan(500);
  }

  @StubberTest
  void endpoint_without_delay_should_respond_under_500_ms(HttpStubber stubber) {
    // given
    stubber.with(stubGetEndpoint())
        .respond(response -> response
            .statusCode(200)
            .contentType("application/json")
        )
        .once();

    // when
    var start = Instant.now();
    call(stubber)
        .get("/test")
        .then()
        .assertThat()
        .statusCode(200);
    var stop = Instant.now();

    Duration duration = Duration.between(start, stop);
    assertThat(duration.toMillis()).isLessThan(500);
  }

}
