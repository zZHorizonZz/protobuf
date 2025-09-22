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
package io.vertx.protobuf.tests.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import io.vertx.protobuf.schema.DefaultEnumType;
import io.vertx.protobuf.schema.DefaultMessageType;
import io.vertx.protobuf.schema.DefaultSchema;
import io.vertx.protobuf.schema.Field;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.schema.ScalarType;
import io.vertx.protobuf.tests.core.support.datatypes.DataTypesProto;
import io.vertx.protobuf.tests.core.support.datatypes.ScalarTypes;
import org.junit.Test;

public abstract class DataTypeTestBase {

  protected static final DefaultSchema SCHEMA = new DefaultSchema();

  protected static final DefaultMessageType SCALAR_TYPES = SCHEMA.of("ScalarTypes");
  protected static final Field INT32 = SCALAR_TYPES.addField(1, "int32", ScalarType.INT32);
  protected static final Field UINT32 = SCALAR_TYPES.addField(2, "uint32", ScalarType.UINT32);
  protected static final Field SINT32 = SCALAR_TYPES.addField(3, "sint32", ScalarType.SINT32);
  protected static final Field INT64 = SCALAR_TYPES.addField(4, "int64", ScalarType.INT64);
  protected static final Field UINT64 = SCALAR_TYPES.addField(5, "uint64", ScalarType.UINT64);
  protected static final Field SINT64 = SCALAR_TYPES.addField(6, "sint64", ScalarType.SINT64);
  protected static final Field BOOL = SCALAR_TYPES.addField(7, "bool", ScalarType.BOOL);
  protected static final Field FIXED32 = SCALAR_TYPES.addField(8, "fixed32", ScalarType.FIXED32);
  protected static final Field SFIXED32 = SCALAR_TYPES.addField(9, "sfixed32", ScalarType.SFIXED32);
  protected static final Field FLOAT = SCALAR_TYPES.addField(10, "_float", ScalarType.FLOAT);
  protected static final Field FIXED64 = SCALAR_TYPES.addField(11, "fixed64", ScalarType.FIXED64);
  protected static final Field SFIXED64 = SCALAR_TYPES.addField(12, "sfixed64", ScalarType.SFIXED64);
  protected static final Field DOUBLE = SCALAR_TYPES.addField(13, "_double", ScalarType.DOUBLE);
  protected static final Field STRING = SCALAR_TYPES.addField(14, "string", ScalarType.STRING);
  protected static final Field BYTES = SCALAR_TYPES.addField(15, "bytes", ScalarType.BYTES);

  protected static final DefaultMessageType ENUM_TYPES = SCHEMA.of("EnumTypes");
  protected static final Field ENUM = ENUM_TYPES.addField(1, "_enum", new DefaultEnumType()
    .addValue(0, "zero")
    .addValue(1, "one")
    .addValue(2, "two")
    .addValue(3, "three")
  );

  protected abstract void testDataType(RecordingVisitor visitor, MessageType messageType, MessageLite expected) throws Exception;

  protected void testDataType(RecordingVisitor visitor, DataTypesProto.ScalarTypes expected) throws Exception {
    testDataType(visitor, SCALAR_TYPES, expected);
  }

  @Test
  public void testFloat() throws Exception {
    testFloat(0);
    testFloat(4);
    testFloat(-4);
    testFloat(Float.MAX_VALUE);
    testFloat(Float.MIN_VALUE);
    testFloat(Float.NaN);
    testFloat(Float.POSITIVE_INFINITY);
    testFloat(Float.NEGATIVE_INFINITY);
  }

  private void testFloat(float value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitFloat(FLOAT, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setFloat(value).build());
  }

  @Test
  public void testDouble() throws Exception {
    testDouble(0);
    testDouble(4);
    testDouble(-4);
    testDouble(Double.MAX_VALUE);
    testDouble(Double.MIN_VALUE);
    testDouble(Double.NaN);
    testDouble(Double.POSITIVE_INFINITY);
    testDouble(Double.NEGATIVE_INFINITY);
  }

  private void testDouble(double value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitDouble(DOUBLE, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setDouble(value).build());
  }

  @Test
  public void testInt32() throws Exception {
    testInt32(0);
    testInt32(4);
    testInt32(-4);
    testInt32(Integer.MAX_VALUE);
    testInt32(Integer.MIN_VALUE);
  }

