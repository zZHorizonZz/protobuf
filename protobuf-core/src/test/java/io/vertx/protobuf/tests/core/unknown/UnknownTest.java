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
package io.vertx.protobuf.tests.core.unknown;

import com.google.protobuf.ByteString;
import com.google.protobuf.UnknownFieldSet;
import io.vertx.protobuf.core.ProtobufReader;
import io.vertx.protobuf.core.ProtobufWriter;
import io.vertx.protobuf.schema.Field;
import io.vertx.protobuf.schema.WireType;
import io.vertx.protobuf.tests.core.RecordingVisitor;
import io.vertx.protobuf.tests.core.support.unknown.Message;
import io.vertx.protobuf.tests.core.support.unknown.MessageLiteral;
import io.vertx.protobuf.tests.core.support.unknown.ProtoReader;
import io.vertx.protobuf.tests.core.support.unknown.ProtoWriter;
import io.vertx.protobuf.tests.core.support.unknown.UnknownProto;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UnknownTest {

  @Test
  public void testUnknownLengthDelimited() throws Exception {
    byte[] bytes = UnknownProto.Message.newBuilder()
      .setUnknownFields(UnknownFieldSet.newBuilder()
        .addField(2, UnknownFieldSet.Field.newBuilder().addLengthDelimited(ByteString.copyFromUtf8("Hello")).build())
        .addField(3, UnknownFieldSet.Field.newBuilder().addLengthDelimited(ByteString.copyFromUtf8("World")).build())
        .build()
      ).build().toByteArray();
    RecordingVisitor visitor = new RecordingVisitor();
    ProtobufReader.parse(MessageLiteral.Message, visitor, bytes);
    RecordingVisitor.Checker checker = visitor.checker();
    checker.init(MessageLiteral.Message);
    Field uf2 = MessageLiteral.Message.unknownField(2, WireType.LEN);
    checker.visitBytes(uf2, "Hello".getBytes(StandardCharsets.UTF_8));
    Field uf3 = MessageLiteral.Message.unknownField(3, WireType.LEN);
    checker.visitBytes(uf3, "World".getBytes(StandardCharsets.UTF_8));
    checker.destroy();
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUnknownFixed32() throws Exception {
    byte[] bytes = UnknownProto.Message.newBuilder()
      .setUnknownFields(UnknownFieldSet.newBuilder().addField(2, UnknownFieldSet.Field.newBuilder().addFixed32(15).build()).build())
      .build().toByteArray();
    RecordingVisitor visitor = new RecordingVisitor();
    ProtobufReader.parse(MessageLiteral.Message, visitor, bytes);
    RecordingVisitor.Checker checker = visitor.checker();
    checker.init(MessageLiteral.Message);
    checker.visitFixed32(MessageLiteral.Message.unknownField(2, WireType.I32), 15);
    checker.destroy();
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUnknownFixed64() throws Exception {
    byte[] bytes = UnknownProto.Message.newBuilder()
      .setUnknownFields(UnknownFieldSet.newBuilder().addField(2, UnknownFieldSet.Field.newBuilder().addFixed64(15L).build()).build())
      .build().toByteArray();
    RecordingVisitor visitor = new RecordingVisitor();
    ProtobufReader.parse(MessageLiteral.Message, visitor, bytes);
    RecordingVisitor.Checker checker = visitor.checker();
    checker.init(MessageLiteral.Message);
    checker.visitFixed64(MessageLiteral.Message.unknownField(2, WireType.I64), 15L);
    checker.destroy();
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUnknownVarInt() throws Exception {
    byte[] bytes = UnknownProto.Message.newBuilder()
      .setUnknownFields(UnknownFieldSet.newBuilder().addField(2, UnknownFieldSet.Field.newBuilder().addVarint(15L).build()).build())
      .build().toByteArray();
    RecordingVisitor visitor = new RecordingVisitor();
    ProtobufReader.parse(MessageLiteral.Message, visitor, bytes);
    RecordingVisitor.Checker checker = visitor.checker();
    checker.init(MessageLiteral.Message);
    checker.visitInt64(MessageLiteral.Message.unknownField(2, WireType.VARINT), 15L);
    checker.destroy();
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testMessage() throws Exception {
    byte[] bytes = UnknownProto.Message.newBuilder()
      .setUnknownFields(UnknownFieldSet.newBuilder()
        .addField(2, UnknownFieldSet.Field.newBuilder().addLengthDelimited(ByteString.copyFromUtf8("Hello")).build())
        .addField(3, UnknownFieldSet.Field.newBuilder().addLengthDelimited(ByteString.copyFromUtf8("World")).build())
        .addField(4, UnknownFieldSet.Field.newBuilder().addFixed64(15L).addFixed64(20L).build())
        .addField(5, UnknownFieldSet.Field.newBuilder().addFixed32(17).build())
        .addField(6, UnknownFieldSet.Field.newBuilder().addVarint(18L).build())
        .build()
      ).build().toByteArray();
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Message, reader, bytes);
    Message msg = (Message) reader.stack.pop();
    assertNotNull(msg.unknownFields());
    Map<Field, List<Object>> map = toMap(msg.unknownFields());
    assertEquals(Arrays.asList(15L, 20L), map.get(MessageLiteral.Message.unknownField(4, WireType.I64)));
    assertEquals(Collections.singletonList(17), map.get(MessageLiteral.Message.unknownField(5, WireType.I32)));
    assertEquals(Collections.singletonList(18L), map.get(MessageLiteral.Message.unknownField(6, WireType.VARINT)));
    List<byte[]> l1 = (List) map.get(MessageLiteral.Message.unknownField(2, WireType.LEN));
    assertEquals(1, l1.size());
    assertEquals("Hello", new String(l1.get(0), StandardCharsets.UTF_8));
    List<byte[]> l2 = (List) map.get(MessageLiteral.Message.unknownField(3, WireType.LEN));
    assertEquals(1, l2.size());
    assertEquals("World", new String(l2.get(0), StandardCharsets.UTF_8));
    bytes = ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(msg, visitor));
    UnknownProto.Message protoMsg = UnknownProto.Message.parseFrom(bytes);
    String stringUtf8 = protoMsg.getUnknownFields().getField(2).getLengthDelimitedList().get(0).toStringUtf8();
    assertEquals("Hello", stringUtf8);
  }

  private static <K, V> Map<K, V> toMap(Iterable<Map.Entry<K, V>> entries) {
    Map<K, V> map = new HashMap<>();
    entries.forEach(entry -> {
      map.put(entry.getKey(), entry.getValue());
    });
    return map;
  }
}
