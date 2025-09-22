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
package io.vertx.protobuf.plugin.schema;

public class FieldDeclaration {

  public final String identifier;
  public final String name;
  public final String jsonName;
  public final boolean map;
  public final boolean mapKey;
  public final boolean mapValue;
  public final boolean repeated;
  public final boolean packed;
  public final String messageTypeIdentifier;
  public final String messageName;
  public final int number;
  public final String typeExpr;

  public FieldDeclaration(String identifier, String name, boolean map, boolean mapKey, boolean mapValue, boolean repeated, boolean packed, String jsonName, String messageTypeIdentifier, int number, String messageName, String typeExpr) {
    this.identifier = identifier;
    this.name = name;
    this.jsonName = jsonName;
    this.messageTypeIdentifier = messageTypeIdentifier;
    this.messageName = messageName;
    this.map = map;
    this.mapKey = mapKey;
    this.mapValue = mapValue;
    this.repeated = repeated;
    this.packed = packed;
    this.number = number;
    this.typeExpr = typeExpr;
  }
}
