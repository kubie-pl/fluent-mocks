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

package pl.kubie.fluentmocks.http.testing;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import pl.kubie.fluentmocks.http.api.HttpStubber;

import java.util.List;

import static pl.kubie.fluentmocks.http.testing.DirectStubberProvider.STUBBERS_KEY;

public class StubberCleanUpExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) {
    stubbers(context).forEach(HttpStubber::clearMocks);
  }

  private static List<HttpStubber> stubbers(ExtensionContext context) {
    return (List<HttpStubber>) context.getStore(ExtensionContext.Namespace.GLOBAL).get(STUBBERS_KEY);
  }
}
