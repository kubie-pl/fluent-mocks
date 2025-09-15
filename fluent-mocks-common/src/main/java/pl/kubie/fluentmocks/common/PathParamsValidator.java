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
package pl.kubie.fluentmocks.common;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

@UtilityClass
public class PathParamsValidator {

  private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{([^/}]+)}");

  public static void validatePathParams(Set<String> pathParams, String url) {
    var missingParams = missingParams(url, pathParams);
    if (!missingParams.isEmpty()) {
      throw new IllegalArgumentException("Missing value for path parameter(s) " + missingParams + " in URL template '" + url + "'");
    }
  }

  public static Set<String> missingParams(String url, Collection<String> pathParams) {
    return urlParamNames(url)
        .stream()
        .filter(it -> !pathParams.contains(it))
        .collect(toSet());
  }

  public static Set<String> urlParamNames(String url) {
    var matcher = PATH_PARAM_PATTERN.matcher(url);
    var result = new HashSet<String>();
    while (matcher.find()) {
      result.add(matcher.group(1));
    }
    return result;
  }

}
