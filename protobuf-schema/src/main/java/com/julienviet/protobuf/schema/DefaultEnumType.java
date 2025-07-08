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
package com.julienviet.protobuf.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class DefaultEnumType implements EnumType {

  private final String name;
  private final Map<String, Integer> numberByName = new HashMap<>();
  private final Map<Integer, String> nameByNumber = new HashMap<>();

  public DefaultEnumType(String name) {
    this.name = name;
  }

  public DefaultEnumType() {
    this.name = null;
  }

  @Override
  public TypeID id() {
    return TypeID.ENUM;
  }

  public DefaultEnumType addValue(int number, String name) {
    numberByName.put(name, number);
    nameByNumber.put(number, name);
    return this;
  }

  @Override
  public WireType wireType() {
    return WireType.VARINT;
  }

  @Override
  public OptionalInt numberOf(String name) {
    Integer number = numberByName.get(name);
    return number != null ? OptionalInt.of(number) : OptionalInt.empty();
  }

  @Override
  public String nameOf(int number) {
    return nameByNumber.get(number);
  }
}
