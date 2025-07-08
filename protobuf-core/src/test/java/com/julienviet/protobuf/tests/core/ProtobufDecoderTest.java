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

import com.julienviet.protobuf.core.DefaultProtobufDecoder;
import com.julienviet.protobuf.core.ProtobufDecoder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProtobufDecoderTest {

  @Test
  public void testReadOversizedVarInt() {
    byte[] data = { -1, -1, -1, -1, -1, -1, -1, -1, 127 };
    long expected = 0b1111111_1111111_1111111_1111111_1111111_1111111_1111111_1111111_1111111L;
    ProtobufDecoder decoder = new DefaultProtobufDecoder(data);
    assertTrue(decoder.readVarInt32());
    assertEquals((int)expected, decoder.intValue());
 }

 @Test
  public void testSome() {
   byte[] data = {
     -71, 96,
     -71, -32, -128, 0,
     -71, -32,
     -128, -128, -128, -128, -128, -128, 0, -1, -1, -1, -1, 7, -128, -128, -128, -128, -8, -1, -1, -1, -1, 1, -128, -128, -128, -128, 32, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, 127, -127, -128, -128, -128, -128, -128, -128, -128, -128, 1 };
   ProtobufDecoder decoder = new DefaultProtobufDecoder(data);
   int[] expected = { 12345, 12345, 12345, 2147483647, -2147483648, 0, -1, -1, 1 };
   for (int i = 0;i < expected.length;i++) {
     assertTrue(decoder.readVarInt32());
     assertEquals("Not same at " + i, expected[i], decoder.intValue());
   }


   // 10111001
   // 01100000

   // 0111001_1100000

 }

  @Test
  public void testReadOversizeUInt64() {
    // 18446744073709551615
    byte[] data = { -1, -1, -1, -1, -1, -1, -1, -1, -1, 1 };
    ProtobufDecoder decoder = new DefaultProtobufDecoder(data);
    assertTrue(decoder.readVarInt64());
//    assertEquals(18446744073709551615L, decoder.longValue());

  }
}
