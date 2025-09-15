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
package pl.kubie.fluentmocks.http.wiremock;

import lombok.Value;
import pl.kubie.fluentmocks.http.api.HttpMock;
import pl.kubie.fluentmocks.http.api.HttpMockTimes;

import java.util.List;
import java.util.function.Consumer;

@Value
public class WireMockHttpTimes implements HttpMockTimes {
  WireMockHttpRequestSpec request;
  WireMockHttpResponseSpec response;
  WireMockClient wireMock;
  WireMockStubbingReporter reporter;
  Consumer<WireMockHttpMock> onMockCreated;

  @Override
  public HttpMock unlimited() {
    var requestMapping = request.mapping();
    var responseDefinition = response.build();
    var mapping = wireMock.register(requestMapping.willReturn(responseDefinition));
    reporter.report(mapping);
    var mock = new WireMockHttpMock(request.requestPattern(), List.of(mapping), wireMock);
    onMockCreated.accept(mock);
    return mock;
  }

  @Override
  public HttpMock exactly(int times) {
    var mock = new WireMockScenarioHttpTimes(request, response, wireMock)
        .times(times);
    onMockCreated.accept(mock);
    return mock;
  }

  @Override
  public HttpMock once() {
    return exactly(1);
  }
}
