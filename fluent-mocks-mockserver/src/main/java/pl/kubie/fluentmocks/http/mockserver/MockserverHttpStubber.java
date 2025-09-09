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

import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JsonSerializer;
import pl.kubie.fluentmocks.http.api.HttpMockSpec;
import pl.kubie.fluentmocks.http.api.HttpStubber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MockserverHttpStubber implements HttpStubber {

  private final MockserverApi mockserverApi;
  private final JsonSerializer jsonSerializer;
  private final FileLoader fileReader;
  private final List<MockserverHttpMock> stubs = new ArrayList<>();
  private final Consumer<HttpMockSpec> onEach;


  public MockserverHttpStubber(
      MockserverApi mockserverApi,
      JsonSerializer objectMapper,
      FileLoader fileReader,
      Consumer<HttpMockSpec> onEach
  ) {
    this.mockserverApi = mockserverApi;
    this.jsonSerializer = objectMapper;
    this.fileReader = fileReader;
    this.onEach = onEach;
  }

  @Override
  public HttpMockSpec stub() {
    var mock = new MockserverHttpMockSpec(
        mockserverApi,
        new MockserverHttpRequestSpec(new MockserverRequestBody(fileReader, jsonSerializer)),
        new MockserverHttpResponseSpec(new MockserverResponseBody(fileReader, jsonSerializer)),
        stubs::add
    );
    onEach.accept(mock);
    return mock;
  }

  @Override
  public String host() {
    return mockserverApi.host();
  }

  @Override
  public int port() {
    return mockserverApi.port();
  }

  @Override
  public void clearMocks() {
    stubs.forEach(stub -> mockserverApi.clear(stub.requestSpec.build()));
    mockserverApi.reset(); // todo it's possible bug in mockserver that it doesn't reset request logs
    stubs.clear();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
