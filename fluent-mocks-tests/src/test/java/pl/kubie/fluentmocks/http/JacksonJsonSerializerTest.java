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
package pl.kubie.fluentmocks.http;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.kubie.fluentmocks.common.JacksonJsonSerializer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonJsonSerializerTest {

  @Test
  void should_throw_exception_when_unable_to_write() {
    var tested = new JacksonJsonSerializer(new ObjectMapper());

    assertThatThrownBy(() -> tested.toJson(new Boom()))
        .isNotNull();
  }

  record Boom() {
    @JsonValue
    String value() {
      throw new RuntimeException("Boom");
    }
  }
}