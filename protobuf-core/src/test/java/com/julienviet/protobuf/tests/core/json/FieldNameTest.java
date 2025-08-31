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
package com.julienviet.protobuf.tests.core.json;

import com.julienviet.protobuf.core.DecodeException;
import com.julienviet.protobuf.core.json.ProtoJsonReader;
import com.julienviet.protobuf.tests.core.RecordingVisitor;
import com.julienviet.protobuf.tests.core.support.basic.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.basic.ProtoReader;
import com.julienviet.protobuf.tests.core.support.basic.SimpleMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FieldNameTest {

  @Test
  public void testOriginalFieldName() {
    ProtoReader reader = new ProtoReader();
    ProtoJsonReader.parse("{\"string_field\":\"the-string\"}", MessageLiteral.SimpleMessage, reader);
    SimpleMessage pop = (SimpleMessage) reader.stack.pop();
    assertEquals("the-string", pop.getStringField());
  }

  @Test
  public void testInferredFieldName() {
    ProtoReader reader = new ProtoReader();
    ProtoJsonReader.parse("{\"stringField\":\"the-string\"}", MessageLiteral.SimpleMessage, reader);
    SimpleMessage pop = (SimpleMessage) reader.stack.pop();
    assertEquals("the-string", pop.getStringField());
  }

  @Test
  public void testUnknownFieldName() {
    ProtoReader reader = new ProtoReader();
    try {
      ProtoJsonReader.parse("{\"does_not_exist\":\"whatever\"}", MessageLiteral.SimpleMessage, reader);
      fail();
    } catch (DecodeException expected) {
    }
  }

  @Test
  public void testDuplicateFieldName() {
    ProtoReader reader = new ProtoReader();
    List<String> duplicates = List.of(
      "{\"stringField\":\"value1\",\"stringField\":\"value2\"}",
      "{\"string_field\":\"value1\",\"string_field\":\"value2\"}",
      "{\"stringField\":\"value1\",\"string_field\":\"value2\"}",
      "{\"string_field\":\"value1\",\"stringField\":\"value2\"}"
    );
    for (String duplicate : duplicates) {
      try {
        ProtoJsonReader.parse(duplicate, MessageLiteral.SimpleMessage, reader);
        fail();
      } catch (DecodeException expected) {
      }
    }
  }
}
