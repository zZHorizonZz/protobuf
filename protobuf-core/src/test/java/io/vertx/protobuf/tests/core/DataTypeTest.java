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

import com.google.protobuf.MessageLite;
import io.vertx.protobuf.core.ProtobufReader;
import io.vertx.protobuf.core.ProtobufWriter;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.tests.core.support.datatypes.EnumTypes;
import io.vertx.protobuf.tests.core.support.datatypes.Enumerated;
import io.vertx.protobuf.tests.core.support.datatypes.FieldLiteral;
import io.vertx.protobuf.tests.core.support.datatypes.MessageLiteral;
import io.vertx.protobuf.tests.core.support.datatypes.ProtoReader;
import io.vertx.protobuf.tests.core.support.datatypes.ProtoWriter;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataTypeTest extends DataTypeTestBase {

  protected void testDataType(RecordingVisitor visitor, MessageType messageType, MessageLite expected) throws Exception {
    byte[] bytes = expected.toByteArray();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtobufReader.parse(messageType, checker, bytes);
    assertTrue(checker.isEmpty());
    bytes = ProtobufWriter.encodeToByteArray(visitor::apply);
    assertEquals(expected, expected.getParserForType().parseFrom(bytes));
  }

  @Test
  public void testReadOversizedBoolean() throws Exception {
    byte[] data = { (byte)(BOOL.number() * 8), -128, -128, -128, -128, -128, -128, -128, -128, -128, 1 };
    RecordingVisitor visitor = new RecordingVisitor();
    ProtobufReader.parse(SCALAR_TYPES, visitor, data);
    RecordingVisitor.Checker checker = visitor.checker();
    checker.init(SCALAR_TYPES);
    checker.visitBool(BOOL, true);
  }

  @Test
  public void testUnknownEnum() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(MessageLiteral.EnumTypes);
    visitor.visitEnum(FieldLiteral.EnumTypes__enum, -1); // Unknown
    visitor.destroy();
    byte[] buffer = ProtobufWriter.encodeToByteArray(visitor::apply);
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.EnumTypes, reader, buffer);
    EnumTypes pop = (EnumTypes) reader.stack.pop();
    Enumerated enumerated = pop.getEnum();
    assertTrue(enumerated.isUnknown());
    assertEquals(-1, enumerated.number());
    try {
      assertNull(enumerated.name());
      fail();
    } catch (IllegalStateException expected) {
    }
    assertNull(enumerated.asEnum());
    byte[] res = ProtobufWriter.encodeToByteArray(v -> ProtoWriter.emit(pop, v));
    RecordingVisitor.Checker checker = visitor.checker();
    ProtobufReader.parse(MessageLiteral.EnumTypes, checker, res);
    assertTrue(checker.isEmpty());
  }
}
