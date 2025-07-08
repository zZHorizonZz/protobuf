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
package com.julienviet.protobuf.core.json;

public class Json {

/*
  public static byte[] encodeToBuffer(JsonObject json) {
    Consumer<ProtoVisitor> consumer = visitor -> {
      ProtoWriter.emit(json, visitor);
    };
    return ProtobufWriter.encodeToByteArray(consumer);
  }

  public static byte[] encodeToByteArray(JsonObject json) {
    Consumer<ProtoVisitor> consumer = visitor -> {
      ProtoWriter.emit(json, visitor);
    };
    return ProtobufWriter.encodeToByteArray(consumer);
  }

  public static JsonObject parseStruct(byte[] buffer) {
    ProtoReader builder = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Struct, builder, buffer);
    return (JsonObject) builder.pop();
  }
*/
}
