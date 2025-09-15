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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import pl.kubie.fluentmocks.http.api.HttpMockSpec;
import pl.kubie.fluentmocks.http.api.HttpStubber;
import pl.kubie.fluentmocks.http.junit.CustomStubber;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CustomStubberTest {

  HttpStubber stubber = Mockito.mock(HttpStubber.class);

  @RegisterExtension
  TestStubber testStubber = new TestStubber(stubber);

  @Test
  public void should_invoke_stubber_and_clear_stubs() {
    testStubber.test();
    verify(stubber, times(1)).stub();
  }

  public static class TestStubber extends CustomStubber {

    public TestStubber(HttpStubber httpStubber) {
      super(httpStubber);
    }

    HttpMockSpec test() {
      return httpStubber.stub();
    }
  }
}
