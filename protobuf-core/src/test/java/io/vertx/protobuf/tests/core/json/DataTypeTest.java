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
package io.vertx.protobuf.tests.core.json;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.vertx.protobuf.core.DecodeException;
import io.vertx.protobuf.core.json.ProtoJsonWriter;
import io.vertx.protobuf.tests.core.support.datatypes.ScalarTypes;
import io.vertx.core.json.JsonObject;
import io.vertx.protobuf.core.json.ProtoJsonReader;
import io.vertx.protobuf.schema.Field;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.schema.ScalarType;
import io.vertx.protobuf.schema.WireType;
import io.vertx.protobuf.tests.core.DataTypeTestBase;
import io.vertx.protobuf.tests.core.RecordingVisitor;
import io.vertx.protobuf.tests.core.support.datatypes.DataTypesProto;
import io.vertx.protobuf.tests.core.support.datatypes.EnumTypes;
import io.vertx.protobuf.tests.core.support.datatypes.Enumerated;
import io.vertx.protobuf.tests.core.support.datatypes.MessageLiteral;
import io.vertx.protobuf.tests.core.support.datatypes.ProtoReader;
import io.vertx.protobuf.tests.core.support.datatypes.ProtoWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataTypeTest extends DataTypeTestBase {

  @Parameterized.Parameters
  public static Collection<Object[]> params() {
    return ProtoJsonWriterProvider.all();
  }

  private final ProtoJsonWriterProvider writerProvider;

  public DataTypeTest(ProtoJsonWriterProvider writerProvider) {
    this.writerProvider = writerProvider;
  }

  @Override
  protected void testDataType(RecordingVisitor visitor, MessageType messageType, MessageLite expected) throws Exception {
    String json = JsonFormat.printer().print((MessageOrBuilder) expected);

    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse(json, messageType, checker);
    assertTrue(checker.isEmpty());

    // Try parsing string formatted numbers as real numbers, this must be parseable
    JsonObject jsonObject = new JsonObject(json);
    for (Map.Entry<String, Object> entry : jsonObject) {
      Object value = entry.getValue();
      if (value instanceof String) {
        try {
          entry.setValue(Long.parseLong((String) value));
        } catch (NumberFormatException ignore) {
        }
      } else if (value instanceof Number) {
        entry.setValue(value.toString());
      } else {
      }
    }
    json = jsonObject.encode();
    checker = visitor.checker();
    ProtoJsonReader.parse(json, messageType, checker);
    assertTrue(checker.isEmpty());

    String encoded = writerProvider.encodeToString(visitor::apply);
    Message.Builder builder = ((Message) expected).newBuilderForType();
    JsonFormat.parser().merge(encoded, builder);
    assertEquals(expected, builder.build());
  }

  @Test
  public void testNullValue() throws Exception {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.destroy();
    DataTypesProto.ScalarTypes b = DataTypesProto.ScalarTypes.newBuilder()
      .setInt32(1)
      .setUint32(1)
      .setSint32(1)
      .setInt64(1)
      .setUint64(1)
      .setSint64(1)
      .setBool(true)
      .setFixed32(1)
      .setSfixed32(1)
      .setFloat(1)
      .setFixed64(1)
      .setSfixed64(1)
      .setDouble(1)
      .setString("s")
      .setBytes(ByteString.copyFromUtf8("s"))
      .build();
    JsonObject json = new JsonObject(JsonFormat.printer().print(b));
    for (Map.Entry<String, Object> entry : json) {
      entry.setValue(null);
    }
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse(json.toString(), SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testParseDefaultValue() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitBool(BOOL, false);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"bool\":false}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testInt32QuotedExponentNotation() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitInt32(INT32, 500);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"int32\":\"5e2\"}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUInt32QuotedExponentNotation() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt32(UINT32, 500);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"uint32\":\"5e2\"}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUInt32() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt32(UINT32, -1);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"uint32\":\"" + BigInteger.valueOf(0xFFFFFFFFL) + "\"}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
    assertEquals(BigInteger.valueOf(0xFFFFFFFFL).toString(), writerProvider.encodeToObject(visitor::apply).getValue("uint32"));
  }

  @Test
  public void testFixed32() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitFixed32(FIXED32, -1);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"fixed32\":\"" + BigInteger.valueOf(0xFFFFFFFFL) + "\"}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
    assertEquals(4294967295L, writerProvider.encodeToObject(visitor::apply).getValue("fixed32"));
  }

  @Test
  public void testXInt64() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitInt64(INT64, 1);
    visitor.visitUInt64(UINT64, 1);
    visitor.visitSInt64(SINT64, 1);
    visitor.visitFixed64(FIXED64, 1);
    visitor.visitSFixed64(SFIXED64, 1);
    visitor.destroy();
    JsonObject json = new JsonObject(ProtoJsonWriter.encode(visitor));
    assertEquals("1", json.getValue("int64"));
    assertEquals("1", json.getValue("uint64"));
    assertEquals("1", json.getValue("sint64"));
    assertEquals("1", json.getValue("fixed64"));
    assertEquals("1", json.getValue("sfixed64"));
  }

  @Test
  public void testUInt64() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt64(UINT64, -2048);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"uint64\":\"18446744073709549568\"}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
    assertEquals("18446744073709549568", writerProvider.encodeToObject(visitor::apply).getValue("uint64"));
  }

  @Test
  public void testFixed64() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitFixed64(FIXED64, -1);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"fixed64\":\"18446744073709551615\"}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
    assertEquals("18446744073709551615", writerProvider.encodeToObject(visitor::apply).getValue("fixed64"));
  }

  @Test
  public void testEnumNumber() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(ENUM_TYPES);
    visitor.visitEnum(ENUM, 1);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"_enum\":1}", ENUM_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  // {"optionalDouble": 1.89769e+308}

  @Test
  public void testEmptyNumber() {
    for (Field field : SCALAR_TYPES.fields()) {
      if (field.type() instanceof ScalarType && field.type().wireType() != WireType.LEN) {
        assertDecodeException(field.jsonName(), "\"\"");
      }
    }
  }

  @Test
  public void testDoubleInfinity() {
    for (String infinity : Arrays.asList("Infinity", "-Infinity")) {
      RecordingVisitor visitor = new RecordingVisitor();
      visitor.init(SCALAR_TYPES);
      visitor.visitDouble(DOUBLE, Double.parseDouble(infinity));
      visitor.destroy();
      RecordingVisitor.Checker checker = visitor.checker();
      ProtoJsonReader.parse("{\"" + DOUBLE.jsonName() + "\":\"" + infinity + "\"}", SCALAR_TYPES, checker);
      assertTrue(checker.isEmpty());
    }
  }

  @Test
  public void testFloatInfinity() {
    for (String infinity : Arrays.asList("Infinity", "-Infinity")) {
      RecordingVisitor visitor = new RecordingVisitor();
      visitor.init(SCALAR_TYPES);
      visitor.visitFloat(FLOAT, Float.parseFloat(infinity));
      visitor.destroy();
      RecordingVisitor.Checker checker = visitor.checker();
      ProtoJsonReader.parse("{\"" + FLOAT.jsonName() + "\":\"" + infinity + "\"}", SCALAR_TYPES, checker);
      assertTrue(checker.isEmpty());
    }
  }

  @Test
  public void testInvalidDouble() {
    assertDecodeException(DOUBLE.jsonName(), "\"1.89769e+308\"");
    assertDecodeException(DOUBLE.jsonName(), "\"-1.89769e+308\"");
    assertDecodeException(DOUBLE.jsonName(), "1.89769e+308");
    assertDecodeException(DOUBLE.jsonName(), "-1.89769e+308");
  }

  @Test
  public void testInvalidFloat() {
    assertDecodeException(FLOAT.jsonName(), "\"3.502823e+38\"");
    assertDecodeException(FLOAT.jsonName(), "\"-3.502823e+38\"");
    assertDecodeException(FLOAT.jsonName(), "3.502823e+38");
    assertDecodeException(FLOAT.jsonName(), "-3.502823e+38");
  }

  @Test
  public void testInvalidInteger() {
    Field[] fields = {
      INT32,
      UINT32,
      SINT32,
      FIXED32,
      SFIXED32,
      INT64,
      UINT64,
      SINT64,
      FIXED64,
      SFIXED64
    };
    for (Field field : fields) {
      assertDecodeException(field.jsonName(), "0.5");
      assertDecodeException(field.jsonName(), "\"0.5\"");
    }
  }

  @Test
  public void testIntegerInteger() {
    Field[] fields = {
      INT32,
      UINT32,
      SINT32,
      FIXED32,
      SFIXED32
    };
    for (Field field : fields) {
      assertDecodeException(field.jsonName(), "4294967296");
      assertDecodeException(field.jsonName(), "\"4294967296\"");
    }
  }

  @Test
  public void testUInt32MaxValue() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt32(UINT32, -1);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"" + UINT32.jsonName() + "\":" + 0xFFFFFFFFL + "}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUInt64MaxValue() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt64(UINT64, -1);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"" + UINT64.jsonName() + "\":" + new BigInteger("FFFFFFFFFFFFFFFF", 16) + "}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testParseExactFloatingValueInt32() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitInt32(INT32, 4);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"" + INT32.jsonName() + "\":4.0}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testParseExactFloatingValueInt64() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitInt64(INT64, 4);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"" + INT64.jsonName() + "\":4.0}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testParseExactFloatingValueUInt32() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt32(UINT32, 4);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"" + UINT32.jsonName() + "\":4.0}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testParseExactFloatingValueUInt64() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    visitor.visitUInt64(UINT64, 4);
    visitor.destroy();
    RecordingVisitor.Checker checker = visitor.checker();
    ProtoJsonReader.parse("{\"" + UINT64.jsonName() + "\":4.0}", SCALAR_TYPES, checker);
    assertTrue(checker.isEmpty());
  }

  private static void assertDecodeException(String fieldName, String value) {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(SCALAR_TYPES);
    RecordingVisitor.Checker checker = visitor.checker();
    try {
      ProtoJsonReader.parse("{\"" + fieldName + "\":" + value + "}", SCALAR_TYPES, checker);
      fail();
    } catch (DecodeException expected) {
    }
    assertTrue(checker.isEmpty());
  }

  @Test
  public void testUnknownNamedEnum() {
    ProtoReader reader = new ProtoReader();
    try {
      ProtoJsonReader.parse("{\"_enum\":\"unknown\"}", MessageLiteral.EnumTypes, reader);
      fail();
    } catch (DecodeException expected) {

    }
  }

  @Test
  public void testUnknownIndexedEnum() {
    ProtoReader reader = new ProtoReader();
    ProtoJsonReader.parse("{\"_enum\":123}", MessageLiteral.EnumTypes, reader);
    EnumTypes c = (EnumTypes) reader.stack.pop();
    Enumerated enumerated = c.getEnum();
    assertTrue(enumerated.isUnknown());
    assertNull(enumerated.asEnum());
    try {
      assertEquals("unknown", enumerated.name());
      fail();
    } catch (IllegalStateException expected) {
    }
    assertEquals(123, enumerated.number());
    JsonObject json = writerProvider.encodeToObject(v -> ProtoWriter.emit(c, v));
    assertEquals(123, json.getValue("Enum"));
  }

  @Test
  public void testURLSafeBase64Bytes() {
    String s = Base64.getUrlEncoder().encodeToString(new byte[]{-5});
    ScalarTypes st = ProtoReader.readScalarTypes(ProtoJsonReader.readStream(MessageLiteral.ScalarTypes, "{\"bytes\":\"" + s + "\"}"));
    byte[] bytes = st.getBytes();
    assertEquals(1, bytes.length);
    assertEquals(-5, bytes[0]);
  }
}
