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
package pl.kubie.fluentmocks.common;

import com.jayway.jsonpath.JsonPath;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

@UtilityClass
public class JsonPayload {
  public static byte[] overrideJson(
      byte[] byteArray,
      Charset encoding,
      String jsonPath,
      String json
  ) {
    return JsonPath.parse(new ByteArrayInputStream(byteArray))
        .map(JsonPath.compile(jsonPath), ((currentValue, configuration) -> configuration.jsonProvider().parse(json)))
        .jsonString()
        .getBytes(encoding);
  }
}
