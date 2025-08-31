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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TSFBuilder;
import com.julienviet.protobuf.core.DecodeException;

import java.io.IOException;
import java.io.Writer;

public class Jackson {

  // Make this pluggable
  static final JsonFactory factory;

  static {
    TSFBuilder<?, ?> tsfBuilder = JsonFactory.builder();
    JsonFactory f = tsfBuilder.build();
    // Non-standard JSON but we allow C style comments in our JSON
    f.configure(JsonParser.Feature.ALLOW_COMMENTS, false);
    factory = f;
  }

  public static JsonParser createParser(String str) {
    try {
      return factory.createParser(str);
    } catch (IOException e) {
      throw new DecodeException(e);
    }
  }

  public static JsonGenerator createGenerator(Writer writer) {
    try {
      return factory.createGenerator(writer);
    } catch (IOException e) {
      throw new DecodeException(e);
    }
  }
}
