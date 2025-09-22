/*
 * Copyright (C) 2025 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.vertx.protobuf.core.json;

import io.vertx.protobuf.core.json.jackson.Jackson;
import io.vertx.protobuf.core.json.jackson.JacksonDecoder;

import java.io.IOException;

public interface JsonDecoder {

  static JsonDecoder create(String json) {
    return new JacksonDecoder(Jackson.createParser(json));
  }

  boolean isCoercionException(IOException e);
  JsonTokenKind nextToken() throws IOException;
  JsonTokenKind currentToken() throws IOException;
  String fieldName() throws IOException;
  String text() throws IOException;
  boolean booleanValue() throws IOException;
  int intValue() throws IOException;
  long longValue() throws IOException;
  double doubleValue() throws IOException;
  float floatValue() throws IOException;
  boolean hasToken(JsonTokenKind kind) throws IOException;

  void close();

}
