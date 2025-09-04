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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mockserver.client.MockServerClient;
import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JacksonJsonSerializer;
import pl.kubie.fluentmocks.http.api.HttpStubber;

@RequiredArgsConstructor
public class MockserverHttpStubberConfig {

  private final ObjectMapper objectMapper;

  public HttpStubber stubber(String host, int port) {
    return new MockserverHttpStubber(
        new MockserverApi(new MockServerClient(host, port)),
        new JacksonJsonSerializer(objectMapper),
        new FileLoader()
    );
  }
}
