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
package com.julienviet.protobuf.core.json;

import com.julienviet.protobuf.core.EncodeException;
import com.julienviet.protobuf.core.ProtoStream;
import com.julienviet.protobuf.core.ProtoVisitor;
import com.julienviet.protobuf.core.interop.ProtoReader;
import com.julienviet.protobuf.lang.internal.Utils;
import com.julienviet.protobuf.schema.EnumType;
import com.julienviet.protobuf.schema.Field;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.schema.Type;
import com.julienviet.protobuf.well_known_types.BoolValue;
import com.julienviet.protobuf.well_known_types.BytesValue;
import com.julienviet.protobuf.well_known_types.DoubleValue;
import com.julienviet.protobuf.well_known_types.Duration;
import com.julienviet.protobuf.well_known_types.FieldMask;
import com.julienviet.protobuf.well_known_types.FloatValue;
import com.julienviet.protobuf.well_known_types.Int32Value;
import com.julienviet.protobuf.well_known_types.Int64Value;
import com.julienviet.protobuf.well_known_types.ListValue;
import com.julienviet.protobuf.well_known_types.MessageLiteral;
import com.julienviet.protobuf.well_known_types.StringValue;
import com.julienviet.protobuf.well_known_types.Struct;
import com.julienviet.protobuf.well_known_types.Timestamp;
import com.julienviet.protobuf.well_known_types.UInt32Value;
import com.julienviet.protobuf.well_known_types.UInt64Value;
import com.julienviet.protobuf.well_known_types.Value;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A streaming writer that relies on the fact that the visitor emits continuous repeated fields
 */
public class ProtoJsonWriter implements ProtoVisitor {

  public static String encode(ProtoStream stream) {
    StringWriter buffer = new StringWriter();
    ProtoJsonWriter writer = new ProtoJsonWriter(buffer);
    writer.write(stream);
    return buffer.toString();
  }

  private final JsonEncoder encoder;

  public void write(ProtoStream stream) {
    stream.accept(this);
  }

  public ProtoJsonWriter(JsonEncoder encoder) {
    this.encoder = encoder;
  }

  public ProtoJsonWriter(Writer writer) {
    this.encoder = JsonEncoder.create(writer);
  }

  @Override
  public void init(MessageType type) {
    encoder.writeStartObject();
  }

  @Override
  public void destroy() {
    try {
      encoder.writeEndObject();
    } finally {
      encoder.close();
    }
  }

