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
