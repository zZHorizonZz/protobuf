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
package io.vertx.protobuf.tests.lang;

import io.vertx.protobuf.lang.internal.Utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

  @Test
  public void testLowerCamelToSnake() {
    assertEquals("bar_baz", Utils.lowerCamelToSnake("BarBaz"));
    assertEquals("_bar_baz", Utils.lowerCamelToSnake("_BarBaz"));
    assertEquals("bar_baz", Utils.lowerCamelToSnake("barBaz"));
    assertEquals("bar_1234", Utils.lowerCamelToSnake("Bar_1234"));
    assertEquals("bar_", Utils.lowerCamelToSnake("Bar_"));
    assertEquals("bar_baz", Utils.lowerCamelToSnake("Bar_Baz"));
    assertEquals("bar__baz", Utils.lowerCamelToSnake("Bar__Baz"));
    assertEquals("b_b_b", Utils.lowerCamelToSnake("BBB"));
    assertEquals("a_ba_ba_b", Utils.lowerCamelToSnake("aBaBaB"));
    assertEquals("", Utils.lowerCamelToSnake(""));
  }

  @Test
  public void testSnakeToLowerCamel() {
    assertEquals("barBaz", Utils.snakeToLowerCamel("bar_baz"));
    assertEquals("BarBaz", Utils.snakeToLowerCamel("_bar_baz"));
    assertEquals("bar12Baz", Utils.snakeToLowerCamel("bar12_baz"));
    assertEquals("barBaz", Utils.snakeToLowerCamel("bar_Baz"));
    assertEquals("Bar", Utils.snakeToLowerCamel("__bar"));
    assertEquals("123", Utils.snakeToLowerCamel("_123"));
    assertEquals("bBB", Utils.snakeToLowerCamel("b_b_b"));
  }
}
