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
import com.julienviet.protobuf.core.ProtobufWriter;
import com.julienviet.protobuf.tests.core.support.presence.Default;
import com.julienviet.protobuf.tests.core.support.presence.Optional;
import com.julienviet.protobuf.tests.core.support.presence.PresenceProto;
import com.julienviet.protobuf.tests.core.support.presence.ProtoWriter;
import com.julienviet.protobuf.tests.core.support.presence.Repeated;
import com.julienviet.protobuf.tests.core.support.presence.Enumerated;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class PresenceTest {

  @Test
  public void testDefaultPresence() {
    Default msg = new Default();
    msg.setString("");
    msg.setBytes(new byte[0]);
    msg.setInt32(0);
    msg.setInt64(0L);
    msg.setUint32(0);
    msg.setUint64(0L);
    msg.setSint32(0);
    msg.setSint64(0L);
    msg.setBool(false);
    msg.setEnum(Enumerated.constant_0);
    msg.setFixed64(0L);
    msg.setSfixed64(0L);
    msg.setFloat(0f);
    msg.setFixed32(0);
    msg.setSfixed32(0);
    msg.setDouble(0D);
    byte[] bytes = ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(msg, visitor));
    PresenceProto.Default.Builder builder = PresenceProto.Default.newBuilder();
    byte[] expected = builder
      .build()
      .toByteArray();
    assertEquals(expected.length, bytes.length);
  }

  @Test
  public void testOptionalPresence() {
    Optional msg = new Optional();
    msg.setString("");
    msg.setBytes(new byte[0]);
    msg.setInt32(0);
    msg.setInt64(0L);
    msg.setUint32(0);
    msg.setUint64(0L);
    msg.setSint32(0);
    msg.setSint64(0L);
    msg.setBool(false);
    msg.setEnum(Enumerated.constant_1);
    msg.setFixed64(0L);
    msg.setSfixed64(0L);
    msg.setDouble(0D);
    msg.setFixed32(0);
    msg.setSfixed32(0);
    msg.setFloat(0f);
    byte[] bytes = ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(msg, visitor));
    PresenceProto.Optional.Builder builder = PresenceProto.Optional.newBuilder()
      .setString("")
      .setBytes(ByteString.EMPTY)
      .setInt32(0)
      .setInt64(0L)
      .setUint32(0)
      .setUint64(0)
      .setSint32(0)
      .setSint64(0L)
      .setBool(false)
      .setEnum(PresenceProto.Enumerated.constant_1)
      .setFixed64(0L)
      .setSfixed64(0L)
      .setDouble(0D)
      .setFixed32(0)
      .setSfixed32(0)
      .setFloat(0f)
      ;
    byte[] expected = builder
      .build()
      .toByteArray();
    assertEquals(expected.length, bytes.length);
  }

  @Test
  public void testRepeatedPresence() {
    Repeated msg = new Repeated();
    msg.setString(Collections.emptyList());
    msg.setBytes(Collections.emptyList());
    msg.setInt32(Collections.emptyList());
    msg.setInt64(Collections.emptyList());
    msg.setUint32(Collections.emptyList());
    msg.setUint64(Collections.emptyList());
    msg.setSint32(Collections.emptyList());
    msg.setSint64(Collections.emptyList());
    msg.setBool(Collections.emptyList());
    msg.setEnum(Collections.emptyList());
    msg.setFixed64(Collections.emptyList());
    msg.setSfixed64(Collections.emptyList());
    msg.setDouble(Collections.emptyList());
    msg.setFixed32(Collections.emptyList());
    msg.setSfixed32(Collections.emptyList());
    msg.setFloat(Collections.emptyList());
    byte[] bytes = ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(msg, visitor));
    PresenceProto.Repeated.Builder builder = PresenceProto.Repeated.newBuilder();
    byte[] expected = builder
      .build()
      .toByteArray();
    assertEquals(expected.length, bytes.length);
  }
}
