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
package pl.kubie.fluentmocks.http.mockserver;

import lombok.experimental.UtilityClass;
import org.mockserver.model.HttpRequest;

import java.util.List;

import static pl.kubie.fluentmocks.common.PathParamsValidator.urlParamNames;

@UtilityClass
public class MockserverPathParamValidator {

  public static void validatePathParams(HttpRequest request) {
    var missingParams = missingPathParams(request);

    if (!missingParams.isEmpty()) {
      throw new IllegalArgumentException("Missing path parameters: " + missingParams);
    }
  }

  private static List<String> missingPathParams(HttpRequest request) {
    var urlParamNames = urlParamNames(request.getPath().getValue());
    var actualParamsNames = actualParamNames(request);
    return urlParamNames.stream()
        .filter(it -> !actualParamsNames.contains(it))
        .toList();
  }

  private static List<String> actualParamNames(HttpRequest request) {
    return request.getPathParameterList().stream().map(it1 -> it1.getName().getValue()).toList();
  }
}
