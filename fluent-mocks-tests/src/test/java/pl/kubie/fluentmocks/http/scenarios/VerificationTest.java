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

import org.junit.jupiter.params.ParameterizedTest;
import pl.kubie.fluentmocks.http.api.HttpStubber;
import pl.kubie.fluentmocks.http.testing.Parameter;
import pl.kubie.fluentmocks.http.testing.StubberTest;
import pl.kubie.fluentmocks.http.testing.Times;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pl.kubie.fluentmocks.http.scenarios.TestStubbing.stubGetEndpoint;
import static pl.kubie.fluentmocks.http.scenarios.TestStubbing.stubPostEndpoint;
import static pl.kubie.fluentmocks.http.testing.Constants.NOT_FOUND_404;
import static pl.kubie.fluentmocks.http.testing.Constants.OK_200;
import static pl.kubie.fluentmocks.http.testing.Constants.TEST_URL;
import static pl.kubie.fluentmocks.http.testing.Http.call;
import static pl.kubie.fluentmocks.http.testing.VerificationAssertions.assertThatVerificationFailed;

public class VerificationTest {

  @StubberTest
  @ParameterizedTest(name = "{0} {1}")
  @Parameter(
      name = "method",
      values = {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE"}
  )
  void verification_should_pass_when_endpoint_called_expected_number_of_times_matching_body(HttpStubber stubber, String method) {
    // when
    var mock = stubber.when(
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

    // and
    mock.verify()
        .once()
        .atLeast(1)
        .atMost(1)
        .between(0, 1)
        .between(1, 1)
        .between(1, 2);
  }

  @StubberTest
  void verification_should_fail_when_stub_not_interacted(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();

    // expect
    assertThatVerificationFailed(() -> mock.verify().once());
    assertThatVerificationFailed(() -> mock.verify().atLeast(1));
    assertThatVerificationFailed(() -> mock.verify().exactly(1));
    assertThatVerificationFailed(() -> mock.verify().between(2, 5));

  }

  @StubberTest
  void verification_should_pass_when_stub_not_interacted(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();

    // expect
    mock.verify()
        .never()
        .atMost(1)
        .exactly(0)
        .between(0, 1);
  }

  @StubberTest
  void should_pass_when_stub_interacted_given_number_of_times(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();

    // when
    Times.run(3,
        () -> call(stubber)
            .when()
            .get(TEST_URL)
            .then()
            .assertThat()
            .statusCode(OK_200)
    );

    // then
    mock.verify()
        .exactly(3)

        .atMost(3)
        .atMost(4)

        .atLeast(3)
        .atLeast(2)

        .between(3, 3)
        .between(2, 3)
        .between(3, 4);
  }

  @StubberTest
  void should_match_all_calls_to_stubbed_endpoint(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .once();

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


    // expect
    mock.verify().exactly(2);
  }

  @StubberTest
  void should_stub_single_endpoint_with_different_bodies_and_verify(HttpStubber stubber) {
    // given
    var mock1 = stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.raw("Stub1")))
        .respond()
        .once();

    var mock2 = stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.raw("Stub2")))
        .respond()
        .once();

    call(stubber)
        .body("Stub1")
        .post(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200);

    call(stubber)
        .body("Stub2")
        .post(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200);

    // expect
    mock1.verify().once();
    mock2.verify().once();

    // and
    call(stubber)
        .body("Stub1")
        .post(TEST_URL)
        .then()
        .assertThat()
        .statusCode(NOT_FOUND_404);

    call(stubber)
        .body("Stub2")
        .post(TEST_URL)
        .then()
        .assertThat()
        .statusCode(NOT_FOUND_404);
  }

  @StubberTest
  void verification_should_take_into_account_requests_exceeding_stubbing(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond(response -> response.statusCode(OK_200))
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

    mock.verify().atLeast(3);
    mock.verify().exactly(4);
  }

  @StubberTest
  void verification_should_fail_when_mock_called_less_times_than_required(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();

    // when
    Times.run(2, () -> call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200));

    // then
    assertThatVerificationFailed(() -> mock.verify().atLeast(3));
  }

  @StubberTest
  void verification_should_fail_when_mock_called_more_times_than_expected(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();

    // when
    Times.run(5, () -> call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200));

    // thenx
    assertThatVerificationFailed(() -> mock.verify().once());
    assertThatVerificationFailed(() -> mock.verify().exactly(4));
    assertThatVerificationFailed(() -> mock.verify().atMost(3));
  }

  @StubberTest
  void verification_should_pass_for_request_performed_with_specific_body(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubPostEndpoint())
        .when(request -> request.body(body -> body.raw("test body")))
        .respond()
        .once();

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

    mock.verify().once();
  }

  @StubberTest
  void verification_should_pass_when_mock_called_with_specific_header(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubPostEndpoint())
        .when(request -> request.header("X-Custom-Header", "valid"))
        .respond()
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

    mock.verify().atLeast(1);
    mock.verify().exactly(2);

    mock.verify()
        .atLeast(1)
        .exactly(2);

  }

  @StubberTest
  void verification_should_pass_when_mock_called_with_specific_cookie(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubPostEndpoint())
        .when(request -> request.cookie("X-Custom-Cookie", "valid"))
        .respond()
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

    mock.verify()
        .atLeast(1)
        .exactly(2);
  }

  @StubberTest
  void verification_should_pass_when_awaiting_interaction(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();

    // when
    call(stubber)
        .when()
        .get(TEST_URL)
        .then()
        .assertThat()
        .statusCode(OK_200);

    // then
    mock.await()
        .verify()
        .once();
  }

  @StubberTest
  void should_report_near_misses_if_verification_failed(HttpStubber stubber) {
    // given
    var mock = stubber.with(stubGetEndpoint())
        .when(request -> request.url(url -> url.queryParameter("foo", "bar")))
        .respond()
        .once();

    // when
    call(stubber)
        .queryParam("foo", "baz")
        .get("/test")
        .then()
        .assertThat()
        .statusCode(404);

    assertThatThrownBy(() -> mock.verify().once()).isNotNull();
  }

}
