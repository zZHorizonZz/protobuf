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
package com.julienviet.protobuf.tests.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.julienviet.protobuf.core.ProtobufWriter;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.tests.core.support.repetition.Enumerated;
import com.julienviet.protobuf.tests.core.support.repetition.FieldLiteral;
import com.julienviet.protobuf.tests.core.support.repetition.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.repetition.Packed;
import com.julienviet.protobuf.tests.core.support.repetition.ProtoWriter;
import com.julienviet.protobuf.tests.core.support.repetition.Repeated;
import com.julienviet.protobuf.tests.core.support.repetition.RepetitionProto;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public abstract class RepetitionTestBase {

  @Test
  public void testParseUnpackedInt32Repetition() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(MessageLiteral.Repeated);
    visitor.visitInt32(FieldLiteral.Repeated_int32, 0);
    visitor.visitInt32(FieldLiteral.Repeated_int32, 1);
    visitor.destroy();
    assertRepetition(RepetitionProto.Repeated.newBuilder().addInt32(0).addInt32(1).build(), visitor);
  }

  @Test
  public void testParseUnpackedStringRepetition() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(MessageLiteral.Repeated);
    visitor.visitString(FieldLiteral.Repeated_string, "s1");
    visitor.visitString(FieldLiteral.Repeated_string, "s2");
    visitor.destroy();
    assertRepetition(RepetitionProto.Repeated.newBuilder().addString("s1").addString("s2").build(), visitor);
  }

  protected void assertRepetition(RepetitionProto.Repeated repeated, RecordingVisitor visitor) {
    assertRepetition(repeated, MessageLiteral.Repeated, visitor);
  }

  protected void assertRepetition(RepetitionProto.Packed repeated, RecordingVisitor visitor) {
    assertRepetition(repeated, MessageLiteral.Packed, visitor);
  }

  protected abstract void assertRepetition(MessageLite message, MessageType type, RecordingVisitor visitor);

  @Test
  public void testParseRepetition() throws Exception {
    RepetitionProto.Repeated r = RepetitionProto.Repeated.newBuilder()
      .addString("0")
      .addString("1")
      .addBytes(ByteString.copyFromUtf8("0"))
      .addBytes(ByteString.copyFromUtf8("1"))
      .addInt32(0)
      .addInt32(1)
      .addInt64(0)
      .addInt64(1)
      .addUint32(0)
      .addUint32(1)
      .addUint64(0)
      .addUint64(1)
      .addSint32(0)
      .addSint32(1)
      .addSint64(0)
      .addSint64(1)
      .addBool(true)
      .addBool(false)
      .addEnum(RepetitionProto.Enumerated.constant_0)
      .addEnum(RepetitionProto.Enumerated.constant_1)
      .addFixed64(0)
      .addFixed64(1)
      .addSfixed64(0)
      .addSfixed64(1)
      .addDouble(0D)
      .addDouble(1D)
      .addFixed32(0)
      .addFixed32(1)
      .addSfixed32(0)
      .addSfixed32(1)
      .addFloat(0F)
      .addFloat(1F)
      .build();
    Repeated msg = parseRepetition(r, MessageLiteral.Repeated);
    assertEquals(Arrays.asList("0", "1"), msg.getString());
    assertEquals(2, msg.getBytes().size());
    assertEquals("0", new String(msg.getBytes().get(0), StandardCharsets.UTF_8));
    assertEquals("1", new String(msg.getBytes().get(1), StandardCharsets.UTF_8));
    assertEquals(Arrays.asList(0, 1), msg.getInt32());
    assertEquals(Arrays.asList(0L, 1L), msg.getInt64());
    assertEquals(Arrays.asList(0, 1), msg.getUint32());
    assertEquals(Arrays.asList(0L, 1L), msg.getUint64());
    assertEquals(Arrays.asList(0L, 1L), msg.getSint64());
    assertEquals(Arrays.asList(0, 1), msg.getFixed32());
    assertEquals(Arrays.asList(true, false), msg.getBool());
    assertEquals(Arrays.asList(com.julienviet.protobuf.tests.core.support.repetition.Enumerated.constant_0, Enumerated.constant_1), msg.getEnum());
    assertEquals(Arrays.asList(0L, 1L), msg.getFixed64());
    assertEquals(Arrays.asList(0L, 1L), msg.getSfixed64());
    assertEquals(Arrays.asList(0d, 1d), msg.getDouble());
    assertEquals(Arrays.asList(0, 1), msg.getSint32());
    assertEquals(Arrays.asList(0, 1), msg.getSfixed32());
    assertEquals(Arrays.asList(0f, 1f), msg.getFloat());
  }

  @Test
  public void testParsePackedRepetition() throws Exception {
    RepetitionProto.Packed p = RepetitionProto.Packed.newBuilder()
      .addInt32(0)
      .addInt32(1)
      .addInt64(0)
      .addInt64(1)
      .addUint32(0)
      .addUint32(1)
      .addUint64(0)
      .addUint64(1)
      .addSint32(0)
      .addSint32(1)
      .addSint64(0)
      .addSint64(1)
      .addBool(true)
      .addBool(false)
      .addEnum(RepetitionProto.Enumerated.constant_0)
      .addEnum(RepetitionProto.Enumerated.constant_1)
      .addFixed64(0)
      .addFixed64(1)
      .addSfixed64(0)
      .addSfixed64(1)
      .addDouble(0D)
      .addDouble(1D)
      .addFixed32(0)
      .addFixed32(1)
      .addSfixed32(0)
      .addSfixed32(1)
      .addFloat(0F)
      .addFloat(1F)
      .build();
    Packed msg = parseRepetition(p, MessageLiteral.Packed);
    assertEquals(Arrays.asList(0, 1), msg.getInt32());
    assertEquals(Arrays.asList(0L, 1L), msg.getInt64());
    assertEquals(Arrays.asList(0, 1), msg.getUint32());
    assertEquals(Arrays.asList(0L, 1L), msg.getUint64());
    assertEquals(Arrays.asList(0L, 1L), msg.getSint64());
    assertEquals(Arrays.asList(0, 1), msg.getFixed32());
    assertEquals(Arrays.asList(true, false), msg.getBool());
    assertEquals(Arrays.asList(Enumerated.constant_0, Enumerated.constant_1), msg.getEnum());
    assertEquals(Arrays.asList(0L, 1L), msg.getFixed64());
    assertEquals(Arrays.asList(0L, 1L), msg.getSfixed64());
    assertEquals(Arrays.asList(0d, 1d), msg.getDouble());
    assertEquals(Arrays.asList(0, 1), msg.getSint32());
    assertEquals(Arrays.asList(0, 1), msg.getSfixed32());
    assertEquals(Arrays.asList(0f, 1f), msg.getFloat());
  }

  @Test
  public void testWritePackedRepetition() throws Exception {
    RepetitionProto.Packed p = RepetitionProto.Packed.newBuilder()
      .addInt32(0)
      .addInt32(1)
      .addInt32(2)
      .addInt32(3)
      .addInt32(4)
      .build();
    Packed msg = parseRepetition(p, MessageLiteral.Packed);
    assertEquals(Arrays.asList(0, 1, 2, 3, 4), msg.getInt32());
    byte[] actual = ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(msg, visitor));
    assertEquals(5, actual[1]);
    RepetitionProto.Packed blah = RepetitionProto.Packed.parseFrom(actual);
    assertEquals(Arrays.asList(0, 1, 2, 3, 4), blah.getInt32List());
  }

  protected  abstract <T> T parseRepetition(MessageLite message, MessageType type);

  }
