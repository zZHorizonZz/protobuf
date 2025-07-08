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

import com.julienviet.protobuf.core.ProtobufWriter;
import com.julienviet.protobuf.schema.DefaultField;
import com.julienviet.protobuf.schema.DefaultMessageType;
import com.julienviet.protobuf.schema.DefaultSchema;
import com.julienviet.protobuf.schema.ScalarType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProtobufWriterTest {

  @Test
  public void testWritePackedUInt64() {
    DefaultSchema schema = new DefaultSchema();
    DefaultMessageType msg = schema.of("msg");
    DefaultField field = msg.addField(builder -> builder.number(1).type(ScalarType.UINT64).repeated(true));
    byte[] output = ProtobufWriter.encodeToByteArray(visitor -> {
      visitor.init(msg);
      visitor.enterPacked(field);
      visitor.visitUInt64(field, -1);
      visitor.leavePacked(field);
      visitor.destroy();
    });
    assertEquals(10, output[1]);
  }

  @Test
  public void testSizeOfField() {
    DefaultSchema schema = new DefaultSchema();
    DefaultMessageType rootMsg = schema.of("msg");
    DefaultMessageType nestedMsg = schema.of("msg");
    DefaultField nestedField = rootMsg.addField(1, nestedMsg);
    DefaultField fixed32Field = nestedMsg.addField(builder -> builder.number(89).type(ScalarType.FIXED32).repeated(true));
    byte[] output = ProtobufWriter.encodeToByteArray(visitor -> {
      visitor.init(rootMsg);
      visitor.enter(nestedField);
      visitor.enterPacked(fixed32Field);
      visitor.visitFixed32(fixed32Field, 1);
      visitor.leavePacked(fixed32Field);
      visitor.leave(nestedField);
      visitor.destroy();
    });
    assertEquals(7, output[1]);
  }
}
