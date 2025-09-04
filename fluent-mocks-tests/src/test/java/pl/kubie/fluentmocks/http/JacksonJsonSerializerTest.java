package pl.kubie.fluentmocks.http;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.kubie.fluentmocks.common.JacksonJsonSerializer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonJsonSerializerTest {

  @Test
  void should_throw_exception_when_unable_to_write() {
    var tested = new JacksonJsonSerializer(new ObjectMapper());

    assertThatThrownBy(() -> tested.toJson(new Boom()))
        .isNotNull();
  }

  record Boom() {
    @JsonValue
    String value() {
      throw new RuntimeException("Boom");
    }
  }
}