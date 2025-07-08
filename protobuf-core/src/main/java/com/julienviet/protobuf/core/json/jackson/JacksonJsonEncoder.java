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
package com.julienviet.protobuf.core.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.julienviet.protobuf.core.EncodeException;
import com.julienviet.protobuf.core.json.JsonEncoder;

import java.io.IOException;

public class JacksonJsonEncoder implements JsonEncoder {

  private final JsonGenerator generator;

  public JacksonJsonEncoder(JsonGenerator generator) {
    this.generator = generator;
  }

  @Override
  public void writeStartArray() {
    try {
      generator.writeStartArray();
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public void writeEndArray() {
    try {
      generator.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public void writeStartObject() {
    try {
      generator.writeStartObject();
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public void writeEndObject() {
    try {
      generator.writeEndObject();
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public void writeFieldName(String name) {
    try {
      generator.writeFieldName(name);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public void writeBinary(byte[] bytes) {
    try {
      generator.writeBinary(bytes);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeFloat(float f) {
    try {
      generator.writeNumber(f);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeDouble(double d) {
    try {
      generator.writeNumber(d);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeBoolean(boolean v) {
    try {
      generator.writeBoolean(v);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeInt(int v) {
    try {
      generator.writeNumber(v);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeLong(long v) {
    try {
      generator.writeNumber(v);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeString(String s) {
    try {
      generator.writeString(s);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  public void writeNull() {
    try {
      generator.writeNull();
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public void close() {
    try {
      generator.close();
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }
}