  private void testInt32(int value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitInt32(INT32, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setInt32(value).build());
  }

  @Test
  public void testUInt32() throws Exception {
    testUInt32(0);
    testUInt32(4);
    testUInt32(-1);
    testUInt32(Integer.MAX_VALUE);
  }

  private void testUInt32(int value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitUInt32(UINT32, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setUint32(value).build());
  }

  @Test
  public void testSInt32() throws Exception {
    testSInt32(0);
    testSInt32(4);
    testSInt32(-4);
    testSInt32(Integer.MAX_VALUE);
    testSInt32(Integer.MIN_VALUE);
  }

  private void testSInt32(int value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitSInt32(SINT32, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setSint32(value).build());
  }

  @Test
  public void testInt64() throws Exception {
    testInt64(0);
    testInt64(4);
    testInt64(-4);
    testInt64(Long.MAX_VALUE);
    testInt64(Long.MIN_VALUE);
  }

  private void testInt64(long value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitInt64(INT64, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setInt64(value).build());
  }

  @Test
  public void testUInt64() throws Exception {
    testUInt64(0);
    testUInt64(4);
    testUInt64(-1); // MAX
    testUInt64(Long.MAX_VALUE);
  }

  private void testUInt64(long value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitUInt64(UINT64, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setUint64(value).build());
  }

  @Test
  public void testSInt64() throws Exception {
    testSint64(0);
    testSint64(4);
    testSint64(-4);
    testSint64(Long.MAX_VALUE);
    testSint64(Long.MIN_VALUE);
  }

  private void testSint64(long value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitSInt64(SINT64, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setSint64(value).build());
  }

  @Test
  public void testFixed32() throws Exception {
    testFixed32(0);
    testFixed32(4);
    testFixed32(Integer.MAX_VALUE);
  }

  private void testFixed32(int value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitFixed32(FIXED32, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setFixed32(value).build());
  }

  @Test
  public void testFixed64() throws Exception {
//    testFixed64(0);
//    testFixed64(4);
    testFixed64(Integer.MAX_VALUE);
  }

  private void testFixed64(long value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitFixed64(FIXED64, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setFixed64(value).build());
  }

  @Test
  public void testSFixed32() throws Exception {
    testSFixed32(0);
    testSFixed32(4);
    testSFixed32(Integer.MAX_VALUE);
  }

  private void testSFixed32(int value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitSFixed32(SFIXED32, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setSfixed32(value).build());
  }

  @Test
  public void testSFixed64() throws Exception {
    testSFixed64(0);
    testSFixed64(4);
    testSFixed64(Integer.MAX_VALUE);
  }

  private void testSFixed64(long value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    if (value != 0) {
      visitor.visitSFixed64(SFIXED64, value);
    }
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setSfixed64(value).build());
  }

  @Test
  public void testStringRecord() throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitString(STRING, "hello");
    visitor.destroy();
    DataTypesProto.ScalarTypes dataTypes = DataTypesProto.ScalarTypes.newBuilder().setString("hello").build();
    ScalarTypes d = new ScalarTypes();
    d.setString("hello");
    testDataType(visitor, dataTypes);
  }

  @Test
  public void testBytesRecord() throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitBytes(BYTES, "hello".getBytes());
    visitor.destroy();
    DataTypesProto.ScalarTypes dataTypes = DataTypesProto.ScalarTypes.newBuilder().setBytes(ByteString.copyFromUtf8("hello")).build();
    testDataType(visitor, dataTypes);
  }

  @Test
  public void testBool() throws Exception {
    testBool(true);
  }

  private void testBool(boolean value) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitBool(BOOL, value);
    visitor.destroy();
    testDataType(visitor, DataTypesProto.ScalarTypes.newBuilder().setBool(value).build());
  }

  @Test
  public void testEnum() throws Exception {
    testEnum(1);
    testEnum(2);
    testEnum(3);
  }

  private void testEnum(int number) throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(ENUM_TYPES);
    visitor.visitEnum(ENUM, number);
    visitor.destroy();
    testDataType(visitor, ENUM_TYPES, DataTypesProto.EnumTypes.newBuilder().setEnumValue(number).build());
  }
}
