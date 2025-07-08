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

import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.tests.core.support.merge.MergeProto;
import com.julienviet.protobuf.tests.core.support.merge.Container;
import com.julienviet.protobuf.tests.core.support.merge.Nested;
import com.julienviet.protobuf.tests.core.support.merge.ProtoReader;
import com.julienviet.protobuf.tests.core.support.merge.MessageLiteral;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MergeTest {

  @Test
  public void testMerge() throws Exception {
    byte[] bytes1 = MergeProto.Container.newBuilder()
      .setInt32(1)
      .addRepeatedInt32(1)
      .setNested(MergeProto.Nested.newBuilder().setInt32(3).addRepeatedInt32(3).build())
      .setOneOfNested(MergeProto.Nested.newBuilder().setInt32(3).addRepeatedInt32(3).build())
      .build().toByteArray();
    byte[] bytes2 = MergeProto.Container.newBuilder()
      .setInt64(1L)
      .addRepeatedInt32(2)
      .setNested(MergeProto.Nested.newBuilder().setInt64(4L).addRepeatedInt32(4).build())
      .setOneOfNested(MergeProto.Nested.newBuilder().setInt64(4L).addRepeatedInt32(4).build())
      .build().toByteArray();
    byte[] aggregated = new byte[bytes1.length + bytes2.length];
    System.arraycopy(bytes1, 0, aggregated, 0, bytes1.length);
    System.arraycopy(bytes2, 0, aggregated, bytes1.length, bytes2.length);
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Container, reader, aggregated);
    Container msg = (Container) reader.stack.pop();
    assertEquals(1, (int)msg.getInt32());
    assertEquals(1L, (long)msg.getInt64());
    assertEquals(Arrays.asList(1, 2), msg.getRepeatedInt32());
    Nested nested = msg.getNested();
    assertEquals(3, (int)nested.getInt32());
    assertEquals(4L, (long)nested.getInt64());
    assertEquals(Arrays.asList(3, 4), nested.getRepeatedInt32());
    Nested oneOfNested = msg.getOneOf().asOneOfNested().get();
    assertEquals(3, (int)oneOfNested.getInt32());
    assertEquals(4L, (long)oneOfNested.getInt64());
    assertEquals(Arrays.asList(3, 4), oneOfNested.getRepeatedInt32());
  }

  @Test
  public void testOneOfOverride() {
    byte[] bytes1 = MergeProto.Container.newBuilder()
      .setOneOfNested(MergeProto.Nested.newBuilder().setInt32(3).build())
      .build().toByteArray();
    byte[] bytes2 = MergeProto.Container.newBuilder().setOneOfInt32(4).build().toByteArray();
    byte[] aggregated = new byte[bytes1.length + bytes2.length];
    System.arraycopy(bytes1, 0, aggregated, 0, bytes1.length);
    System.arraycopy(bytes2, 0, aggregated, bytes1.length, bytes2.length);
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Container, reader, aggregated);
    Container msg = (Container) reader.stack.pop();
    Integer i = msg.getOneOf().asOneOfInt32().get();
    assertEquals(4, (int)i);
  }
}
