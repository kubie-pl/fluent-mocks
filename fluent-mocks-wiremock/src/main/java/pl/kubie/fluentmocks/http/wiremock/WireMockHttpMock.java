/**
 * Copyright 2025 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.kubie.fluentmocks.http.wiremock;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.awaitility.Awaitility;
import pl.kubie.fluentmocks.common.ThrowingRunnable;
import pl.kubie.fluentmocks.http.api.HttpMock;
import pl.kubie.fluentmocks.http.api.HttpVerification;

import java.time.Duration;
import java.util.List;

public class WireMockHttpMock implements HttpMock, HttpVerification {

  WireMockHttpRequestSpec request;
  WireMockClient wireMockClient;
  List<StubMapping> mappings;
  Duration awaitTimeout = null;

  public WireMockHttpMock(
      WireMockHttpRequestSpec request,
      List<StubMapping> mappings,
      WireMockClient wireMockClient
  ) {
    this.mappings = mappings;
    this.wireMockClient = wireMockClient;
    this.request = request;
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
    return verify(WireMock.exactly(0));
  }

  @Override
  public HttpVerification once() {
    return verify(WireMock.exactly(1));
  }

  @Override
  public HttpVerification exactly(int times) {
    return verify(WireMock.exactly(times));
  }

  @Override
  public HttpVerification atLeast(int times) {
    return verify(WireMock.moreThanOrExactly(times));
  }

  @Override
  public HttpVerification atMost(int times) {
    return verify(WireMock.lessThanOrExactly(times));
  }

  @Override
  public HttpVerification between(int atLeast, int atMost) {
    return atLeast(atLeast).atMost(atMost);
  }

  private HttpVerification verify(CountMatchingStrategy expectedCount) {
    ThrowingRunnable verification = () -> wireMockClient.verifyThat(expectedCount, request.pattern());
    if (awaitTimeout != null) {
      Awaitility.await()
          .atMost(awaitTimeout)
          .untilAsserted(verification::run);
    } else {
      verification.run();
    }
    return this;
  }

  @Override
  public HttpVerification verify() {
    return this;
  }
}
