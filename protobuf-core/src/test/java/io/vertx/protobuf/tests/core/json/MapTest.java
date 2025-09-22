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

import io.vertx.core.json.JsonObject;
import io.vertx.protobuf.core.json.ProtoJsonReader;
import io.vertx.protobuf.tests.core.support.map.MapValueVariant;
import io.vertx.protobuf.tests.core.support.map.ProtoReader;
import io.vertx.protobuf.tests.core.support.map.MessageLiteral;
import io.vertx.protobuf.tests.core.support.map.MapKeyVariant;
import io.vertx.protobuf.tests.core.support.map.ProtoWriter;
import io.vertx.protobuf.tests.core.support.map.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MapTest {

  @Parameterized.Parameters
  public static Collection<Object[]> params() {
    return ProtoJsonWriterProvider.all();
  }

  private final ProtoJsonWriterProvider writerProvider;

  public MapTest(ProtoJsonWriterProvider writerProvider) {
    this.writerProvider = writerProvider;
  }

  @Test
  public void testMultipleEntries() {
    JsonObject json = new JsonObject().put("string", new JsonObject()
      .put("10", 10)
      .put("20", 20)
      .put("50", 50)
    );
    MapKeyVariant map = parseMap(json.encode());
    assertEquals(50, (int)map.getString().get("50"));
  }

  @Test
  public void testParseStringKey() {
    MapKeyVariant map = parseMap("{\"string\":{\"foo\":50}}");
    assertEquals(50, (int)map.getString().get("foo"));
  }

  @Test
  public void testParseInt32Key() {
    MapKeyVariant map = parseMap("{\"int32\":{\"4\":50}}");
    assertEquals(50, (int)map.getInt32().get(4));
  }

  @Test
  public void testParseInt64Key() {
    MapKeyVariant map = parseMap("{\"int64\":{\"4\":50}}");
    assertEquals(50, (int)map.getInt64().get(4L));
  }

  @Test
  public void testParseUInt32Key() {
    MapKeyVariant map = parseMap("{\"uint32\":{\"4\":50}}");
    assertEquals(50, (int)map.getUint32().get(4));
  }

  @Test
  public void testParseUInt64Key() {
    MapKeyVariant map = parseMap("{\"uint64\":{\"4\":50}}");
    assertEquals(50, (int)map.getUint64().get(4L));
  }

  @Test
  public void testParseSInt32Key() {
    MapKeyVariant map = parseMap("{\"sint32\":{\"4\":50}}");
    assertEquals(50, (int)map.getSint32().get(4));
  }

  @Test
  public void testParseSInt64Key() {
    MapKeyVariant map = parseMap("{\"sint64\":{\"4\":50}}");
    assertEquals(50, (int)map.getSint64().get(4L));
  }

  @Test
  public void testParseBoolKey() {
    MapKeyVariant map = parseMap("{\"bool\":{\"true\":50}}");
    assertEquals(50, (int)map.getBool().get(true));
  }

  @Test
  public void testParseFixed64Key() {
    MapKeyVariant map = parseMap("{\"fixed64\":{\"4\":50}}");
    assertEquals(50, (int)map.getFixed64().get(4L));
  }

  @Test
  public void testParseSFixed64Key() {
    MapKeyVariant map = parseMap("{\"sfixed64\":{\"4\":50}}");
    assertEquals(50, (int)map.getSfixed64().get(4L));
  }

  @Test
  public void testParseFixed32Key() {
    MapKeyVariant map = parseMap("{\"fixed32\":{\"4\":50}}");
    assertEquals(50, (int)map.getFixed32().get(4));
  }

  @Test
  public void testParseSFixed32Key() {
    MapKeyVariant map = parseMap("{\"sfixed32\":{\"4\":50,\"5\":10}}");
    assertEquals(50, (int)map.getSfixed32().get(4));
  }

  private MapKeyVariant parseMap(String json) {

    JsonObject src = new JsonObject(json);

    ProtoReader pr = new ProtoReader();
    ProtoJsonReader.parse(json, MessageLiteral.MapKeyVariant, pr);

    MapKeyVariant res = (MapKeyVariant) pr.stack.pop();
    JsonObject encoded = writerProvider.encodeToObject(v -> ProtoWriter.emit(res, v));

    assertEquals(src, encoded);

    return res;
  }

  @Test
  public void testSerializeMapValue() {
    ProtoReader pr = new ProtoReader();
    ProtoJsonReader.parse("{\"_message_v\":{\"15\":{\"value\":4}}}", MessageLiteral.MapValueVariant, pr);
    MapValueVariant pop = (MapValueVariant) pr.stack.pop();
    assertEquals(4, (int)pop.getMessageV().get(15).getValue());
  }

  @Test
  public void testMapOfMessage() {
    MapValueVariant container = new MapValueVariant();
    Value value1 = new Value().setValue(1);
    Value value2 = new Value().setValue(2);
    container.getMessageV().put(1, value1);
    container.getMessageV().put(2, value2);
    JsonObject output = writerProvider.encodeToObject(ProtoWriter.streamOf(container));
    JsonObject expected = new JsonObject()
      .put("MessageV", new JsonObject()
        .put("1", new JsonObject().put("value", 1))
        .put("2", new JsonObject().put("value", 2))
      );
    assertEquals(expected, output);

  }
}
