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

import com.github.tomakehurst.wiremock.matching.ContentPattern;
import org.jetbrains.annotations.NotNull;
import pl.kubie.fluentmocks.common.AbstractRequestBody;
import pl.kubie.fluentmocks.common.FileLoader;
import pl.kubie.fluentmocks.common.JsonSerializer;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class WireMockRequestBody extends AbstractRequestBody {

  public WireMockRequestBody(
      FileLoader fileLoader,
      JsonSerializer serializer
  ) {
    super(fileLoader, serializer);
  }

  public Optional<ContentPattern<?>> toContentPattern() {
    return switch (format) {
      case EMPTY -> Optional.empty();
      case RAW_BYTES -> Optional.of(binaryEqualTo(bytes));
      case RAW -> Optional.of(equalTo(asString()));
      case JSON -> Optional.of(equalToJson(asString(), false, true));
    };
  }

  private @NotNull String asString() {
    return new String(bytes, UTF_8);
  }
}
