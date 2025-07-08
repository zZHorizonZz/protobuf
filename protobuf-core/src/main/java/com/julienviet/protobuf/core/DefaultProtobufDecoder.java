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
package com.julienviet.protobuf.core;

import java.nio.charset.StandardCharsets;

public class DefaultProtobufDecoder implements ProtobufDecoder {

  private final byte[] buffer;
  private int idx;
  private int len;
  private int fieldNumber;
  private int wireType;
  private int intValue;
  private long longValue;

  public DefaultProtobufDecoder(byte[] buffer) {
    this.buffer = buffer;
    this.idx = 0;
    this.len = buffer.length;
  }

  public int len() {
    return len;
  }

  public DefaultProtobufDecoder len(int len) {
    this.len = len;
    return this;
  }

  public DefaultProtobufDecoder index(int index) {
    idx = index;
    return this;
  }

  public int index() {
    return idx;
  }

  public void skip(int n) {
    idx += n;
  }

  public String readString(int lengthInBytes) {
    String str = new String(buffer, idx, lengthInBytes, StandardCharsets.UTF_8);
    idx += lengthInBytes;
    return str;
  }

  public byte[] readBytes(int lengthInBytes) {
    byte[] str = new byte[lengthInBytes];
    System.arraycopy(buffer, idx, str, 0, lengthInBytes);
    idx += lengthInBytes;
    return str;
  }

  public boolean readTag() {
    int c = idx;
    int e = decodeVarInt32();
    // Can be branch-less
    if (idx > c) {
      fieldNumber = e >> 3;
      wireType = e & 0b0111;
      return true;
    } else {
      return false;
    }
  }

  public int fieldNumber() {
    return fieldNumber;
  }

  public int wireType() {
    return wireType;
  }

  public int intValue() {
    return intValue;
  }

  public long longValue() {
    return longValue;
  }

  public boolean readVarInt32() {
    int c = idx;
    intValue = decodeVarInt32();
    return idx > c;
  }

  public boolean readVarInt64() {
    int c = idx;
    longValue = decodeVarInt64();
    return idx > c;
  }

  public boolean readI32() {
    intValue = ((int)buffer[idx++] & 0xFF) + (((int)buffer[idx++] & 0xFF) << 8) + (((int)buffer[idx++] & 0xFF) << 16) + (((int)buffer[idx++] & 0xFF) << 24);
    return true;
  }

  public boolean readI64() {
    longValue = ((long)buffer[idx++] & 0xFF) + ((long)(buffer[idx++]  & 0xFF) << 8) + ((long)(buffer[idx++]  & 0xFF) << 16) + ((long)(buffer[idx++]  & 0xFF) << 24)
      + ((long)(buffer[idx++]  & 0xFF) << 32) + ((long)(buffer[idx++]  & 0xFF) << 40) + ((long)(buffer[idx++]  & 0xFF) << 48) + ((long)(buffer[idx++]  & 0xFF) << 56);
    return true;
  }

  private int readableBytes() {
    return len - idx;
  }

  public boolean isReadable() {
    return idx < len;
  }

  public int decodeVarInt32() {
    return (int) decodeRawVarInt();
  }

  public long decodeVarInt64() {
    return decodeRawVarInt();
  }

  private long decodeRawVarInt() {
    int i = idx;
    int l = idx + len;
    while (i < l) {
      byte b = buffer[i];
      i++;
      if ((b & 0x80) == 0) {
        long val = 0;
        int to = idx;
        idx = i;
        int from = idx - 1;
        while (from >= to) {
          val <<= 7;
          val += (buffer[from--] & 0x7F);
        }
        return val;
      }
    }
    throw new DecodeException();
  }
}
