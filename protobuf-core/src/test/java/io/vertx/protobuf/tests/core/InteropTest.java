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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.protobuf.core.ProtoVisitor;
import io.vertx.protobuf.core.ProtobufReader;
import io.vertx.protobuf.core.ProtobufWriter;
import io.vertx.protobuf.core.interop.ProtoWriter;
import io.vertx.protobuf.tests.core.support.interop.Container;
import io.vertx.protobuf.tests.core.support.interop.InteropProto;
import io.vertx.protobuf.tests.core.support.interop.MessageLiteral;
import io.vertx.protobuf.tests.core.support.interop.ProtoReader;
import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.time.Duration;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class InteropTest extends InteropTestBase {

  @Override
  protected Container read(InteropProto.Container src) {
    byte[] bytes = src.toByteArray();
   io.vertx.protobuf.tests.core.support.interop.ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Container, reader, bytes);
    return (Container) reader.stack.pop();
  }

  protected InteropProto.Container write(Container src) {
    byte[] bytes = ProtobufWriter.encodeToByteArray(v ->io.vertx.protobuf.tests.core.support.interop.ProtoWriter.emit(src, v));
    try {
      return InteropProto.Container.parseFrom(bytes);
    } catch (InvalidProtocolBufferException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
  }

  @Test
  public void testDecodeStruct() throws Exception {
    testDecodeStruct(new JsonObject().put("string-1", "the-string-1").put("string-2", "the-string-2"));
    testDecodeStruct(new JsonObject().put("number-1", 0).put("number-2", 4321));
    testDecodeStruct(new JsonObject().put("object", new JsonObject().put("string", "the-string")));
    testDecodeStruct(new JsonObject().put("object", new JsonArray().add(1)));
    testDecodeStruct(new JsonObject().put("null-1", null).put("null-2", null));
    testDecodeStruct(new JsonObject().put("true", true).put("false", false));
    testDecodeStruct(new JsonObject().put("object", new JsonArray().add(new JsonObject().put("string", "the-string")).add(4)));
    testDecodeStruct(new JsonObject().put("object", new JsonArray().add(new JsonObject().put("string", "the-string")).add(4)));
  }

  private void testDecodeStruct(Object value) throws Exception {
    String s = Json.encode(value);
    Struct.Builder builder = Struct.newBuilder();
    JsonFormat.parser().merge(s, builder);
    byte[] protobuf = builder.build().toByteArray();
  }

  @Test
  public void testEncodeStruct() throws Exception {
    testEncodeStruct(new JsonObject().put("string-1", "the-string-1").put("string-2", "the-string-2"));
    testEncodeStruct(new JsonObject().put("number-1", 0).put("number-2", 4321));
    testEncodeStruct(new JsonObject().put("object", new JsonObject().put("string", "the-string")));
    testEncodeStruct(new JsonObject().put("object", new JsonArray().add(1)));
    testEncodeStruct(new JsonObject().put("null-1", null).put("null-2", null));
    testEncodeStruct(new JsonObject().put("true", true).put("false", false));
    testEncodeStruct(new JsonObject().put("object", new JsonArray().add(new JsonObject().put("string", "the-string")).add(4)));
    testEncodeStruct(new JsonObject()
      .put("the-string", "the-string-value")
      .put("the-number", 4)
      .put("the-boolean", true)
      .put("the-null", null)
      .put("the-object", new JsonObject()
        .put("the-string", "the-string-value")
        .put("the-number", 4)
        .put("the-boolean", true)
        .put("the-null", null)));
  }

  private void testEncodeStruct(JsonObject json) throws Exception {
//    byte[] buffer = io.vertx.protobuf.json.Json.encodeToBuffer(json);
//    String S1 = new BigInteger(1, buffer).toString(16);
//    Struct.Builder builder = Struct.newBuilder();
//    JsonFormat.parser().merge(json.encode(), builder);
//    byte[] real = builder.build().toByteArray();
//    String S2 = new BigInteger(1, real).toString(16);
//    assertEquals(S2, S1);
  }

  @Test
  public void testDecodeDuration() throws Exception {
    testDecodeDuration(1, 1);
    testDecodeDuration(0, 0);
    testDecodeDuration(1, 0);
    testDecodeDuration(0, 1);
  }

  private void testDecodeDuration(long seconds, int nano) throws Exception {
    byte[] bytes = com.google.protobuf.Duration
      .newBuilder()
      .setSeconds(seconds).setNanos(nano).build()
      .toByteArray();

    io.vertx.protobuf.core.interop.ProtoReader builder = new io.vertx.protobuf.core.interop.ProtoReader();
    ProtobufReader.parse(io.vertx.protobuf.well_known_types.MessageLiteral.Duration, builder, bytes);
    Duration duration = (Duration) builder.pop();
    assertEquals(seconds, duration.getSeconds());
    assertEquals(nano, duration.getNano());
  }

  @Test
  public void testEncodeDuration() throws Exception {
    testEncodeDuration(1, 1);
    testEncodeDuration(0, 0);
    testEncodeDuration(1, 0);
    testEncodeDuration(0, 1);
  }

  private void testEncodeDuration(long seconds, int nano) throws Exception {
    Consumer<ProtoVisitor> consumer = visitor -> {
      ProtoWriter.emit(Duration.ofSeconds(seconds, nano), visitor);
    };
    byte[] buffer = ProtobufWriter.encodeToByteArray(consumer);
    com.google.protobuf.Duration duration = com.google.protobuf.Duration.parseFrom(buffer);
    assertEquals(seconds, duration.getSeconds());
    assertEquals(nano, duration.getNanos());
  }
}
