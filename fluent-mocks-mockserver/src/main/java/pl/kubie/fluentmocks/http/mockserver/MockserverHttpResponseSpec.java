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

import org.mockserver.model.HttpResponse;
import pl.kubie.fluentmocks.http.api.response.MockHttpResponseSpec;
import pl.kubie.fluentmocks.http.api.response.ResponseBody;

import java.time.Duration;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MockserverHttpResponseSpec implements MockHttpResponseSpec {

  private final HttpResponse delegate;
  private final MockserverResponseBody body;

  public MockserverHttpResponseSpec(MockserverResponseBody body) {
    this.body = body;
    this.delegate = new HttpResponse();
  }

  @Override
  public MockHttpResponseSpec body(Consumer<ResponseBody> customizer) {
    customizer.accept(body);
    return this;
  }

  @Override
  public MockHttpResponseSpec statusCode(int statusCode) {
    delegate.withStatusCode(statusCode);
    return this;
  }

  @Override
  public MockHttpResponseSpec header(String name, String value) {
    delegate.withHeader(name, value);
    return this;
  }

  @Override
  public MockHttpResponseSpec cookie(String name, String value) {
    delegate.withCookie(name, value);
    return this;
  }

  @Override
  public MockHttpResponseSpec delayed(Duration delay) {
    delegate.withDelay(MILLISECONDS, delay.toMillis());
    return this;
  }

  public HttpResponse build() {
    delegate.withBody(body.toMockserverBody());
    return delegate;
  }
}
