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
import io.vertx.protobuf.core.json.jackson.JacksonJsonEncoder;

import java.io.Writer;

public interface JsonEncoder {

  static JsonEncoder create(Writer writer) {
    return new JacksonJsonEncoder(Jackson.createGenerator(writer));
  }

  void writeStartObject();
  void writeEndObject();
  void writeStartArray();
  void writeEndArray();
  void writeFieldName(String name);
  void writeBinary(byte[] bytes);
  void writeFloat(float f);
  void writeDouble(double d);
  void writeBoolean(boolean v);
  void writeInt(int v);
  void writeLong(long v);
  void writeString(String s);
  void writeNull();
  void close();

}
