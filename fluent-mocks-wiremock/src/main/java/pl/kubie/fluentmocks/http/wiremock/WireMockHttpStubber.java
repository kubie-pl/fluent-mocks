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

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JsonSerializer;
import pl.kubie.fluentmocks.http.api.HttpMockSpec;
import pl.kubie.fluentmocks.http.api.HttpStubber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class WireMockHttpStubber implements HttpStubber {

  private final WireMockClient wireMockClient;
  private final FileLoader fileLoader;
  private final JsonSerializer serializer;
  private final List<WireMockHttpMock> mocks = new ArrayList<>();
  private final Consumer<HttpMockSpec> onEach;

  @Override
  public HttpMockSpec stub() {
    var mock = new WireMockHttpMockSpec(
        request(),
        response(),
        wireMockClient,
        serializer,
        mocks::add
    );
    onEach.accept(mock);
    return mock;
  }

  private @NotNull WireMockHttpResponseSpec response() {
    return new WireMockHttpResponseSpec(new WireMockResponseBody(fileLoader, serializer));
  }

  private @NotNull WireMockHttpRequestSpec request() {
    return new WireMockHttpRequestSpec(new WireMockRequestBody(fileLoader, serializer));
  }

  @Override
  public String host() {
    return wireMockClient.host();
  }

  @Override
  public int port() {
    return wireMockClient.port();
  }

  @Override
  public void clearMocks() {
    wireMockClient.removeAll(mocks);
    mocks.clear();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  public WireMock client() {
    return wireMockClient.wireMock();
  }
}
