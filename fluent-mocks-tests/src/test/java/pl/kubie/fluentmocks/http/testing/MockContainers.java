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

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.stream.Stream;

@Slf4j
public class MockContainers {

  public static final WireMockContainer wiremockContainer;
  public static final MockServerContainer mockserverContainer;

  static {
    wiremockContainer = wiremockContainer();
    mockserverContainer = mockserverContainer();
    Startables.deepStart(Stream.of(wiremockContainer, mockserverContainer)).join();
  }

  private static MockServerContainer mockserverContainer() {
    return new MockServerContainer(
        DockerImageName.parse("mockserver/mockserver:latest")
    );
//        .withLogConsumer(new Slf4jLogConsumer(log));
  }

  private static WireMockContainer wiremockContainer() {

    return new WireMockContainer("wiremock/wiremock:3x")
//        .withLogConsumer(new Slf4jLogConsumer(log))
        .withBanner();
  }

}
