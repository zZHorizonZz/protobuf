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
package io.vertx.protobuf.core.interop;

import io.vertx.protobuf.core.ProtoStream;
import io.vertx.protobuf.core.ProtoVisitor;
import io.vertx.protobuf.well_known_types.FieldLiteral;
import io.vertx.protobuf.well_known_types.MessageLiteral;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

public class ProtoWriter {

  public static void emit(Duration duration, ProtoVisitor visitor) {
    visitor.init(MessageLiteral.Duration);
    visit(duration, visitor);
    visitor.destroy();
  }

  public static ProtoStream protoStream(Duration duration) {
    return visitor -> {
      emit(duration, visitor);
    };
  }

  public static void visit(Duration duration, ProtoVisitor visitor) {
    long seconds = duration.getSeconds();
    if (seconds != 0L) {
      visitor.visitInt64(FieldLiteral.Duration_seconds, seconds);
    }
    int nano = duration.getNano();
    if (nano != 0) {
      visitor.visitInt32(FieldLiteral.Duration_nanos, nano);
    }
  }

  public static void emit(OffsetDateTime timestamp, ProtoVisitor visitor) {
    visitor.init(MessageLiteral.Timestamp);
    visit(timestamp, visitor);
    visitor.destroy();
  }

  public static void visit(OffsetDateTime timestamp, ProtoVisitor visitor) {
    Instant instant = timestamp.toInstant();
    long seconds = instant.getEpochSecond();
    if (seconds != 0L) {
      visitor.visitInt64(FieldLiteral.Timestamp_seconds, seconds);
    }
    int nano = timestamp.getNano();
    if (nano != 0) {
      visitor.visitInt32(FieldLiteral.Timestamp_nanos, nano);
    }
  }

/*
  public static void emit(JsonObject json, ProtoVisitor visitor) {
    visitor.init(MessageLiteral.Struct);
    visit(json, visitor);
    visitor.destroy();
  }

  public static void visit(JsonObject json, ProtoVisitor visitor) {
    Map<String, Object> map = json.getMap();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      visitor.enter(FieldLiteral.Struct_fields); // fields
      visitor.visitString(FieldLiteral.Struct_FieldsEntry_key, entry.getKey());
      visitor.enter(FieldLiteral.Struct_FieldsEntry_value);
      visitValueInternal(entry.getValue(), visitor);
      visitor.leave(FieldLiteral.Struct_FieldsEntry_value);
      visitor.leave(FieldLiteral.Struct_fields);
    }
  }

  public static void emit(JsonArray json, ProtoVisitor visitor) {
    visitor.init(MessageLiteral.ListValue);
    visit(json, visitor);
    visitor.destroy();
  }

  public static void visit(JsonArray json, ProtoVisitor visitor) {
    for (Object value : json.getList()) {
      visitor.enter(FieldLiteral.ListValue_values); // values
      visitValueInternal(value, visitor);
      visitor.leave(FieldLiteral.ListValue_values);
    }
  }
*/

/*
  private static void visitValueInternal(Object value, ProtoVisitor visitor) {
    if (value == null) {
      visitor.visitEnum(FieldLiteral.Value_null_value, 0);
    } else if (value instanceof String) {
      visitor.visitString(FieldLiteral.Value_string_value, (String) value);
    } else if (value instanceof Boolean) {
      visitor.visitBool(FieldLiteral.Value_bool_value, (Boolean) value);
    } else if (value instanceof Number) {
      visitor.visitDouble(FieldLiteral.Value_number_value, ((Number) value).doubleValue());
    } else if (value instanceof JsonObject) {
      visitor.enter(FieldLiteral.Value_struct_value);
      visit((JsonObject) value, visitor);
      visitor.leave(FieldLiteral.Value_struct_value);
    } else if (value instanceof JsonArray) {
      visitor.enter(FieldLiteral.Value_list_value);
      visit((JsonArray) value, visitor);
      visitor.leave(FieldLiteral.Value_list_value);
    } else {
      throw new UnsupportedOperationException("" + value);
    }
  }
*/
}
