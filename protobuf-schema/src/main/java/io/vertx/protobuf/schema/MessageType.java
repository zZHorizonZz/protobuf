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
package io.vertx.protobuf.schema;

public interface MessageType extends Type {
  @Override
  default TypeID id() {
    return TypeID.MESSAGE;
  }
  @Override
  default WireType wireType() {
    return WireType.LEN;
  }
  String name(); // rename to proto name ?
  Field field(int number);
  default Field fieldByName(String name) {
    return null;
  }
  default Field fieldByJsonName(String jsonName) {
    return null;
  }
  default Field unknownField(int number, WireType wireType) {
    return new UnknownField(this, number, wireType);
  }
 }
