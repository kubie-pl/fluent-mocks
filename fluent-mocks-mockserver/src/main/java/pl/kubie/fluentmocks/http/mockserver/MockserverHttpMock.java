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
package pl.kubie.fluentmocks.http.mockserver;

import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.mockserver.verify.VerificationTimes;
import pl.kubie.fluentmocks.http.api.HttpMock;
import pl.kubie.fluentmocks.http.api.HttpVerification;
import pl.kubie.fluentmocks.http.api.request.MockHttpRequestSpec;

import java.time.Duration;
import java.util.function.Consumer;

public class MockserverHttpMock implements HttpMock, HttpVerification {

  MockserverApi mockserverApi;
  MockserverHttpRequestSpec requestSpec;
  MockserverExpectations expectations;

  Duration awaitTimeout = null;

  public MockserverHttpMock(
      MockserverApi mockserverApi,
      MockserverHttpRequestSpec requestSpec,
      MockserverExpectations expectations) {
    this.mockserverApi = mockserverApi;
    this.requestSpec = requestSpec;
    this.expectations = expectations;
  }

  @Override
  public HttpMock await() {
    return await(Duration.ofSeconds(3));
  }

  @Override
  public HttpMock await(Duration timeout) {
    this.awaitTimeout = timeout;
    return this;
  }

  @Override
  public HttpVerification never() {
    return verify(VerificationTimes.never());
  }

  @Override
  public HttpVerification once() {
    return verify(VerificationTimes.once());
  }

  @Override
  public HttpVerification exactly(int times) {
    return verify(VerificationTimes.exactly(times));
  }

  @Override
  public HttpVerification atLeast(int times) {
    return verify(VerificationTimes.atLeast(times));
  }

  @Override
  public HttpVerification atMost(int times) {
    return verify(VerificationTimes.atMost(times));
  }

  @Override
  public HttpVerification between(int atLeast, int atMost) {
    return verify(VerificationTimes.between(atLeast, atMost));
  }

  @Override
  public HttpVerification matching(Consumer<MockHttpRequestSpec> onRequest) {
    onRequest.accept(requestSpec);
    return this;
  }

  @Override
  public HttpVerification verify() {
    return this;
  }

  private HttpVerification verify(VerificationTimes times) {
    execute(() -> mockserverApi.verify(requestSpec, times));
    return this;
  }

  private void execute(ThrowingRunnable assertion) {
    if (shouldAwait()) {
      Awaitility.await()
          .atMost(awaitTimeout)
          .untilAsserted(assertion);
    } else {
      try {
        assertion.run();
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }
    }
  }

  private boolean shouldAwait() {
    return awaitTimeout != null;
  }
}
