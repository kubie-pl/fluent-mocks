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

package pl.kubie.fluentmocks.http.mockserver;

import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.mockserver.verify.VerificationTimes;
import pl.kubie.fluentmocks.http.api.HttpMock;

import java.time.Duration;

import static org.mockserver.verify.VerificationTimes.atLeast;
import static org.mockserver.verify.VerificationTimes.atMost;
import static org.mockserver.verify.VerificationTimes.between;
import static org.mockserver.verify.VerificationTimes.exactly;
import static org.mockserver.verify.VerificationTimes.once;

public class MockserverHttpMock implements HttpMock {

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
  public void verifyNever() {
    verify(VerificationTimes.never());
  }

  @Override
  public void verifyOnce() {
    verify(once());
  }

  @Override
  public void verifyExactly(int times) {
    verify(exactly(times));
  }

  @Override
  public void verifyAtLeast(int times) {
    verify(atLeast(times));
  }

  @Override
  public void verifyAtMost(int times) {
    verify(atMost(times));
  }

  @Override
  public void verifyBetween(int atLeast, int atMost) {
    verify(between(atLeast, atMost));
  }

  private void verify(VerificationTimes times) {
    execute(() -> mockserverApi.verify(requestSpec, times));
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
