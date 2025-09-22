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

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.vertx.core.json.JsonObject;
import io.vertx.protobuf.core.ProtobufWriter;
import io.vertx.protobuf.well_known_types.ProtoWriter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.ByteArrayOutputStream;

@State(Scope.Thread)
public class ProtobufEncodeBenchmark extends BenchmarkBase {

  private Struct struct;
  private io.vertx.protobuf.well_known_types.Struct vertxStruct;
  private JsonObject json;

  @Setup
  public void setup() {
    struct = Struct
      .newBuilder()
      .putFields("the-string", Value.newBuilder().setStringValue("the-string-value").build())
      .putFields("the-number", Value.newBuilder().setNumberValue(4).build())
      .putFields("the-boolean", Value.newBuilder().setBoolValue(true).build())
      .putFields("the-null", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
      .putFields("the-object", Value.newBuilder().setStructValue(Struct
        .newBuilder()
        .putFields("the-string", Value.newBuilder().setStringValue("the-string-value").build())
        .putFields("the-number", Value.newBuilder().setNumberValue(4).build())
        .putFields("the-boolean", Value.newBuilder().setBoolValue(true).build())
        .putFields("the-null", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
        .build()).build())
      .build();
    json = new JsonObject()
      .put("the-string", "the-string-value")
      .put("the-number", 4)
      .put("the-boolean", true)
      .put("the-null", null)
      .put("the-object", new JsonObject()
        .put("the-string", "the-string-value")
        .put("the-number", 4)
        .put("the-boolean", true)
        .put("the-null", null));
    vertxStruct = new io.vertx.protobuf.well_known_types.Struct();
    vertxStruct.getFields().put("the-string", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofStringValue("the-string-value")));
    vertxStruct.getFields().put("the-number", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofNumberValue(4D)));
    vertxStruct.getFields().put("the-boolean", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofBoolValue(true)));
    vertxStruct.getFields().put("the-null", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofNullValue(io.vertx.protobuf.well_known_types.NullValue.Enum.NULL_VALUE)));
    io.vertx.protobuf.well_known_types.Struct nested = new io.vertx.protobuf.well_known_types.Struct();
    nested.getFields().put("the-string", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofStringValue("the-string-value")));
    nested.getFields().put("the-number", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofNumberValue(4D)));
    nested.getFields().put("the-boolean", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofBoolValue(true)));
    nested.getFields().put("the-null", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofNullValue(io.vertx.protobuf.well_known_types.NullValue.Enum.NULL_VALUE)));
    vertxStruct.getFields().put("the-object", new io.vertx.protobuf.well_known_types.Value().setKind(io.vertx.protobuf.well_known_types.Value.Kind.ofStructValue(nested)));
  }

  @Benchmark
  public byte[] structToByteArray() {
    return struct.toByteArray();
  }

  @Benchmark
  public byte[] structToOutputStream() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CodedOutputStream cos = CodedOutputStream.newInstance(baos);
    try {
      struct.writeTo(cos);
    } finally {
      cos.flush();
      baos.close();
    }
    return baos.toByteArray();
  }

  @Benchmark
  public byte[] vertxStruct() {
    return ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(vertxStruct, visitor));
  }

  @Benchmark
  public byte[] jsonObject() {
//    return Json.encodeToByteArray(json);
    throw new UnsupportedOperationException();
  }
}
