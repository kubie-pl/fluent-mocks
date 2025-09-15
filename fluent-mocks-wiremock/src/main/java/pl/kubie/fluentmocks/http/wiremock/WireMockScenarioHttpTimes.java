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

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.Value;

import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@Value
public class WireMockScenarioHttpTimes {
  WireMockHttpRequestSpec request;
  WireMockHttpResponseSpec response;
  WireMockClient wireMockClient;

  public WireMockHttpMock times(int times) {
    String scenario = UUID.randomUUID().toString();
    var mappings = IntStream.range(0, times)
        .mapToObj(i -> registerStep(scenario, step(i), step(i + 1)))
        .toList();
    var finalMapping = registerFinalStep(scenario, step(times));
    wireMockClient.setSingleScenarioState(scenario, "step-0");
    return new WireMockHttpMock(
        request,
        Stream.concat(mappings.stream(), Stream.of(finalMapping)).toList(),
        wireMockClient
    );
  }

  private StubMapping registerStep(String scenario, String source, String target) {
    var requestMapping = request.mapping()
        .inScenario(scenario)
        .whenScenarioStateIs(source)
        .willSetStateTo(target)
        .willReturn(response.build());
    return wireMockClient.register(requestMapping);
  }

  private StubMapping registerFinalStep(String scenario, String step) {
    var requestMapping = request.mapping()
        .inScenario(scenario)
        .whenScenarioStateIs(step)
        .willSetStateTo("Completed")
        .willReturn(aResponse().withStatus(404));
    return wireMockClient.register(requestMapping);
  }

  private static String step(int i) {
    return "step-%s".formatted(i);
  }
}
