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
package io.vertx.protobuf.core.json;

import io.vertx.protobuf.core.DecodeException;
import io.vertx.protobuf.core.ProtoVisitor;
import io.vertx.protobuf.well_known_types.FieldLiteral;

import java.io.IOException;

class StructParser {

  static void parseObject(JsonDecoder parser, ProtoVisitor visitor) throws IOException {
    assert parser.hasToken(JsonTokenKind.START_OBJECT);
    visitor.enterPacked(FieldLiteral.Struct_fields);
    while (parser.nextToken() == JsonTokenKind.FIELD_NAME) {
      String key = parser.fieldName();
      visitor.enter(FieldLiteral.Struct_fields);
      visitor.visitString(FieldLiteral.Struct_FieldsEntry_key, key);
      parser.nextToken();
      visitor.enter(FieldLiteral.Struct_FieldsEntry_value);
      parseValue(parser, visitor);
      visitor.leave(FieldLiteral.Struct_FieldsEntry_value);
      visitor.leave(FieldLiteral.Struct_fields);
    }
    visitor.leavePacked(FieldLiteral.Struct_fields);
  }

  public static void parseValue(JsonDecoder parser, ProtoVisitor visitor) throws IOException, DecodeException {
    switch (parser.currentToken()) {
      case START_OBJECT:
        visitor.enter(FieldLiteral.Value_struct_value);
        parseObject(parser, visitor);
        visitor.leave(FieldLiteral.Value_struct_value);
        break;
      case START_ARRAY:
        visitor.enter(FieldLiteral.Value_list_value);
        parseArray(parser, visitor);
        visitor.leave(FieldLiteral.Value_list_value);
        break;
      case STRING:
        String text = parser.text();
        visitor.visitString(FieldLiteral.Value_string_value, text);
        break;
      case NUMBER_FLOAT:
      case NUMBER_INT:
        double number = parser.doubleValue();
        visitor.visitDouble(FieldLiteral.Value_number_value, number);
        break;
      case TRUE:
        visitor.visitBool(FieldLiteral.Value_bool_value, true);
        break;
      case FALSE:
        visitor.visitBool(FieldLiteral.Value_bool_value, false);
        break;
      case NULL:
        visitor.visitEnum(FieldLiteral.Value_null_value, 0);
        break;
      default:
        throw new DecodeException("Unexpected token"/*, parser.getCurrentLocation()*/);
    }
  }

  public static void parseArray(JsonDecoder parser, ProtoVisitor visitor) throws IOException {
    assert parser.hasToken(JsonTokenKind.START_ARRAY);
    visitor.enterPacked(FieldLiteral.ListValue_values);
    while (parser.nextToken() != JsonTokenKind.END_ARRAY) {
      visitor.enter(FieldLiteral.ListValue_values);
      parseValue(parser, visitor);
      visitor.leave(FieldLiteral.ListValue_values);
    }
    visitor.leavePacked(FieldLiteral.ListValue_values);
  }
}
