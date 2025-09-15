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


import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.assertj.core.api.ListAssert;
import org.jetbrains.annotations.NotNull;
import pl.kubie.fluentmocks.http.testing.StubberTest;
import pl.kubie.fluentmocks.http.testing.UseWiremock;
import pl.kubie.fluentmocks.http.wiremock.WireMockHttpStubber;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kubie.fluentmocks.http.scenarios.TestStubbing.stubGetEndpoint;

public class MockResetTest {
  @StubberTest
  @UseWiremock
  void wiremock_should_reset_stubs_properly(WireMockHttpStubber stubber) {
    // given
    assertNoStubs(stubber);
    assertJournal(stubber).isEmpty();
    stubber.with(stubGetEndpoint())
        .respond()
        .unlimited();
    // and
    assertStubCount(stubber, 1);

    // when
    stubber.clearMocks();

    // then
    assertNoStubs(stubber);
  }

  private static @NotNull ListAssert<ServeEvent> assertJournal(WireMockHttpStubber stubber) {
    return assertThat(stubber.client().getServeEvents());
  }

  private static ListAssert<StubMapping> assertStubCount(WireMockHttpStubber stubber, int count) {
    return getStubMappingListAssert(stubber).hasSize(count);
  }

  private static @NotNull ListAssert<StubMapping> getStubMappingListAssert(WireMockHttpStubber stubber) {
    return assertThat(stubber.client().allStubMappings().getMappings());
  }

  private static void assertNoStubs(WireMockHttpStubber stubber) {
    assertThat(stubber.client().allStubMappings().getMappings()).isEmpty();
  }
}
