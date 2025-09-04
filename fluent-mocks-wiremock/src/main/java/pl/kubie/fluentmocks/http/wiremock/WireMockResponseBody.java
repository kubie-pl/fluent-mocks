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

import com.github.tomakehurst.wiremock.http.Body;
import pl.kubie.fluentmocks.common.AbstractResponseBody;
import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JsonSerializer;

import java.nio.charset.StandardCharsets;

public class WireMockResponseBody extends AbstractResponseBody {

  public WireMockResponseBody(
      FileLoader fileLoader,
      JsonSerializer serializer
  ) {
    super(fileLoader, serializer);
  }

  public Body toWireMockBody() {
    return switch (format) {
      case RAW_BYTES -> Body.fromOneOf(bytes, null, null, null);
      case RAW -> Body.fromOneOf(null, new String(bytes, StandardCharsets.UTF_8), null, null);
      case JSON -> Body.fromJsonBytes(bytes);
      case EMPTY -> Body.none();
    };
  }
}
