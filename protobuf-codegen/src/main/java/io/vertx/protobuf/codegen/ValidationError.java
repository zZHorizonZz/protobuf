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
package io.vertx.protobuf.codegen;

public enum ValidationError {

  MESSAGE_INVALID_JAVA_CLASS,
  FIELD_MISSING_JAVA_GETTER_METHOD,
  FIELD_MISSING_JAVA_SETTER_METHOD,
  FIELD_INVALID_JAVA_METHOD,
  FIELD_INVALID_JAVA_TYPE,
  FIELD_INVALID_PROTO_NAME,
  FIELD_TYPE_MISMATCH,
  FIELD_DUPLICATE_NUMBER,
  FIELD_INVALID_NUMBER
  ;

}
