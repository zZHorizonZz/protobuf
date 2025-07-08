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

import com.julienviet.protobuf.core.DecodeException;
import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.schema.DefaultMessageType;
import com.julienviet.protobuf.schema.ScalarType;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ProtobufReaderTest {

  @Test
  public void testReadInvalidTagWireType() {
    byte[] data = { 8 + 4 };
    testInvalidInput(data);
  }

  @Test
  public void testReadInvalidTagNumberType() {
    byte[] data = { 1 };
    testInvalidInput(data);
  }

  private void testInvalidInput(byte[] data) {
    DefaultMessageType msg = new DefaultMessageType("whatever");
    msg.addField(1, ScalarType.STRING);
    RecordingVisitor visitor = new RecordingVisitor();
    try {
      ProtobufReader.parse(msg, visitor, data);
      fail();
    } catch (DecodeException expected) {
    }
  }
}
