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

import io.vertx.protobuf.core.ProtoVisitor;
import io.vertx.protobuf.schema.Field;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.well_known_types.FieldLiteral;
import io.vertx.protobuf.well_known_types.MessageLiteral;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.Deque;

public class ProtoReader implements ProtoVisitor {

  public static OffsetDateTime toOffsetDateTime(long seconds, int nanos) {
    return OffsetDateTime.ofInstant(Instant.ofEpochSecond(seconds, nanos), ZoneId.of("UTC"));
  }

  private final Deque<Object> stack;
  public MessageLiteral rootType;

  private long durationSeconds;
  private int durationNanos;
  private long timestampSeconds;
  private int timestampNanos;
  private double doubleValue;
  private float floatValue;
  private boolean booleanValue;
  private long intValue;
  private long longValue;
  private String stringValue;
  private byte[] bytesValue;

  public ProtoReader() {
    this(new ArrayDeque<>());
  }

  public ProtoReader(Deque<Object> stack) {
    this.stack = stack;
  }

  public int depth() {
    return stack.size();
  }

  public Object pop() {
    Object obj = stack.pop();
    if (obj == NULL) {
      obj = null;
    }
    return obj;
  }

  @Override
  public void init(MessageType type) {
    if (type instanceof MessageLiteral) {
      MessageLiteral literal = (MessageLiteral) type;
      switch (literal) {
        case Struct:
//          stack.push(new JsonObject());
          throw new UnsupportedOperationException();
        case ListValue:
//          stack.push(new JsonArray());
          throw new UnsupportedOperationException();
        case Value:
          break;
        case Duration:
          durationSeconds = 0;
          durationNanos = 0;
          break;
        case Timestamp:
          timestampSeconds = 0;
          timestampNanos = 0;
          break;
        case DoubleValue:
          doubleValue = 0D;
          break;
        case FloatValue:
          floatValue = 0F;
          break;
        case Int64Value:
        case UInt64Value:
          longValue = 0L;
          break;
        case Int32Value:
        case UInt32Value:
          intValue = 0;
          break;
        case BoolValue:
          booleanValue = false;
          break;
        case StringValue:
          stringValue = "";
          break;
        case BytesValue:
          bytesValue = new byte[0];
          break;
        default:
          throw new UnsupportedOperationException();
      }
      rootType = literal;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitBool(Field field, boolean v) {
    if (field instanceof FieldLiteral) {
      switch ((FieldLiteral) field) {
        case BoolValue_value:
          booleanValue = v;
          break;
        case Value_bool_value:
          switch (rootType) {
            case Value:
            case ListValue:
            case Struct:
              stack.push(v);
              break;
          }
          break;
        default:
          throw new UnsupportedOperationException();
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private static final Object NULL = new Object();

  @Override
  public void visitEnum(Field field, int number) {
    if (field instanceof FieldLiteral) {
      switch ((FieldLiteral) field) {
        case Value_null_value:
          switch (rootType) {
            case Struct:
            case ListValue:
            case Value:
              stack.push(NULL);
              break;
            default:
              throw new UnsupportedOperationException();
          }
          break;
        default:
          throw new UnsupportedOperationException();
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }

/*
  private void append(Object value) {
    Object container = stack.peek();
    if (container instanceof Entry) {
      ((Entry)container).value = value;
    } else {
      ((JsonArray)container).add(value);
    }
  }
*/

  @Override
  public void visitInt64(Field field, long v) {
    switch ((FieldLiteral)field) {
      case Duration_seconds:
        durationSeconds = v;
        break;
      case Timestamp_seconds:
        timestampSeconds = v;
        break;
      case Int64Value_value:
        longValue = v;
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitFixed32(Field field, int v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitSFixed32(Field field, int v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitFixed64(Field field, long v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitSFixed64(Field field, long v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitSInt32(Field field, int v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitSInt64(Field field, long v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitUInt64(Field field, long v) {
    switch ((FieldLiteral)field) {
      case UInt64Value_value:
        longValue = v;
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitInt32(Field field, int v) {
    switch ((FieldLiteral)field) {
      case Duration_nanos:
        durationNanos = v;
        break;
      case Timestamp_nanos:
        timestampNanos = v;
        break;
      case Int32Value_value:
        intValue = v;
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitUInt32(Field field, int v) {
    switch ((FieldLiteral)field) {
      case UInt32Value_value:
        intValue = v;
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitString(Field field, String s) {
    if (field instanceof FieldLiteral) {
      switch ((FieldLiteral)field) {
        case Struct_FieldsEntry_key:
          // FieldsEntry
          ((Entry)stack.peek()).key = s;
          break;
        case Value_string_value:
          switch (rootType) {
            case Struct:
            case ListValue:
            case Value:
              stack.push(s);
              break;
            default:
              throw new UnsupportedOperationException();
          }
          break;
        case StringValue_value:
          stringValue = s;
          break;
        default:
          throw new UnsupportedOperationException();
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitFloat(Field field, float f) {
    if (field instanceof FieldLiteral) {
      switch ((FieldLiteral)field) {
        case FloatValue_value:
          floatValue = f;
          break;
        default:
          throw new UnsupportedOperationException();
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void visitDouble(Field field, double d) {
    if (field instanceof FieldLiteral) {
      switch ((FieldLiteral)field) {
        case DoubleValue_value:
          doubleValue = d;
          break;
        case Value_number_value:
          switch (rootType) {
            case Struct:
            case ListValue:
            case Value:
              stack.push(d);
              break;
            default:
              throw new UnsupportedOperationException();
          }
          break;
        default:
          throw new UnsupportedOperationException();
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void enterPacked(Field field) {
  }

  @Override
  public void enter(Field field) {
    FieldLiteral fl = (FieldLiteral) field;
    switch (fl) {
      case Value_struct_value:
//        stack.push(new JsonObject());
      case Value_list_value:
//        stack.push(new JsonArray());
        throw new UnsupportedOperationException();
      case Struct_fields:
        stack.push(new Entry());
        break;
      case Struct_FieldsEntry_value:
      case ListValue_values:
        break;
      default:
        throw new UnsupportedOperationException(fl.name());
    }
  }

  private static class Entry {
    String key;
    Object value;
  }

  @Override
  public void leavePacked(Field field) {

  }

  @Override
  public void leave(Field field) {
    FieldLiteral fl = (FieldLiteral) field;
    switch (fl) {
      case Value_struct_value:
        break;
      case Value_list_value:
        break;
      case Struct_fields:
//        Entry entry = (Entry) stack.pop();
//        ((JsonObject)stack.peek()).put(entry.key, entry.value);
        throw new UnsupportedOperationException();
      case Struct_FieldsEntry_value:
        Object value = pop();
        ((Entry)stack.peek()).value = value;
        break;
      case ListValue_values:
//        value = pop();
//        ((JsonArray)stack.peek()).add(value);
        throw new UnsupportedOperationException();
      default:
        throw new UnsupportedOperationException(fl.name());
    }
  }

  @Override
  public void destroy() {
    switch (rootType) {
      case Duration:
        stack.push(Duration.ofSeconds(durationSeconds, durationNanos));
        break;
      case Timestamp:
        stack.push(toOffsetDateTime(timestampSeconds, timestampNanos));
        break;
      case DoubleValue:
        stack.push(doubleValue);
        break;
      case FloatValue:
        stack.push(floatValue);
        break;
      case Int64Value:
      case UInt64Value:
        stack.push(longValue);
        break;
      case Int32Value:
      case UInt32Value:
        stack.push(intValue);
        break;
      case BoolValue:
        stack.push(booleanValue);
        break;
      case StringValue:
        stack.push(stringValue);
        stringValue = null;
        break;
      case BytesValue:
        stack.push(bytesValue);
        bytesValue = null;
        break;
    }
    rootType = null;
  }

  @Override
  public void visitBytes(Field field, byte[] bytes) {
    if (field instanceof FieldLiteral) {
      switch ((FieldLiteral)field) {
        case BytesValue_value:
          bytesValue = bytes;
          break;
        default:
          throw new UnsupportedOperationException();
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }
}
