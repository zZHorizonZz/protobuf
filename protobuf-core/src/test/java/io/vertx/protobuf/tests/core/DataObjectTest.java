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
package io.vertx.protobuf.tests.core;

import com.google.protobuf.ByteString;
import io.vertx.protobuf.core.ProtoStream;
import io.vertx.protobuf.core.ProtobufReader;
import io.vertx.protobuf.core.ProtobufWriter;
import io.vertx.protobuf.tests.core.support.importing.Container;
import io.vertx.protobuf.tests.core.support.importing.ImportingProto;
import io.vertx.protobuf.tests.core.support.basic.MessageLiteral;
import io.vertx.protobuf.tests.core.support.basic.ProtoReader;
import io.vertx.protobuf.tests.core.support.basic.ProtoWriter;
import io.vertx.protobuf.tests.core.support.basic.SimpleMessage;
import io.vertx.protobuf.tests.core.support.basic.TestProto;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class DataObjectTest {

  @Test
  public void testReadWrite() throws Exception {
    TestProto.SimpleMessage expected = TestProto.SimpleMessage.newBuilder()
      .setStringField("hello")
      .setBytesField(ByteString.copyFromUtf8("hello"))
      .setInt32Field(5)
      .addAllStringListField(Arrays.asList("s-1", "s-2"))
      .putMapStringString("the-key-1", "the-value-1")
      .putMapStringString("the-key-2", "the-value-2")
      .putMapStringInt32("the-key-1", 4)
      .putMapStringInt32("the-key-2", 5)
      .build();
    ProtoStream stream = ProtobufReader.readerStream(MessageLiteral.SimpleMessage, expected.toByteArray());
    SimpleMessage obj = ProtoReader.readSimpleMessage(stream);
    assertNotNull(obj);
    byte[] result = ProtobufWriter.encodeToByteArray(ProtoWriter.streamOf(obj));
    TestProto.SimpleMessage actual = TestProto.SimpleMessage.parseFrom(result);
    assertEquals(expected, actual);
  }

  @Test
  public void testReadSimple() {
    byte[] bytes = TestProto.SimpleMessage.newBuilder()
      .setStringField("hello")
      .setBytesField(ByteString.copyFromUtf8("hello"))
      .setInt32Field(5)
      .addAllStringListField(Arrays.asList("s-1", "s-2"))
      .putMapStringString("the-key-1", "the-value-1")
      .putMapStringString("the-key-2", "the-value-2")
      .putMapStringInt32("the-key-1", 4)
      .putMapStringInt32("the-key-2", 5)
      .build().toByteArray();
    io.vertx.protobuf.tests.core.support.basic.ProtoReader reader = new io.vertx.protobuf.tests.core.support.basic.ProtoReader();
    ProtobufReader.parse(MessageLiteral.SimpleMessage, reader, bytes);
    SimpleMessage msg = (SimpleMessage) reader.stack.pop();
    assertEquals("hello", msg.getStringField());
    assertEquals("hello", new String(msg.getBytesField(), StandardCharsets.UTF_8));
    assertEquals(5, (int)msg.getInt32Field());
    assertEquals(Arrays.asList("s-1", "s-2"), msg.getStringListField());
    assertEquals(Map.of("the-key-1", "the-value-1", "the-key-2", "the-value-2"), msg.getMapStringString());
    assertEquals(Map.of("the-key-1", 4, "the-key-2", 5), msg.getMapStringInt32());
  }

  @Test
  public void testWriteSimple() throws Exception {
    SimpleMessage value = new SimpleMessage()
      .setStringField("the-string")
      .setBytesField("the-bytes".getBytes(StandardCharsets.UTF_8))
      .setInt32Field(5)
      .setStringListField(Arrays.asList("s-1", "s-2"))
      .setMapStringString(Map.of("the-key-1", "the-value-1", "the-key-2", "the-value-2"))
      .setMapStringInt32(Map.of("the-key", 4));
    byte[] result = ProtobufWriter.encodeToByteArray(visitor -> {
      io.vertx.protobuf.tests.core.support.basic.ProtoWriter.emit(value, visitor);
    });
    TestProto.SimpleMessage res = TestProto.SimpleMessage.parseFrom(result);
    assertEquals("the-string", res.getStringField());
    assertEquals("the-bytes", res.getBytesField().toStringUtf8());
    assertEquals(5, res.getInt32Field());
    assertEquals(Arrays.asList("s-1", "s-2"), res.getStringListFieldList());
    assertEquals(Map.of("the-key-1", "the-value-1", "the-key-2", "the-value-2"), res.getMapStringStringMap());
    assertEquals(Map.of("the-key", 4), res.getMapStringInt32Map());
  }

  @Test
  public void testReadImports() {
    byte[] bytes = ImportingProto.Container.newBuilder().setSimpleMessage(TestProto.SimpleMessage.newBuilder().setStringField("the-string").build()).build().toByteArray();
    io.vertx.protobuf.tests.core.support.importing.ProtoReader reader = new io.vertx.protobuf.tests.core.support.importing.ProtoReader();
    ProtobufReader.parse(io.vertx.protobuf.tests.core.support.importing.MessageLiteral.Container, reader, bytes);
    Container msg = (Container) reader.stack.pop();
    assertEquals("the-string", msg.getSimpleMessage().getStringField());
  }

  @Test
  public void testWriteImports() throws Exception {
    Container container = new Container();
    container.setSimpleMessage(new SimpleMessage().setStringField("the-string"));
    byte[] result = ProtobufWriter.encodeToByteArray(io.vertx.protobuf.tests.core.support.importing.ProtoWriter.streamOf(container));
    ImportingProto.Container res = ImportingProto.Container.parseFrom(result);
    assertEquals("the-string", res.getSimpleMessage().getStringField());
  }
}
