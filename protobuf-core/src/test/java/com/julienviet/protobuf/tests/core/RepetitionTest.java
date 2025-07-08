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

import com.google.protobuf.MessageLite;
import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.tests.core.support.repetition.ProtoReader;
import com.julienviet.protobuf.tests.core.support.repetition.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.repetition.FieldLiteral;
import com.julienviet.protobuf.tests.core.support.repetition.RepetitionProto;
import org.junit.Test;

public class RepetitionTest extends RepetitionTestBase {

  @Test
  public void testParsePackedInt32Repetition() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(MessageLiteral.Packed);
    visitor.enterPacked(FieldLiteral.Packed_int32);
    visitor.visitInt32(FieldLiteral.Packed_int32, 0);
    visitor.visitInt32(FieldLiteral.Packed_int32, 1);
    visitor.leavePacked(FieldLiteral.Packed_int32);
    visitor.destroy();
    assertRepetition(RepetitionProto.Packed.newBuilder().addInt32(0).addInt32(1).build(), visitor);
  }

  @Test
  public void testParseUnpackedEmbeddedRepetition() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(MessageLiteral.Repeated);
    visitor.enter(FieldLiteral.Repeated_embedded);
    visitor.leave(FieldLiteral.Repeated_embedded);
    visitor.enter(FieldLiteral.Repeated_embedded);
    visitor.visitInt32(FieldLiteral.Embedded_value, 1);
    visitor.enterPacked(FieldLiteral.Embedded_packed);
    visitor.visitInt32(FieldLiteral.Embedded_packed, 1);
    visitor.visitInt32(FieldLiteral.Embedded_packed, 2);
    visitor.leavePacked(FieldLiteral.Embedded_packed);
    visitor.visitInt32(FieldLiteral.Embedded_unpacked, 3);
    visitor.visitInt32(FieldLiteral.Embedded_unpacked, 4);
    visitor.leave(FieldLiteral.Repeated_embedded);
    visitor.enter(FieldLiteral.Repeated_embedded);
    visitor.visitInt32(FieldLiteral.Embedded_value, 2);
    visitor.leave(FieldLiteral.Repeated_embedded);
    visitor.destroy();
    assertRepetition(RepetitionProto.Repeated.newBuilder()
      .addEmbedded(RepetitionProto.Embedded.newBuilder().setValue(0).build())
      .addEmbedded(RepetitionProto.Embedded.newBuilder().setValue(1).addPacked(1).addPacked(2).addUnpacked(3).addUnpacked(4).build())
      .addEmbedded(RepetitionProto.Embedded.newBuilder().setValue(2).build())
      .build(), visitor);
  }

  @Override
  protected void assertRepetition(MessageLite message, MessageType type, RecordingVisitor visitor) {
    byte[] bytes = message.toByteArray();
    ProtobufReader.parse(type, visitor.checker(), bytes);
  }

  protected  <T> T parseRepetition(MessageLite message, MessageType type) {
    byte[] bytes = message.toByteArray();
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(type, reader, bytes);
    return (T) reader.stack.pop();
  }
}
