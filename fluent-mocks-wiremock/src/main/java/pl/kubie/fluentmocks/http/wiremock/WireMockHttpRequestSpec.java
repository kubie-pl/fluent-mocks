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


import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ExactMatchMultiValuePattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import pl.kubie.fluentmocks.http.api.request.MockHttpRequestSpec;
import pl.kubie.fluentmocks.http.api.request.RequestBody;
import pl.kubie.fluentmocks.http.api.request.UrlSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.github.tomakehurst.wiremock.http.RequestMethod.fromString;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class WireMockHttpRequestSpec implements MockHttpRequestSpec {

  private final MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());

  private final Map<String, String> cookies = new HashMap<>();

  private final WireMockUrl url = new WireMockUrl();

  private final WireMockRequestBody body;

  public WireMockHttpRequestSpec(WireMockRequestBody body) {
    this.body = body;
  }

  private String method = "ANY";


  @Override
  public MockHttpRequestSpec url(Consumer<UrlSpec> onUrl) {
    onUrl.accept(url);
    return this;
  }

  @Override
  public MockHttpRequestSpec method(String method) {
    this.method = method;
    return this;
  }

  @Override
  public MockHttpRequestSpec header(String key, String value) {
    headers.add(key, value);
    return this;
  }

  @Override
  public MockHttpRequestSpec cookie(String key, String value) {
    cookies.put(key, value);
    return this;
  }

  @Override
  public MockHttpRequestSpec body(Consumer<RequestBody> bodyCustomization) {
    bodyCustomization.accept(body);
    return this;
  }

  public MappingBuilder mapping() {
    var request = WireMock.request(method, urlPathTemplate(url.asString()));
    url.pathParams().forEach(entry -> request.withPathParam(entry.getKey(), WireMock.equalTo(entry.getValue())));
    headers.forEach((header, values) -> request.withHeader(header, exactMatch(values, WireMock::equalTo)));
    cookies.forEach((cookie, value) -> request.withCookie(cookie, WireMock.equalTo(value)));
    url.getQueryParams().forEach((param, values) -> request.withQueryParam(param, exactMatch(values, WireMock::equalTo)));
    body.toContentPattern().ifPresent(request::withRequestBody);
    return request;
  }

  public RequestPatternBuilder pattern() {
    var pattern = newRequestPattern(fromString(method), urlPathTemplate(url.asString()));
    url.pathParams().forEach(entry -> pattern.withPathParam(entry.getKey(), WireMock.equalTo(entry.getValue())));
    headers.forEach((header, values) -> pattern.withHeader(header, exactMatch(values, WireMock::equalTo)));
    cookies.forEach((cookie, value) -> pattern.withCookie(cookie, WireMock.equalTo(value)));
    url.getQueryParams().forEach((param, values) -> pattern.withQueryParam(param, exactMatch(values, WireMock::equalTo)));
    body.toContentPattern().ifPresent(pattern::withRequestBody);
    return pattern;
  }

  private static @NotNull ExactMatchMultiValuePattern exactMatch(List<String> values, Function<String, StringValuePattern> patternFactory) {
    return values.stream()
        .map(patternFactory)
        .collect(collectingAndThen(toList(), ExactMatchMultiValuePattern::new));
  }
}
