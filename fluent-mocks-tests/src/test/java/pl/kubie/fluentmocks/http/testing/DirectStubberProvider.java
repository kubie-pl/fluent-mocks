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
package pl.kubie.fluentmocks.http.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import pl.kubie.fluentmocks.http.api.HttpStubber;
import pl.kubie.fluentmocks.http.mockserver.MockserverHttpStubberConfig;
import pl.kubie.fluentmocks.http.wiremock.WireMockHttpStubberConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public class DirectStubberProvider implements ArgumentsProvider {

  public static final String STUBBERS_KEY = "stubbers";
  private final Map<String, HttpStubber> stubbers = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    var testMethod = context.getTestMethod().orElseThrow();
    var stubbers = getStubbers(context, testMethod);
    var methodParameters = testMethod.getParameters();
    var parameterAnnotations = testMethod.getAnnotationsByType(Parameter.class);

    List<Map<String, Object>> namedCombinations = generateNamedCombinations(
        stubbers,
        parameterAnnotations
    );

    return namedCombinations
        .stream()
        .map(combinationMap -> {
          Object[] orderedArgs = new Object[methodParameters.length];
          for (int i = 0; i < methodParameters.length; i++) {
            var param = methodParameters[i];
            // The stubber is resolved by its type
            if (HttpStubber.class.isAssignableFrom(param.getType())) {
              orderedArgs[i] = combinationMap.get("__STUBBER__");
            } else {
              // Other parameters are resolved by their name
              orderedArgs[i] = combinationMap.get(param.getName());
            }
            if (orderedArgs[i] == null) {
              throw new IllegalStateException(
                  "Could not find a value for parameter '" +
                      param.getName() +
                      "' in test method '" +
                      testMethod.getName() +
                      "'. Ensure a @Parameter annotation exists with this name."
              );
            }
          }
          return Arguments.of(orderedArgs);
        });
  }

  private List<Map<String, Object>> generateNamedCombinations(
      List<HttpStubber> stubbers,
      Parameter[] parameterAnnotations
  ) {
    List<Map<String, Object>> combinations = new ArrayList<>();
    for (HttpStubber stubber : stubbers) {
      Map<String, Object> initialMap = new HashMap<>();
      initialMap.put("__STUBBER__", stubber); // Use a special key for the stubber
      combinations.add(initialMap);
    }

    // Iteratively expand the combinations with each @Parameter's values
    for (Parameter paramAnnotation : parameterAnnotations) {
      List<Map<String, Object>> nextCombinations = new ArrayList<>();
      for (Map<String, Object> combination : combinations) {
        for (String value : paramAnnotation.values()) {
          Map<String, Object> newCombination = new HashMap<>(combination);
          newCombination.put(paramAnnotation.name(), value);
          nextCombinations.add(newCombination);
        }
      }
      combinations = nextCombinations;
    }
    return combinations;
  }

  private @NotNull List<HttpStubber> getStubbers(ExtensionContext context, Method testMethod) {
    List<HttpStubber> stubbers = new ArrayList<>();
    if (noneMatch(testMethod)) {
      stubbers.add(wiremockArgument());
      stubbers.add(mockserverArgument());
    }
    if (testMethod.isAnnotationPresent(UseWiremock.class)) {
      stubbers.add(wiremockArgument());
    }
    if (testMethod.isAnnotationPresent(UseMockserver.class)) {
      stubbers.add(mockserverArgument());
    }
    context.getStore(ExtensionContext.Namespace.GLOBAL).put(STUBBERS_KEY, stubbers);
    return stubbers;
  }

  private static boolean noneMatch(Method testMethod) {
    return !testMethod.isAnnotationPresent(UseWiremock.class) && !testMethod.isAnnotationPresent(UseMockserver.class);
  }

  private HttpStubber mockserverArgument() {
    return stubbers.computeIfAbsent(UseMockserver.KEY, key -> mockserverHttpStubber());
  }

  private HttpStubber wiremockArgument() {
    return stubbers.computeIfAbsent(UseWiremock.KEY, key -> wiremockHttpStubber());
  }

  private HttpStubber mockserverHttpStubber() {
    log.info("Creating mockserver stubber for port {}", MockContainers.mockserverContainer.getServerPort());
    return MockserverHttpStubberConfig.configure()
        .objectMapper(objectMapper)
        .host("localhost")
        .port(MockContainers.mockserverContainer.getServerPort())
        .build();
  }

  private HttpStubber wiremockHttpStubber() {
    log.info("Creating wiremock stubber for port {}", MockContainers.wiremockContainer.getPort());
    return WireMockHttpStubberConfig.configure()
        .objectMapper(objectMapper)
        .local(MockContainers.wiremockContainer.getPort())
        .build();
  }

}
