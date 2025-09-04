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

import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;

public record MockserverApi(MockServerClient mockServerClient) {

  public int port() {
    return mockServerClient.remoteAddress().getPort();
  }

  public String host() {
    return mockServerClient.remoteAddress().getHostString();
  }

  public void clear(HttpRequest request) {
    mockServerClient.clear(request);
  }

  public void reset() {
    mockServerClient.reset();
  }

  public MockserverHttpMock register(MockserverHttpRequestSpec request, MockserverHttpResponseSpec response, Times times) {
    var expectations = mockServerClient.when(request.build(), times).respond(response.build());
    return new MockserverHttpMock(
        this,
        request,
        new MockserverExpectations(expectations)
    );
  }

  public void verify(MockserverHttpRequestSpec requestSpec, VerificationTimes times) {
    mockServerClient.verify(requestSpec.build(), times);
  }

}
