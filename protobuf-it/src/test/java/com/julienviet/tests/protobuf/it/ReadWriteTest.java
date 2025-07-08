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
package com.julienviet.tests.protobuf.it;

import com.julienviet.protobuf.core.ProtoStream;
import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.core.ProtobufWriter;
import com.julienviet.protobuf.it.MessageLiteral;
import com.julienviet.protobuf.it.ProtoReader;
import com.julienviet.protobuf.it.ProtoWriter;
import com.julienviet.protobuf.it.SimpleMessage;
import com.julienviet.protobuf.core.json.ProtoJsonReader;
import com.julienviet.protobuf.core.json.ProtoJsonWriter;
import org.junit.Test;

public class ReadWriteTest {

  @Test
  public void testProtobuf() {
    SimpleMessage msg = new SimpleMessage();
    msg.setLongField(3);
    msg.setStringField("the-string");
    byte[] bytes = ProtobufWriter.encodeToByteArray(ProtoWriter.streamOf(msg));
    ProtoStream stream = ProtobufReader.readerStream(MessageLiteral.SimpleMessage, bytes);
    ProtoReader.readSimpleMessage(stream);
  }

  @Test
  public void testProtojson() {
    SimpleMessage msg = new SimpleMessage();
    msg.setLongField(3);
    msg.setStringField("the-string");
    String json = ProtoJsonWriter.encode(ProtoWriter.streamOf(msg));
    ProtoStream stream = ProtoJsonReader.readStream(MessageLiteral.SimpleMessage, json);
    ProtoReader.readSimpleMessage(stream);
  }
}
