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
import io.vertx.protobuf.tests.core.support.enumeration.EnumLiteral;
import io.vertx.protobuf.tests.core.support.enumeration.Container;
import io.vertx.protobuf.tests.core.support.enumeration.EnumWithNegativeValue;
import io.vertx.protobuf.tests.core.support.enumeration.MessageLiteral;
import io.vertx.protobuf.tests.core.support.enumeration.ProtoReader;
import io.vertx.protobuf.tests.core.support.enumeration.ProtoWriter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumTest {

  @Test
  public void testAliases() {
    assertEquals(0, EnumLiteral.EnumWithAliases.numberOf("ZERO").getAsInt());
    assertEquals(1, EnumLiteral.EnumWithAliases.numberOf("ONE").getAsInt());
    assertEquals(2, EnumLiteral.EnumWithAliases.numberOf("TWO").getAsInt());
    assertEquals(1, EnumLiteral.EnumWithAliases.numberOf("UNO").getAsInt());
    assertEquals(2, EnumLiteral.EnumWithAliases.numberOf("DOS").getAsInt());
  }

  @Test
  public void testEnumValueAsVarInt64() {
    Container container = new Container();
    container.setEnumVal(EnumWithNegativeValue.MINUS_ONE);
    byte[] bytes = ProtobufWriter.encodeToByteArray(ProtoWriter.streamOf(container));
    assertEquals(11, bytes.length);
    container = ProtoReader.readContainer(ProtobufReader.readerStream(MessageLiteral.Container, bytes));
    assertEquals(EnumWithNegativeValue.MINUS_ONE, container.getEnumVal());
  }
}
