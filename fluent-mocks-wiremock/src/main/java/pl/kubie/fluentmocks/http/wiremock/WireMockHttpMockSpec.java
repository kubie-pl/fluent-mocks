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
import pl.kubie.fluentmocks.common.JsonSerializer;
import pl.kubie.fluentmocks.http.api.HttpMockSpec;
import pl.kubie.fluentmocks.http.api.HttpMockTimes;
import pl.kubie.fluentmocks.http.api.request.MockHttpRequestSpec;
import pl.kubie.fluentmocks.http.api.response.MockHttpResponseSpec;

import java.util.function.Consumer;

@Value
public class WireMockHttpMockSpec implements HttpMockSpec {

  WireMockHttpRequestSpec request;
  WireMockHttpResponseSpec response;
  WireMockClient wireMock;
  JsonSerializer serializer;
  Consumer<WireMockHttpMock> onMockCreated;

  @Override
  public HttpMockTimes times() {
    return new WireMockHttpTimes(
        request,
        response,
        wireMock,
        new WireMockStubbingReporter(serializer),
        onMockCreated
    );
  }

  @Override
  public MockHttpRequestSpec request() {
    return request;
  }

  @Override
  public MockHttpResponseSpec response() {
    return response;
  }
}
