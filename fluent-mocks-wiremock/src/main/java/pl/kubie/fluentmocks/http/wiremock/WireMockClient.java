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

package pl.kubie.fluentmocks.http.wiremock;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record WireMockClient(String host, int port, WireMock wireMock) {

  public WireMockClient(String host, int port) {
    this(host, port, new WireMock(host, port));
  }

  public void reset() {
    log.info("Resetting WireMock setup");
    wireMock.resetRequests();
    wireMock.resetMappings();
  }

  public StubMapping register(MappingBuilder mappingBuilder) {
    log.info("Registering WireMock mapping {} {} {}", host, port, mappingBuilder);
    return wireMock.register(mappingBuilder);
  }

  public void setSingleScenarioState(String scenario, String state) {
    log.info("Setting single scenario {} to state {}", scenario, state);
    wireMock.setSingleScenarioState(scenario, state);
  }

  public void verifyThat(CountMatchingStrategy expectedCount, RequestPatternBuilder requestPattern) {
    log.info("Verifying that {} requests matching request pattern {}", expectedCount, requestPattern);
    wireMock.verifyThat(expectedCount, requestPattern);
  }
}
