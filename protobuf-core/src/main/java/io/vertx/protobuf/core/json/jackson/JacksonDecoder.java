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
package io.vertx.protobuf.core.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import io.vertx.protobuf.core.DecodeException;
import io.vertx.protobuf.core.json.JsonDecoder;
import io.vertx.protobuf.core.json.JsonTokenKind;

import java.io.IOException;

public class JacksonDecoder implements JsonDecoder {

  private final JsonParser parser;

  private static final JsonTokenKind[] map = new JsonTokenKind[100];
  private static final int[] map2 = new int[100];

  static {
    mapping(JsonTokenId.ID_STRING, JsonTokenKind.STRING);
    mapping(JsonTokenId.ID_NULL, JsonTokenKind.NULL);
    mapping(JsonTokenId.ID_FALSE, JsonTokenKind.FALSE);
    mapping(JsonTokenId.ID_TRUE, JsonTokenKind.TRUE);
    mapping(JsonTokenId.ID_NUMBER_FLOAT, JsonTokenKind.NUMBER_FLOAT);
    mapping(JsonTokenId.ID_NUMBER_INT, JsonTokenKind.NUMBER_INT);
    mapping(JsonTokenId.ID_FIELD_NAME, JsonTokenKind.FIELD_NAME);
    mapping(JsonTokenId.ID_START_OBJECT, JsonTokenKind.START_OBJECT);
    mapping(JsonTokenId.ID_END_OBJECT, JsonTokenKind.END_OBJECT);
    mapping(JsonTokenId.ID_START_ARRAY, JsonTokenKind.START_ARRAY);
    mapping(JsonTokenId.ID_END_ARRAY, JsonTokenKind.END_ARRAY);
  }

  private static void mapping(int id, JsonTokenKind k) {
    map[id] = k;
    map2[k.ordinal()] = id;
  }

  public JacksonDecoder(JsonParser parser) {
    this.parser = parser;
  }

  @Override
  public JsonTokenKind nextToken() throws IOException {
    JsonToken t = parser.nextToken();
    return t != null ? map[t.id()] : null;
  }

  @Override
  public JsonTokenKind currentToken() {
    JsonToken t = parser.currentToken();
    JsonTokenKind k = map[t.id()];
    assert k != null;
    return k;
  }

  @Override
  public String fieldName() throws IOException {
    return parser.currentName();
  }

  @Override
  public String text() throws IOException {
    return parser.getText();
  }

  @Override
  public boolean booleanValue() throws IOException {
    return parser.getBooleanValue();
  }

  @Override
  public int intValue() throws IOException  {
    return parser.getIntValue();
  }

  @Override
  public long longValue() throws IOException {
    return parser.getLongValue();
  }

  @Override
  public double doubleValue() throws IOException {
    return parser.getDoubleValue();
  }

  @Override
  public float floatValue() throws IOException {
    return parser.getFloatValue();
  }

  @Override
  public boolean hasToken(JsonTokenKind kind) throws IOException {
    int v = map2[kind.ordinal()];
    return parser.getCurrentToken().id() == v;
  }

  public boolean isCoercionException(IOException e) {
    return e instanceof InputCoercionException;
  }

  @Override
  public void close() {
    try {
      parser.close();
    } catch (IOException e) {
      throw new DecodeException(e);
    }
  }
}
