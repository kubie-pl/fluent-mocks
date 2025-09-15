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

import lombok.RequiredArgsConstructor;
import pl.kubie.fluentmocks.http.api.http.body.FileBody;
import pl.kubie.fluentmocks.http.api.http.body.JsonBody;
import pl.kubie.fluentmocks.http.api.http.body.RawBody;
import pl.kubie.fluentmocks.http.api.response.ResponseBody;

import static java.nio.charset.StandardCharsets.UTF_8;
import static pl.kubie.fluentmocks.common.JsonPayload.overrideJson;
import static pl.kubie.fluentmocks.common.PayloadFormat.JSON;
import static pl.kubie.fluentmocks.common.PayloadFormat.RAW;
import static pl.kubie.fluentmocks.common.PayloadFormat.RAW_BYTES;

@RequiredArgsConstructor
public class AbstractResponseBody
    implements
    ResponseBody,
    JsonBody<ResponseBody>,
    FileBody<ResponseBody>,
    RawBody<ResponseBody> {
  private final FileLoader fileLoader;
  private final JsonSerializer serializer;

  protected byte[] bytes;
  protected PayloadFormat format = PayloadFormat.EMPTY;

  @Override
  public FileBody<ResponseBody> file(String path) {
    bytes = fileLoader.load(path);
    return this;
  }

  @Override
  public RawBody<ResponseBody> raw(byte[] payload) {
    this.bytes = payload;
    this.format = RAW_BYTES;
    return this;
  }

  public JsonBody<ResponseBody> json(byte[] payload) {
    bytes = payload;
    format = JSON;
    return this;
  }

  @Override
  public RawBody<ResponseBody> raw(String payload) {
    bytes = payload.getBytes(UTF_8);
    format = RAW;
    return this;
  }

  @Override
  public JsonBody<ResponseBody> json(Object payload) {
    return json(serializer.toJson(payload));
  }

  @Override
  public JsonBody<ResponseBody> json(String payload) {
    return json(payload.getBytes(UTF_8));
  }

  @Override
  public JsonBody<ResponseBody> override(
      String jsonPath,
      String json
  ) {
    bytes = overrideJson(bytes, UTF_8, jsonPath, json);
    return this;
  }

  @Override
  public JsonBody<ResponseBody> override(
      String jsonPath,
      Object object
  ) {
    return override(
        jsonPath,
        serializer.toJson(object)
    );
  }

  @Override
  public JsonBody<ResponseBody> json() {
    return json(bytes);
  }

  @Override
  public RawBody<ResponseBody> raw() {
    return raw(bytes);
  }
}
