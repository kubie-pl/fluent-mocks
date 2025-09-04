package pl.kubie.fluentmocks.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kubie.fluentmocks.common.FileLoader;

class FileLoaderTest {

  @Test
  void should_throw_exception_when_unable_to_read() {
    Assertions.assertThatThrownBy(() -> new FileLoader().load("invalid"))
        .isNotNull();
  }

}