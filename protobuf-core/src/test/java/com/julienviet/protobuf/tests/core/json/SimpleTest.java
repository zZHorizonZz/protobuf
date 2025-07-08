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

import com.google.protobuf.util.JsonFormat;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.core.json.ProtoJsonReader;
import com.julienviet.protobuf.tests.core.support.basic.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.basic.ProtoReader;
import com.julienviet.protobuf.tests.core.support.basic.ProtoWriter;
import com.julienviet.protobuf.tests.core.support.basic.SimpleMessage;
import com.julienviet.protobuf.tests.core.support.basic.TestProto;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SimpleTest {

  @Parameterized.Parameters
  public static Collection<Object[]> params() {
    return ProtoJsonWriterProvider.all();
  }

  private final ProtoJsonWriterProvider writerProvider;

  public SimpleTest(ProtoJsonWriterProvider writerProvider) {
    this.writerProvider = writerProvider;
  }

  @Test
  public void testSimple() throws Exception {

    String json = JsonFormat.printer().print(TestProto.SimpleMessage.newBuilder()
      .setStringField("the-string")
      .setInt32Field(4)
      .addStringListField("s1")
      .addStringListField("s2")
      .build());

    ProtoReader pr = new ProtoReader();

    ProtoJsonReader.parse(json, MessageLiteral.SimpleMessage, pr);

    SimpleMessage pop = (SimpleMessage) pr.stack.pop();

    assertEquals("the-string", pop.getStringField());
    assertEquals(4, (int)pop.getInt32Field());
    assertEquals(Arrays.asList("s1", "s2"), pop.getStringListField());

    JsonObject actual = writerProvider.encodeToObject(v -> ProtoWriter.emit(pop, v));

    assertEquals(new JsonObject(json), actual);
  }

  @Ignore
  @Test
  public void testTransmute() {
    byte[] bytes = TestProto.SimpleMessage.newBuilder()
      .setStringField("the-string")
      .setInt32Field(4)
      .addStringListField("s1")
      .addStringListField("s2")
      .build()
      .toByteArray();
    JsonObject json = writerProvider.encodeToObject(visitor -> {
      ProtobufReader.parse(MessageLiteral.SimpleMessage, visitor, bytes);
    });
    JsonObject expected = new JsonObject()
      .put("stringField", "the-string")
      .put("int32Field", 4)
      .put("stringListField", new JsonArray().add("s1").add("s2"));
    assertEquals(expected, json);
  }
}
