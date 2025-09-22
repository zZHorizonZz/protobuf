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
package io.vertx.protobuf.core;

import java.nio.charset.StandardCharsets;

public class DefaultProtobufEncoder implements ProtobufEncoder {

  private final byte[] buffer;
  private int index;

  public DefaultProtobufEncoder(int size) {
    this.buffer = new byte[size];
    this.index = 0;
  }

  public byte[] buffer() {
    return buffer;
  }

  public int index() {
    return index();
  }

  public void writeTag(int fieldNumber, int wireType) {
    int tag = (fieldNumber << 3) | (wireType & 0x07);
    index = encodeVarInt32(buffer, index, tag);
  }

  public void writeVarInt32(int v) {
    index = encodeVarInt32(buffer, index, v);
  }

  public void writeVarInt64(long v) {
    index = encodeVarInt64(buffer, index, v);
  }

  public void writeInt(int d) {
    buffer[index++] = (byte)(d & 0xFF);
    buffer[index++] = (byte)((d & 0xFF00) >> 8);
    buffer[index++] = (byte)((d & 0xFF0000) >> 16);
    buffer[index++] = (byte)((d & 0xFF000000) >> 24);
  }

  public void writeLong(long d) {
    buffer[index++] = (byte)(d & 0xFFL);
    buffer[index++] = (byte)((d & 0xFF00L) >> 8);
    buffer[index++] = (byte)((d & 0xFF0000L) >> 16);
    buffer[index++] = (byte)((d & 0xFF000000L) >> 24);
    buffer[index++] = (byte)((d & 0xFF00000000L) >> 32);
    buffer[index++] = (byte)((d & 0xFF0000000000L) >> 40);
    buffer[index++] = (byte)((d & 0xFF000000000000L) >> 48);
    buffer[index++] = (byte)((d & 0xFF00000000000000L) >> 56);
  }

  public void writeString(String s) {
    writeBinary(s.getBytes(StandardCharsets.UTF_8));
  }

  public void writeBinary(byte[] bytes) {
    System.arraycopy(bytes, 0, buffer, index, bytes.length);
    index += bytes.length;
  }

  /**
   * Encode a 4 bytes value to {@code VARINT} format
   * @param out to be written to
   * @param value to be written
   */
  private static int encodeVarInt32(byte[] out, int index, int value) {
    while (true) {
      if ((value & ~0x7F) == 0) {
        out[index++] = (byte)value;
        return index;
      } else {
        out[index++] = (byte)((value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }

  /**
   * Encode a 8 bytes value to {@code VARINT} format
   * @param out to be written to
   * @param value to be written
   */
  static int encodeVarInt64(byte[] out, int index, long value) {
    while (true) {
      if ((value & ~0x7F) == 0) {
        out[index++] = (byte) value;
        return index;
      } else {
        out[index++] = (byte)((value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }

  /**
   * Computes size of protobuf varint32 after encoding.
   * @param value which is to be encoded.
   * @return size of value encoded as protobuf varint32.
   */
  public static int computeRawVarint32Size(final int value) {
    if ((value & (0xffffffff <<  7)) == 0) {
      return 1;
    }
    if ((value & (0xffffffff << 14)) == 0) {
      return 2;
    }
    if ((value & (0xffffffff << 21)) == 0) {
      return 3;
    }
    if ((value & (0xffffffff << 28)) == 0) {
      return 4;
    }
    return 5;
  }

  public static int computeRawVarint64Size(final long value) {
    if ((value & (0xffffffffffffffffL <<  7)) == 0) {
      return 1;
    }
    if ((value & (0xffffffffffffffffL << 14)) == 0) {
      return 2;
    }
    if ((value & (0xffffffffffffffffL << 21)) == 0) {
      return 3;
    }
    if ((value & (0xffffffffffffffffL << 28)) == 0) {
      return 4;
    }
    if ((value & (0xffffffffffffffffL << 35)) == 0) {
      return 5;
    }
    if ((value & (0xffffffffffffffffL << 42)) == 0) {
      return 6;
    }
    if ((value & (0xffffffffffffffffL << 49)) == 0) {
      return 7;
    }
    if ((value & (0xffffffffffffffffL << 56)) == 0) {
      return 8;
    }
    if ((value & (0xffffffffffffffffL << 63)) == 0) {
      return 9;
    }
    return 10;
  }
}
