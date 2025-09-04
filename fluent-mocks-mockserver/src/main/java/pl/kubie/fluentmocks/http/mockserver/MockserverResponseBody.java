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

import org.mockserver.model.BinaryBody;
import org.mockserver.model.BodyWithContentType;
import org.mockserver.model.JsonBody;
import org.mockserver.model.StringBody;
import pl.kubie.fluentmocks.common.AbstractResponseBody;
import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JsonSerializer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MockserverResponseBody extends AbstractResponseBody {

  public MockserverResponseBody(
      FileLoader fileLoader,
      JsonSerializer serializer
  ) {
    super(fileLoader, serializer);
  }

  public BodyWithContentType<?> toMockserverBody() {
    return switch (format) {
      case EMPTY -> StringBody.exact("");
      case RAW -> StringBody.exact(new String(bytes, UTF_8));
      case RAW_BYTES -> BinaryBody.binary(bytes);
      case JSON -> JsonBody.json(new String(bytes, UTF_8));
    };
  }
}
