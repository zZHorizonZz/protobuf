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

public interface ProtobufDecoder {

  int len();

  ProtobufDecoder len(int len);

  ProtobufDecoder index(int index);

  int index();

  String readString(int lengthInBytes);

  byte[] readBytes(int lengthInBytes);

  boolean readTag();

  int fieldNumber();

  int wireType();

  int intValue();

  long longValue();

  boolean readVarInt32();

  boolean readVarInt64();

  boolean readI32();

  boolean readI64();

  boolean isReadable();

}
