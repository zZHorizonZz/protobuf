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

public class DefaultFieldBuilder {

  int number;
  String name;
  String jsonName;
  Type type;
  Boolean packed;
  boolean repeated;
  boolean optional;
  boolean map;
  boolean mapKey;
  boolean mapValue;

  public DefaultFieldBuilder optional(boolean optional) {
    this.optional = optional;
    return this;
  }

  public DefaultFieldBuilder number(int number) {
    this.number = number;
    return this;
  }

  public DefaultFieldBuilder name(String name) {
    this.name = name;
    return this;
  }

  public DefaultFieldBuilder jsonName(String jsonName) {
    this.jsonName = jsonName;
    return this;
  }

  public DefaultFieldBuilder type(Type type) {
    this.type = type;
    return this;
  }

  public DefaultFieldBuilder packed(boolean packed) {
    this.packed = packed;
    return this;
  }

  public DefaultFieldBuilder repeated(boolean repeated) {
    this.repeated = repeated;
    return this;
  }

  public DefaultFieldBuilder map(boolean map) {
    this.map = map;
    return this;
  }

  public DefaultFieldBuilder mapKey(boolean mapKey) {
    this.mapKey = mapKey;
    return this;
  }

  public DefaultFieldBuilder mapValue(boolean mapValue) {
    this.mapValue = mapValue;
    return this;
  }
}
