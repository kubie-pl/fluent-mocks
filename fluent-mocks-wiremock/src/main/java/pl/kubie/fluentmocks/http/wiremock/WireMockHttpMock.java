/*
   Copyright 2025 the original author or authors

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package pl.kubie.fluentmocks.http.wiremock;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import pl.kubie.fluentmocks.http.api.HttpMock;

import java.time.Duration;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.lessThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;

public class WireMockHttpMock implements HttpMock {

  WireMockClient wireMockClient;
  RequestPatternBuilder requestPattern;
  List<StubMapping> mappings;
  Duration awaitTimeout = null;

  public WireMockHttpMock(
      RequestPatternBuilder requestPattern,
      List<StubMapping> mappings,
      WireMockClient wireMockClient
  ) {
    this.requestPattern = requestPattern;
    this.mappings = mappings;
    this.wireMockClient = wireMockClient;
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
  public void verifyNever() {
    verifyExactly(0);
  }

  @Override
  public void verifyOnce() {
    verifyExactly(1);
  }

  @Override
  public void verifyExactly(int times) {
    verify(exactly(times));
  }

  @Override
  public void verifyAtLeast(int times) {
    verify(moreThanOrExactly(times));
  }

  @Override
  public void verifyAtMost(int times) {
    verify(lessThanOrExactly(times));
  }

  @Override
  public void verifyBetween(int atLeast, int atMost) {
    verifyAtLeast(atLeast);
    verifyAtMost(atMost);
  }

  private void verify(CountMatchingStrategy expectedCount) {
    ThrowingRunnable verification = () -> wireMockClient.verifyThat(expectedCount, requestPattern);
    if (awaitTimeout != null) {
      Awaitility.await()
          .atMost(awaitTimeout)
          .untilAsserted(verification);
    } else {
      try {
        verification.run();
      } catch (Throwable throwable) {
        throw new AssertionError(throwable);
      }
    }
  }

}
