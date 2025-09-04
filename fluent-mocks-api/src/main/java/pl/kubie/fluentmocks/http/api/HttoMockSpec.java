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

package pl.kubie.fluentmocks.http.api;

import pl.kubie.fluentmocks.http.api.request.MockHttpRequestSpec;
import pl.kubie.fluentmocks.http.api.response.MockHttpResponseSpec;

import java.util.function.Consumer;

public interface HttoMockSpec {

  default HttoMockSpec when(Consumer<MockHttpRequestSpec> onRequest) {
    onRequest.accept(request());
    return this;
  }

  default HttpMockTimes respond(Consumer<MockHttpResponseSpec> onResponse) {
    onResponse.accept(response());
    return times();
  }

  default HttpMockTimes respond() {
    return times();
  }

  default HttoMockSpec with(Consumer<HttoMockSpec> onStub) {
    onStub.accept(this);
    return this;
  }

  HttpMockTimes times();

  MockHttpRequestSpec request();

  MockHttpResponseSpec response();
}
