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

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import pl.kubie.fluentmocks.http.api.response.MockHttpResponseSpec;
import pl.kubie.fluentmocks.http.api.response.ResponseBody;

import java.time.Duration;
import java.util.function.Consumer;

public class WireMockHttpResponseSpec implements MockHttpResponseSpec {

  private final ResponseDefinitionBuilder delegate = new ResponseDefinitionBuilder();
  private final WireMockResponseBody body;

  public WireMockHttpResponseSpec(WireMockResponseBody responseBodyDefinition) {
    this.body = responseBodyDefinition;
  }

  @Override
  public MockHttpResponseSpec body(Consumer<ResponseBody> bodyCustomizer) {
    bodyCustomizer.accept(this.body);
    return this;
  }

  @Override
  public MockHttpResponseSpec statusCode(int statusCode) {
    delegate.withStatus(statusCode);
    return this;
  }

  @Override
  public MockHttpResponseSpec header(String name, String value) {
    delegate.withHeader(name, value);
    return this;
  }

  @Override
  public MockHttpResponseSpec cookie(String name, String value) {
    return header("Set-Cookie", "%s=%s".formatted(name, value));
  }

  @Override
  public MockHttpResponseSpec delayed(Duration delay) {
    delegate.withFixedDelay(Long.valueOf(delay.toMillis()).intValue());
    return this;
  }

  public ResponseDefinitionBuilder build() {
    delegate.withResponseBody(body.toWireMockBody());
    return delegate;
  }

}
