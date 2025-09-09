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

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JacksonJsonSerializer;
import pl.kubie.fluentmocks.http.api.HttpMockSpec;

import java.util.function.Consumer;

public class WireMockHttpStubberConfig {

  private ObjectMapper objectMapper;
  private String host;
  private int port;
  private Consumer<HttpMockSpec> onEach = mock -> {
  };


  public static WireMockHttpStubberConfig configure() {
    return new WireMockHttpStubberConfig();
  }

  public WireMockHttpStubberConfig objectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    return this;
  }

  public WireMockHttpStubberConfig host(String host) {
    this.host = host;
    return this;
  }

  public WireMockHttpStubberConfig port(int port) {
    this.port = port;
    return this;
  }

  public WireMockHttpStubberConfig onEach(Consumer<HttpMockSpec> onEach) {
    this.onEach = onEach;
    return this;
  }

  public WireMockHttpStubber build() {
    var serializer = new JacksonJsonSerializer(objectMapper);
    return new WireMockHttpStubber(
        new WireMockClient(host, port, serializer),
        new FileLoader(),
        serializer,
        onEach
    );
  }

  public WireMockHttpStubberConfig local(Integer port) {
    return host("localhost").port(port);
  }
}
