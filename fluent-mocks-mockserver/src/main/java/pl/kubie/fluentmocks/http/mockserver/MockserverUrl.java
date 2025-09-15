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
package pl.kubie.fluentmocks.http.mockserver;

import lombok.RequiredArgsConstructor;
import org.mockserver.model.HttpRequest;
import pl.kubie.fluentmocks.http.api.request.UrlSpec;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MockserverUrl implements UrlSpec {

  private final HttpRequest request;

  @Override
  public UrlSpec url(String url) {
    request.withPath(url);
    return this;
  }

  @Override
  public UrlSpec queryParameter(String name, String value) {
    request.withQueryStringParameter(name, value);
    return this;
  }

  @Override
  public UrlSpec queryParameter(String name, List<String> value) {
    request.withQueryStringParameters(Map.of(name, value));
    return this;
  }

  @Override
  public UrlSpec pathParameter(String name, String value) {
    request.withPathParameter(name, value);
    return this;
  }
}
