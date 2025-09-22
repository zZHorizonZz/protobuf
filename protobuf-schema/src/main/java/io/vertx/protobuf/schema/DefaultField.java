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

public class DefaultField implements Field {

  static String toJsonName(String fieldName) {
    StringBuilder sb = new StringBuilder(fieldName.length());
    char prev = ' ';
    for (int i = 0;i < fieldName.length();i++) {
      char ch = fieldName.charAt(i);
      boolean upperCase = ch >= 'a' && ch <= 'z' && prev == '_';
      prev = ch;
      if (ch != '_') {
        ch = (char)(upperCase ? ch + ('A' - 'a') : ch);
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  private final DefaultMessageType owner;
  private final int number;
  private final String name;
  private final String jsonName;
  private final boolean map;
  private final boolean mapKey;
  private final boolean mapValue;
  private final boolean repeated;
  private final boolean packed;
  private final Type type;

  DefaultField(DefaultMessageType owner, int number, String name, String jsonName, boolean map, boolean mapKey, boolean mapValue, boolean repeated, boolean packed, Type type) {
    this.owner = owner;
    this.number = number;
    this.jsonName = jsonName;
    this.name = name;
    this.repeated = repeated;
    this.mapKey = mapKey;
    this.map = map;
    this.mapValue = mapValue;
    this.packed = packed;
    this.type = type;
  }

  public MessageType owner() {
    return owner;
  }

  @Override
  public String protoName() {
    return name;
  }

  @Override
  public String jsonName() {
    return jsonName;
  }

  @Override
  public boolean isPacked() {
    return packed;
  }

  @Override
  public boolean isMap() {
    return map;
  }

  @Override
  public boolean isMapKey() {
    return mapKey;
  }

  @Override
  public boolean isMapValue() {
    return mapValue;
  }

  @Override
  public boolean isRepeated() {
    return repeated;
  }

  public int number() {
    return number;
  }

  public Type type() {
    return type;
  }

  public String toString() {
    return "Field[number=" + number + ",type=" + type + ",owner=" + owner.name() + "]";
  }
}
