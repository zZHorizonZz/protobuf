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
package com.julienviet.protobuf.tests.core.json;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.vertx.core.json.JsonObject;
import com.julienviet.protobuf.tests.core.support.json.JsonProto;
import com.julienviet.protobuf.tests.core.support.json.ProtoWriter;
import com.julienviet.protobuf.tests.core.support.json.Unpacked;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ProtoJsonWriterTest {

  @Parameterized.Parameters
  public static Collection<Object[]> params() {
    return ProtoJsonWriterProvider.all();
  }

  private final ProtoJsonWriterProvider writerProvider;

  public ProtoJsonWriterTest(ProtoJsonWriterProvider writerProvider) {
    this.writerProvider = writerProvider;
  }

  @Test
  public void testListOfNumbers() {
    JsonProto.Unpacked expected = JsonProto.Unpacked.newBuilder().addAllListOfNumbers(Arrays.asList(1, 2, 3, 4)).build();
    Unpacked unpacked = new Unpacked();
    unpacked.getListOfNumbers().addAll(Arrays.asList(1, 2, 3, 4));
    assertEquals(expected, unpacked);
  }

  @Test
  public void testListOfEmbedded() {
    JsonProto.Unpacked expected = JsonProto.Unpacked.newBuilder().addListOfEmbedded(JsonProto.Unpacked.newBuilder().addAllListOfNumbers(Arrays.asList(1, 2, 3, 4)).build()).build();
    Unpacked unpacked = new Unpacked();
    unpacked.getListOfEmbedded().add(new Unpacked().setListOfNumbers(Arrays.asList(1, 2, 3, 4)));
    assertEquals(expected, unpacked);
  }

  private void assertEquals(JsonProto.Unpacked expected, Unpacked unpacked) {
    JsonObject expectedJson;
    try {
      expectedJson = new JsonObject(JsonFormat.printer().print(expected));
    } catch (InvalidProtocolBufferException e) {
      throw new AssertionFailedError(e.getMessage());
    }
    JsonObject json = writerProvider.encodeToObject(v -> ProtoWriter.emit(unpacked, v));
    Assert.assertEquals(expectedJson, json);
  }
}
