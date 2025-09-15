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

import org.mockserver.model.HttpRequest;
import pl.kubie.fluentmocks.http.api.request.MockHttpRequestSpec;
import pl.kubie.fluentmocks.http.api.request.RequestBody;
import pl.kubie.fluentmocks.http.api.request.UrlSpec;

import java.util.function.Consumer;

import static pl.kubie.fluentmocks.http.mockserver.MockserverPathParamValidator.validatePathParams;

public class MockserverHttpRequestSpec implements MockHttpRequestSpec {

  private final HttpRequest delegate;
  private final MockserverRequestBody body;
  private final MockserverUrl url;

  public MockserverHttpRequestSpec(MockserverRequestBody body) {
    this.body = body;
    this.delegate = new HttpRequest();
    this.url = new MockserverUrl(delegate);
  }

  @Override
  public MockHttpRequestSpec url(Consumer<UrlSpec> onUrl) {
    onUrl.accept(url);
    return this;
  }

  @Override
  public MockHttpRequestSpec method(String method) {
    delegate.withMethod(method);
    return this;
  }

  @Override
  public MockHttpRequestSpec header(String key, String value) {
    delegate.withHeader(key, value);
    return this;
  }

  @Override
  public MockHttpRequestSpec cookie(String key, String value) {
    delegate.withCookie(key, value);
    return this;
  }

  @Override
  public MockHttpRequestSpec body(Consumer<RequestBody> customizer) {
    customizer.accept(body);
    delegate.withBody(body.toMockserverBody());
    return this;
  }

  public HttpRequest build() {
    validatePathParams(delegate);
    return delegate;
  }
}
