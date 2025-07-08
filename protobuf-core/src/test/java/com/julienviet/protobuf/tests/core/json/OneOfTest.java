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

import io.vertx.core.json.JsonObject;
import com.julienviet.protobuf.core.json.ProtoJsonReader;
import com.julienviet.protobuf.tests.core.support.oneof.Container;
import com.julienviet.protobuf.tests.core.support.oneof.FieldLiteral;
import com.julienviet.protobuf.tests.core.support.oneof.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.oneof.ProtoReader;
import org.junit.Test;

public class OneOfTest {

  /**
   * For now duplicate field are accepted which contradicts the behavior of ProtoJSON, note that Protobuf
   * accepts this behavior.
   */
  @Test
  public void testDuplicate() {

    ProtoReader reader = new ProtoReader();
    JsonObject json = new JsonObject()
      .put(FieldLiteral.Container_integer.jsonName(), 1)
      .put(FieldLiteral.Container_string.jsonName(), "str");
    ProtoJsonReader.parse(json.encode(), MessageLiteral.Container, reader);
    Container msg = (Container) reader.stack.pop();
  }
}
