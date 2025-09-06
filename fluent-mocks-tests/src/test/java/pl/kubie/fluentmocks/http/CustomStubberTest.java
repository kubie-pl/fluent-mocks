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