  @Override
  public void visitInt32(Field field, int v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Integer.toString(v));
      } else if (field.isMapValue()) {
        writeInt32(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeInt32(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitUInt32(Field field, int v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Integer.toString(v));
      } else if (field.isMapValue()) {
        writeUInt32(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeUInt32(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSInt32(Field field, int v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Integer.toString(v));
      } else if (field.isMapValue()) {
        writeSInt32(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeSInt32(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitEnum(Field field, int number) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Integer.toString(number));
      } else if (field.isMapValue()) {
        writeEnum(field, number);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeEnum(field, number);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  private void writeEnum(Field field, int number) throws IOException {
    // Best effort
    String name = ((EnumType) field.type()).nameOf(number);
    if (name != null) {
      writeString(name);
    } else {
      writeInt(number);
    }
  }

  @Override
  public void visitInt64(Field field, long v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Long.toString(v));
      } else if (field.isMapValue()) {
        writeInt64(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeInt64(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitUInt64(Field field, long v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Long.toString(v));
      } else if (field.isMapValue()) {
        writeUInt64(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeUInt64(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSInt64(Field field, long v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Long.toString(v));
      } else if (field.isMapValue()) {
        writeSInt64(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeSInt64(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitBool(Field field, boolean v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Boolean.toString(v));
      } else if (field.isMapValue()) {
        writeBool(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeBool(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitDouble(Field field, double d) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Double.toString(d));
      } else if (field.isMapValue()) {
        writeDouble(d);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeDouble(d);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitFixed64(Field field, long v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Long.toString(v));
      } else if (field.isMapValue()) {
        writeFixed64(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeFixed64(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSFixed64(Field field, long v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Long.toString(v));
      } else if (field.isMapValue()) {
        writeSFixed64(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeSFixed64(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitFloat(Field field, float f) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Float.toString(f));
      } else if (field.isMapValue()) {
        writeFloat(f);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeFloat(f);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitFixed32(Field field, int v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Integer.toString(v));
      } else if (field.isMapValue()) {
        writeFixed32(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeFixed32(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSFixed32(Field field, int v) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(Integer.toString(v));
      } else if (field.isMapValue()) {
        writeSFixed32(v);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeSFixed32(v);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  private static EnumMap<MessageLiteral, Callback> WELL_KNOWN_TYPES = new EnumMap<>(MessageLiteral.class);

  interface Callback {
    void accept(ProtoJsonWriter writer, Object element) throws IOException;
  }

  static {
    WELL_KNOWN_TYPES.put(MessageLiteral.Duration, (writer, obj) -> {
      if (obj instanceof Duration) {
        writer.writeDuration((Duration) obj);
      } else {
        writer.writeDuration((java.time.Duration) obj);
      }
    });
    WELL_KNOWN_TYPES.put(MessageLiteral.Timestamp, (writer, obj) -> {
      if (obj instanceof Timestamp) {
        writer.writeTimestamp((Timestamp) obj);
      } else {
        writer.writeTimestamp((OffsetDateTime) obj);
      }
    });
    WELL_KNOWN_TYPES.put(MessageLiteral.Struct, (writer, obj) -> writer.writeStruct((Struct) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.ListValue, (writer, obj) -> writer.writeListValue((ListValue) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.Value, (writer, obj) -> writer.writeValue((Value) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.DoubleValue, (writer, obj) -> writer.writeDoubleValue((DoubleValue) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.FloatValue, (writer, obj) -> writer.writeFloatValue((FloatValue) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.StringValue, (writer, obj) -> writer.writeStringValue((StringValue) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.BoolValue, (writer, obj) -> writer.writeBoolValue((BoolValue) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.BytesValue, (writer, obj) -> writer.writeBytesValue((BytesValue) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.Int32Value, (writer, obj) -> writer.writeInt32Value((Int32Value) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.Int64Value, (writer, obj) -> writer.writeInt64Value((Int64Value) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.UInt32Value, (writer, obj) -> writer.writeUInt32Value((UInt32Value) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.UInt64Value, (writer, obj) -> writer.writeUInt64Value((UInt64Value) obj));
    WELL_KNOWN_TYPES.put(MessageLiteral.FieldMask, (writer, obj) -> writer.writeFieldMask((FieldMask) obj));
  }

  private void writeDuration(Duration value) throws IOException {
    if (!ProtoJsonReader.isValidDuration(value.getSeconds(), value.getNanos())) {
      throw new EncodeException();
    }
    BigDecimal bd = new BigDecimal(value.getSeconds()).add(BigDecimal.valueOf(value.getNanos(), 9));
    writeString(bd.toPlainString() + "s");
  }

  private void writeDuration(java.time.Duration value) throws IOException {
    BigDecimal bd = new BigDecimal(value.getSeconds()).add(BigDecimal.valueOf(value.getNano(), 9));
    writeString(bd.toPlainString() + "s");
  }

  private void writeTimestamp(Timestamp value) throws IOException {
    if (!ProtoJsonReader.isValidTimestamp(value.getSeconds(), value.getNanos())) {
      throw new EncodeException();
    }
    writeTimestamp(ProtoReader.toOffsetDateTime(value.getSeconds(), value.getNanos()));
  }

  private void writeTimestamp(OffsetDateTime value) throws IOException {
    writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }

  private void writeStruct(Struct value) throws IOException {
    encoder.writeStartObject();
    Map<String, Value> fields = value.getFields();
    for (Map.Entry<String, Value> field : fields.entrySet()) {
      encoder.writeFieldName(field.getKey());
      writeValue(field.getValue());
    }
    encoder.writeEndObject();
  }

  private void writeListValue(ListValue value) throws IOException {
    encoder.writeStartArray();
    for (Value elt : value.getValues()) {
      writeValue(elt);
    }
    encoder.writeEndArray();
  }

  private void writeValue(Value value) throws IOException {
    switch (value.getKind().discriminant()) {
      case BOOL_VALUE:
        writeBool(value.getKind().asBoolValue().get());
        break;
      case LIST_VALUE:
        writeListValue(value.getKind().asListValue().get());
        break;
      case NULL_VALUE:
        writeNull();
        break;
      case STRING_VALUE:
        writeString(value.getKind().asStringValue().get());
        break;
      case NUMBER_VALUE:
        writeDouble(value.getKind().asNumberValue().get());
        break;
      case STRUCT_VALUE:
        writeStruct(value.getKind().asStructValue().get());
        break;
    }
  }

  private void writeDoubleValue(DoubleValue value) throws IOException {
    writeDouble(value.getValue());
  }

  private void writeFloatValue(FloatValue value) throws IOException {
    writeFloat(value.getValue());
  }

  private void writeStringValue(StringValue value) throws IOException {
    writeString(value.getValue());
  }

  private void writeBoolValue(BoolValue value) throws IOException {
    writeBool(value.getValue());
  }

  private void writeBytesValue(BytesValue value) throws IOException {
    writeBytes(value.getValue());
  }

  private void writeInt32Value(Int32Value value) throws IOException {
    writeInt32(value.getValue());
  }

  private void writeInt64Value(Int64Value value) throws IOException {
    writeInt64(value.getValue());
  }

  private void writeUInt32Value(UInt32Value value) throws IOException {
    writeInt32(value.getValue());
  }

  private void writeUInt64Value(UInt64Value value) throws IOException {
    writeInt64(value.getValue());
  }

  private void writeFieldMask(FieldMask value) throws IOException {
    List<String> paths = value.getPaths();
    String ser;
    if (paths != null) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0;i < paths.size();i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append(Utils.snakeToLowerCamel(paths.get(i)));
      }
      ser = sb.toString();
    } else {
      ser = "";
    }
    writeString(ser);
  }

  @Override
  public <T> void visitEmbedded(Field field, T embedded, BiConsumer<T, ProtoVisitor> continuation) {
    Type type = field.type();
    if (type.getClass() == MessageLiteral.class) {
      try {
        MessageLiteral messageLiteral = (MessageLiteral) type;
        switch (messageLiteral) {
          case Duration:
            encoder.writeFieldName(field.jsonName());
            if (embedded instanceof Duration) {
              writeDuration((Duration) embedded);
            } else {
              writeDuration((java.time.Duration) embedded);
            }
            break;
          case Timestamp:
            encoder.writeFieldName(field.jsonName());
            if (embedded instanceof Timestamp) {
              writeTimestamp((Timestamp) embedded);
            } else {
              writeTimestamp((OffsetDateTime) embedded);
            }
            break;
          case Struct:
            encoder.writeFieldName(field.jsonName());
            writeStruct((Struct) embedded);
            break;
          case Value:
            encoder.writeFieldName(field.jsonName());
            writeValue((Value) embedded);
            break;
          case ListValue:
            encoder.writeFieldName(field.jsonName());
            writeListValue((ListValue) embedded);
            break;
          case DoubleValue:
            encoder.writeFieldName(field.jsonName());
            writeDoubleValue((DoubleValue) embedded);
            break;
          case FloatValue:
            encoder.writeFieldName(field.jsonName());
            writeFloatValue((FloatValue) embedded);
            break;
          case StringValue:
            encoder.writeFieldName(field.jsonName());
            writeStringValue((StringValue) embedded);
            break;
          case BoolValue:
            encoder.writeFieldName(field.jsonName());
            writeBoolValue((BoolValue) embedded);
            break;
          case BytesValue:
            encoder.writeFieldName(field.jsonName());
            writeBytesValue((BytesValue) embedded);
            break;
          case Int32Value:
            encoder.writeFieldName(field.jsonName());
            writeInt32Value((Int32Value) embedded);
            break;
          case Int64Value:
            encoder.writeFieldName(field.jsonName());
            writeInt64Value((Int64Value) embedded);
            break;
          case UInt32Value:
            encoder.writeFieldName(field.jsonName());
            writeUInt32Value((UInt32Value) embedded);
            break;
          case UInt64Value:
            encoder.writeFieldName(field.jsonName());
            writeUInt64Value((UInt64Value) embedded);
            break;
          case FieldMask:
            encoder.writeFieldName(field.jsonName());
            writeFieldMask((FieldMask) embedded);
            break;
          default:
            ProtoVisitor.super.visitEmbedded(field, embedded, continuation);
        }
      } catch (IOException e) {
        throw new EncodeException(e.getMessage());
      }
    } else {
      ProtoVisitor.super.visitEmbedded(field, embedded, continuation);
    }
  }

  @Override
  public <T> void visitEmbedded(Field field, Iterator<T> iterator, BiConsumer<T, ProtoVisitor> continuation) {
    Type type = field.type();
    Callback callback;
    if (type.getClass() == MessageLiteral.class) {
      MessageLiteral messageLiteral = (MessageLiteral) type;
      callback = WELL_KNOWN_TYPES.get(messageLiteral);
    } else {
      callback = null;
    }
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      if (callback != null) {
        while (iterator.hasNext()) {
          T elt = iterator.next();
          callback.accept(this, elt);
        }
      } else {
        while (iterator.hasNext()) {
          T elt = iterator.next();
          encoder.writeStartObject();
          continuation.accept(elt, this);
          encoder.writeEndObject();
        }
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void enter(Field field) {
    if (!field.isMapValue()) {
      encoder.writeFieldName(field.jsonName());
    }
    encoder.writeStartObject();
  }

  @Override
  public void leave(Field field) {
    encoder.writeEndObject();
  }

  @Override
  public void visitString(Field field, String s) {
    try {
      if (field.isMapKey()) {
        encoder.writeFieldName(s);
      } else if (field.isMapValue()) {
        writeString(s);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeString(s);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitBytes(Field field, byte[] bytes) {
    try {
      if (field.isMapKey()) {
        throw new UnsupportedOperationException();
      } else if (field.isMapValue()) {
        writeBytes(bytes);
      } else {
        encoder.writeFieldName(field.jsonName());
        writeBytes(bytes);
      }
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void enterPacked(Field field) {

  }

  @Override
  public void leavePacked(Field field) {

  }

  @Override
  public <K, V> void visitMap(Field field, Iterator<Map.Entry<K, V>> entries, BiConsumer<Map.Entry<K, V>, ProtoVisitor> continuation) {
    if (entries.hasNext()) {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartObject();
      while (entries.hasNext()) {
        Map.Entry<K, V> entry = entries.next();
        continuation.accept(entry, this);
      }
      encoder.writeEndObject();
    }
  }

  @Override
  public void visitString(Field field, Iterator<String> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeString(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitFixed32(Field field, Iterator<Integer> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeFixed32(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSFixed32(Field field, Iterator<Integer> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeSFixed32(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitUInt32(Field field, Iterator<Integer> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeUInt32(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitInt32(Field field, Iterator<Integer> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeInt32(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSInt32(Field field, Iterator<Integer> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeSInt32(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitInt64(Field field, Iterator<Long> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeInt64(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitUInt64(Field field, Iterator<Long> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeUInt64(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSInt64(Field field, Iterator<Long> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeSInt64(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitFixed64(Field field, Iterator<Long> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeFixed64(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitDouble(Field field, Iterator<Double> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeDouble(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitSFixed64(Field field, Iterator<Long> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeSFixed64(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitFloat(Field field, Iterator<Float> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeFloat(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitBytes(Field field, Iterator<byte[]> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeBytes(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitEnum(Field field, Iterator<Integer> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeEnum(field, iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  @Override
  public void visitBool(Field field, Iterator<Boolean> iterator) {
    try {
      encoder.writeFieldName(field.jsonName());
      encoder.writeStartArray();
      while (iterator.hasNext()) {
        writeBool(iterator.next());
      }
      encoder.writeEndArray();
    } catch (IOException e) {
      throw new EncodeException(e.getMessage());
    }
  }

  private void writeBytes(byte[] bytes) throws IOException {
    encoder.writeBinary(bytes);
  }

  private void writeSFixed64(long v) throws IOException {
    writeLong(v);
  }

  private void writeFixed64(long v) throws IOException {
    writeString(encodeUInt64(v));
  }

  private void writeSInt64(long v) throws IOException {
    writeLong(v);
  }

  private void writeUInt64(long v) throws IOException {
    writeString(encodeUInt64(v));
  }

  private void writeInt64(long v) throws IOException {
    writeLong(v);
  }

  private void writeSInt32(int v) throws IOException {
    writeInt(v);
  }

  private void writeInt32(int v) throws IOException {
    writeInt(v);
  }

  private void writeUInt32(int v) throws IOException {
    writeString(encodeUInt32(v));
  }

  private void writeSFixed32(int v) throws IOException {
    writeInt(v);
  }

  private void writeFixed32(int v) throws IOException {
    writeLong((long) v & 0xFFFFFFFFL);
  }

  private void writeFloat(float f) throws IOException {
    encoder.writeFloat(f);
  }

  private void writeDouble(double d) throws IOException {
    encoder.writeDouble(d);
  }

  private void writeBool(boolean v) throws IOException {
    encoder.writeBoolean(v);
  }

  private void writeInt(int v) throws IOException {
    encoder.writeInt(v);
  }

  private void writeLong(long v) throws IOException {
    encoder.writeLong(v);
  }

  private void writeString(String s) throws IOException {
    encoder.writeString(s);
  }

  private void writeNull() throws IOException {
    encoder.writeNull();
  }

  private static String encodeUInt32(final int value) {
    if (value >= 0) {
      return Integer.toString(value);
    } else {
      return Long.toString(value & 0xFFFFFFFFL);
    }
  }

  private static String encodeUInt64(long value) {
    if (value >= 0) {
      return Long.toString(value);
    } else {
      return BigInteger.valueOf(value & Long.MAX_VALUE).setBit(Long.SIZE - 1).toString();
    }
  }
}
