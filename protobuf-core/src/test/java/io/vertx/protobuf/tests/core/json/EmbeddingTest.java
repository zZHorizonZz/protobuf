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

import com.google.protobuf.util.JsonFormat;
import io.vertx.protobuf.core.json.ProtoJsonReader;
import io.vertx.protobuf.tests.core.support.embedding.Container;
import io.vertx.protobuf.tests.core.support.embedding.EmbeddingProto;
import io.vertx.protobuf.tests.core.support.embedding.MessageLiteral;
import io.vertx.protobuf.tests.core.support.embedding.ProtoReader;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmbeddingTest {

  @Test
  public void testEmbedding() throws Exception {
    EmbeddingProto.Container expected = EmbeddingProto.Container.newBuilder().setEmbedded(EmbeddingProto.Embedded.newBuilder().setValue(4).build()).build();
    String json = JsonFormat.printer().print(expected);
    ProtoReader visitor = new ProtoReader();
    ProtoJsonReader.parse(json, MessageLiteral.Container, visitor);
    Container container = (Container) visitor.stack.pop();
    assertNotNull(container.getEmbedded());
    assertEquals(4, (int)container.getEmbedded().getValue());
  }

  @Test
  public void testRepeating() throws Exception {
    EmbeddingProto.Container expected = EmbeddingProto.Container.newBuilder()
      .addRepeated(EmbeddingProto.Embedded.newBuilder().setValue(4).build())
      .addRepeated(EmbeddingProto.Embedded.newBuilder().setValue(6).build())
      .build();
    String json = JsonFormat.printer().print(expected);
    ProtoReader visitor = new ProtoReader();
    ProtoJsonReader.parse(json, MessageLiteral.Container, visitor);
    Container container = (Container) visitor.stack.pop();
    assertNotNull(container.getRepeated());
    assertEquals(2, container.getRepeated().size());
  }
}
