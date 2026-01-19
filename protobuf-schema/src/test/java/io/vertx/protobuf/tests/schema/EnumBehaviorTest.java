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
package io.vertx.protobuf.tests.schema;

import io.vertx.protobuf.schema.DefaultEnumType;
import io.vertx.protobuf.schema.EnumBehavior;
import io.vertx.protobuf.schema.Syntax;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnumBehaviorTest {

  @Test
  public void testDefaultBehaviorForProto2() {
    assertEquals(EnumBehavior.CLOSED, EnumBehavior.defaultFor(Syntax.PROTO2));
  }

  @Test
  public void testDefaultBehaviorForProto3() {
    assertEquals(EnumBehavior.OPEN, EnumBehavior.defaultFor(Syntax.PROTO3));
  }

  @Test
  public void testDefaultBehaviorForEdition2023() {
    assertEquals(EnumBehavior.OPEN, EnumBehavior.defaultFor(Syntax.EDITION_2023));
  }

  @Test
  public void testDefaultBehaviorForEdition2024() {
    assertEquals(EnumBehavior.OPEN, EnumBehavior.defaultFor(Syntax.EDITION_2024));
  }

  @Test
  public void testDefaultEnumTypeDefaultBehavior() {
    DefaultEnumType enumType = new DefaultEnumType("TestEnum");
    assertEquals(EnumBehavior.OPEN, enumType.behavior());
  }

  @Test
  public void testDefaultEnumTypeSetBehaviorOpen() {
    DefaultEnumType enumType = new DefaultEnumType("TestEnum");
    enumType.setBehavior(EnumBehavior.OPEN);
    assertEquals(EnumBehavior.OPEN, enumType.behavior());
  }

  @Test
  public void testDefaultEnumTypeSetBehaviorClosed() {
    DefaultEnumType enumType = new DefaultEnumType("TestEnum");
    enumType.setBehavior(EnumBehavior.CLOSED);
    assertEquals(EnumBehavior.CLOSED, enumType.behavior());
  }

  @Test
  public void testDefaultEnumTypeFluentSetBehavior() {
    DefaultEnumType enumType = new DefaultEnumType("TestEnum")
        .addValue(0, "UNKNOWN")
        .addValue(1, "VALUE_ONE")
        .setBehavior(EnumBehavior.CLOSED);
    
    assertEquals(EnumBehavior.CLOSED, enumType.behavior());
    assertEquals("UNKNOWN", enumType.nameOf(0));
    assertEquals("VALUE_ONE", enumType.nameOf(1));
  }
}
