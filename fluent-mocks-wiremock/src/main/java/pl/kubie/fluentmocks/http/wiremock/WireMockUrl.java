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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import pl.kubie.fluentmocks.http.api.request.UrlSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static pl.kubie.fluentmocks.common.PathParamsValidator.validatePathParams;

@Getter
@RequiredArgsConstructor
public class WireMockUrl implements UrlSpec {

  private final MultiValueMap<String, String> queryParams = new MultiValueMapAdapter<>(new HashMap<>());
  private final Map<String, String> pathParams = new HashMap<>();
  private String value;


  @Override
  public UrlSpec url(String url) {
    value = url;
    return this;
  }

  @Override
  public UrlSpec queryParameter(String name, String value) {
    queryParams.add(name, value);
    return this;
  }

  @Override
  public UrlSpec queryParameter(String name, List<String> value) {
    queryParams.addAll(name, value);
    return this;
  }

  @Override
  public UrlSpec pathParameter(String name, String value) {
    pathParams.put(name, value);
    return this;
  }

  public String asString() {
    return value;
  }

  public Stream<Map.Entry<String, String>> pathParams() {
    validatePathParams(pathParams.keySet(), value);
    return pathParams.entrySet().stream();
  }

}
