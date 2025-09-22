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

class UnknownField implements Field, Type {
  final MessageType owner;
  final int number;
  final WireType wireType;

  UnknownField(MessageType owner, int number,WireType wireType) {
    this.owner = owner;
    this.number = number;
    this.wireType = wireType;
  }

  @Override
  public MessageType owner() {
    return owner;
  }

  @Override
  public int number() {
    return number;
  }

  @Override
  public boolean isPacked() {
    return false;
  }

  @Override
  public Type type() {
    return this;
  }

  @Override
  public TypeID id() {
    switch (wireType) {
      case LEN:
        return TypeID.BYTES;
      case I32:
        return TypeID.FIXED32;
      case I64:
        return TypeID.FIXED64;
      case VARINT:
        return TypeID.INT64;
      default:
        throw new IllegalStateException();
    }
  }

  @Override
  public String protoName() {
    return null;
  }

  @Override
  public String jsonName() {
    return null;
  }

  @Override
  public WireType wireType() {
    return wireType;
  }

  @Override
  public boolean isUnknown() {
    return true;
  }

  @Override
  public boolean isMap() {
    return false;
  }

  @Override
  public boolean isMapKey() {
    return false;
  }

  @Override
  public boolean isMapValue() {
    return false;
  }

  @Override
  public boolean isRepeated() {
    return false;
  }

  @Override
  public int hashCode() {
    return number;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Field) {
      Field that = (Field) obj;
      return number == that.number() && type().id() == that.type().id() && wireType == that.type().wireType();
    }
    return false;
  }
}
