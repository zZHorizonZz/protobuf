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

import io.vertx.protobuf.core.ProtobufReader;
import io.vertx.protobuf.core.ProtobufWriter;
import io.vertx.protobuf.schema.Field;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.tests.core.support.map.Enumerated;
import io.vertx.protobuf.tests.core.support.map.MapKeyVariant;
import io.vertx.protobuf.tests.core.support.map.MapProto;
import io.vertx.protobuf.tests.core.support.map.MapValueVariant;
import io.vertx.protobuf.tests.core.support.map.ProtoReader;
import io.vertx.protobuf.tests.core.support.map.ProtoWriter;
import io.vertx.protobuf.tests.core.support.map.FieldLiteral;
import io.vertx.protobuf.tests.core.support.map.MessageLiteral;
import io.vertx.protobuf.tests.core.support.map.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MapTest {

  @Test
  public void testEmptyMapKeyVariant() throws Exception {
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_string, MapKeyVariant::getString, "");
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_int32, MapKeyVariant::getInt32, 0);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_int64, MapKeyVariant::getInt64, 0L);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_uint32, MapKeyVariant::getUint32, 0);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_uint64, MapKeyVariant::getUint64, 0L);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_sint32, MapKeyVariant::getSint32, 0);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_sint64, MapKeyVariant::getSint64, 0L);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_bool, MapKeyVariant::getBool, false);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_fixed64, MapKeyVariant::getFixed64, 0L);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_sfixed64, MapKeyVariant::getSfixed64, 0L);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_fixed32, MapKeyVariant::getFixed32, 0);
    testEmptyMapKeyVariant(FieldLiteral.MapKeyVariant_sfixed32, MapKeyVariant::getSfixed32, 0);
  }

  public <K> void testEmptyMapKeyVariant(Field mapField, Function<MapKeyVariant, Map<K, Integer>> extractor, K expected) throws Exception {
    byte[] buffer = ProtobufWriter.encodeToByteArray(visitor -> {
      visitor.init(MessageLiteral.MapKeyVariant);
      visitor.enter(mapField);
      visitor.leave(mapField);
      visitor.destroy();
    });
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.MapKeyVariant, reader, buffer);
    MapKeyVariant map = (MapKeyVariant) reader.stack.pop();
    Map<K, Integer> entries = extractor.apply(map);
    assertEquals(1, entries.size());
    Map.Entry<K, Integer> entry = entries.entrySet().iterator().next();
    assertEquals(expected, entry.getKey());
    assertEquals(0, (int)entry.getValue());
  }

  @Test
  public void testReorderedMapKeyVariant() throws Exception {
    Field mapField = FieldLiteral.MapKeyVariant_string;
    byte[] buffer = ProtobufWriter.encodeToByteArray(visitor -> {
      visitor.init(MessageLiteral.MapKeyVariant);
      visitor.enter(mapField);
      visitor.visitInt32(((MessageType)mapField.type()).field(2), 4);
      visitor.visitString(((MessageType)mapField.type()).field(1), "string-value");
      visitor.leave(mapField);
      visitor.destroy();
    });
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.MapKeyVariant, reader, buffer);
    MapKeyVariant map = (MapKeyVariant) reader.stack.pop();
    Map<String, Integer> entries = map.getString();
    assertEquals(1, entries.size());
    Map.Entry<String, Integer> entry = entries.entrySet().iterator().next();
    assertEquals("string-value", entry.getKey());
    assertEquals(4, (int)entry.getValue());
    byte[] serialized = ProtobufWriter.encodeToByteArray(v -> ProtoWriter.emit(map, v));
    MapProto.MapKeyVariant.parseFrom(serialized);
  }

  @Test
  public void testEmptyMapValueVariant() throws Exception {
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_string_v, MapValueVariant::getStringV, "");
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_bytes_v, MapValueVariant::getBytesV, new byte[0]);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_int32_v, MapValueVariant::getInt32V, 0);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_int64_v, MapValueVariant::getInt64V, 0L);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_uint32_v, MapValueVariant::getUint32V, 0);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_uint64_v, MapValueVariant::getUint64V, 0L);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_sint32_v, MapValueVariant::getSint32V, 0);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_sint64_v, MapValueVariant::getSint64V, 0L);
    testEmptyMapValueVariant(FieldLiteral.MapKeyVariant_bool, MapValueVariant::getBoolV, false);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant__enum_v, MapValueVariant::getEnumV, Enumerated.constant_0);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_fixed64_v, MapValueVariant::getFixed64V, 0L);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_sfixed64_v, MapValueVariant::getSfixed64V, 0L);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant__double_v, MapValueVariant::getDoubleV, 0D);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_fixed32_v, MapValueVariant::getFixed32V, 0);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant_sfixed32_v, MapValueVariant::getSfixed32V, 0);
    testEmptyMapValueVariant(FieldLiteral.MapValueVariant__float_v, MapValueVariant::getFloatV, 0F);
    this.<Value>testEmptyMapValueVariant(FieldLiteral.MapValueVariant__message_v, MapValueVariant::getMessageV, Assert::assertNotNull);
  }

  public <V> void testEmptyMapValueVariant(Field mapField, Function<MapValueVariant, Map<Integer, V>> extractor, V expected) throws Exception {
    this.<V>testEmptyMapValueVariant(mapField, extractor, actual -> {
      if (expected instanceof byte[] && actual instanceof byte[]) {
        byte[] expectedBytes = (byte[]) expected;
        byte[] actualBytes = (byte[]) actual;
        assertArrayEquals(expectedBytes, actualBytes);
      } else {
        assertEquals(expected, actual);
      }
    });
  }

  public <V> void testEmptyMapValueVariant(Field mapField, Function<MapValueVariant, Map<Integer, V>> extractor, Consumer<V> predicate) throws Exception {
    byte[] buffer = ProtobufWriter.encodeToByteArray(visitor -> {
      visitor.init(MessageLiteral.MapValueVariant);
      visitor.enter(mapField);
      visitor.leave(mapField);
      visitor.destroy();
    });
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.MapValueVariant, reader, buffer);
    MapValueVariant map = (MapValueVariant) reader.stack.pop();
    Map<Integer, V> entries = extractor.apply(map);
    assertEquals(1, entries.size());
    Map.Entry<Integer, V> entry = entries.entrySet().iterator().next();
    assertEquals(0, (int)entry.getKey());
    predicate.accept(entry.getValue());
  }
}
