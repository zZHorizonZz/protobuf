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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import com.julienviet.protobuf.core.ProtoStream;
import com.julienviet.protobuf.core.json.ProtoJsonWriter;
import com.julienviet.protobuf.tests.core.support.basic.FieldLiteral;
import com.julienviet.protobuf.tests.core.support.basic.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.basic.ProtoWriter;
import com.julienviet.protobuf.tests.core.support.basic.SimpleMessage;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StreamingWriterTest {

  private JsonObject testWrite(SimpleMessage simpleMessage) throws Exception {
    ProtoStream stream = ProtoWriter.streamOf(simpleMessage);
    return testWrite(stream);
  }

  private JsonObject testWrite(ProtoStream stream) throws Exception {
    StringWriter out = new StringWriter();
    ProtoJsonWriter streamingProtoJsonWriter = new ProtoJsonWriter(out);
    streamingProtoJsonWriter.write(stream);
    return new JsonObject(out.toString());
  }

  @Test
  public void testSome() throws Exception {
    SimpleMessage simpleMessage = new SimpleMessage();
    simpleMessage.setStringField("the-string");
  }

  @Test
  public void testList() throws Exception {
    SimpleMessage simpleMessage = new SimpleMessage();
    simpleMessage.setStringListField(Arrays.asList("s1", "s2", "s3"));
    assertEquals(new JsonObject().put("stringListField", new JsonArray().add("s1").add("s2").add("s3")), testWrite(simpleMessage));
  }

  @Test
  public void testMore2() throws Exception {

    JsonObject res = testWrite(visitor -> {
      visitor.init(MessageLiteral.Recursive);
      visitor.visitString(FieldLiteral.Recursive_string, "0");
      visitor.<Integer>visitEmbedded(FieldLiteral.Recursive_embedded, List.of(0, 1, 2).iterator(), (i, v) -> {
        visitor.visitString(FieldLiteral.Recursive_string, "0-" + i);
        visitor.<Integer>visitEmbedded(FieldLiteral.Recursive_embedded, List.of(0).iterator(), (i2, v2) -> {
          visitor.visitString(FieldLiteral.Recursive_string, "0-" + i + "-" + i2);
        });
      });
      visitor.destroy();
    });
    JsonObject expected = new JsonObject();
    expected.put("string", "0");
    expected.put("embedded", new JsonArray()
      .add(new JsonObject()
        .put("string", "0-0")
        .put("embedded", new JsonArray()
          .add(new JsonObject()
            .put("string", "0-0-0")
          )
        )
      )
      .add(new JsonObject()
        .put("string", "0-1")
        .put("embedded", new JsonArray()
          .add(new JsonObject()
            .put("string", "0-1-0")
          )
        )
      )
      .add(new JsonObject()
        .put("string", "0-2")
        .put("embedded", new JsonArray()
          .add(new JsonObject()
            .put("string", "0-2-0")
          )
        )
      )
    );
    assertEquals(expected, res);
  }

  @Test
  public void testMap() throws Exception {
    SimpleMessage msg = new SimpleMessage();
    msg.getMapStringString().put("key1", "value1");
    msg.getMapStringString().put("key2", "value2");
    JsonObject jsonObject = testWrite(ProtoWriter.streamOf(msg));
    System.out.println(jsonObject);
  }

}
