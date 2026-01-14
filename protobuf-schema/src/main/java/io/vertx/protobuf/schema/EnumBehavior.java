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

/**
 * Defines how enum values that aren't contained within the defined set are handled.
 * 
 * <p>This corresponds to the protobuf editions {@code features.enum_type} feature.</p>
 * 
 * @see <a href="https://protobuf.dev/editions/features/#enum_type">Protobuf Editions - features.enum_type</a>
 */
public enum EnumBehavior {
  
  /**
   * Open enum behavior - out of range values are parsed into the field directly.
   * 
   * <p>This is the default behavior in Edition 2023, Edition 2024, and proto3.</p>
   */
  OPEN,
  
  /**
   * Closed enum behavior - out of range values are stored in the unknown field set.
   * 
   * <p>This is the default behavior in proto2.</p>
   */
  CLOSED;
  
  /**
   * Returns the default enum behavior for the given syntax.
   * 
   * @param syntax the protobuf syntax version
   * @return the default enum behavior
   */
  public static EnumBehavior defaultFor(Syntax syntax) {
    switch (syntax) {
      case PROTO2:
        return CLOSED;
      case PROTO3:
      case EDITION_2023:
      case EDITION_2024:
      default:
        return OPEN;
    }
  }
}
