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
package io.vertx.protobuf.plugin;

public class GenWriter {

  private StringBuilder content = new StringBuilder();
  private int margin = 0;
  private int cols = 0;

  public GenWriter println() {
    content.append("\r\n");
    cols = 0;
    return this;
  }

  public GenWriter margin(int val) {
    if (val < 0) {
      throw new IllegalArgumentException();
    }
    margin = val;
    return this;
  }

  public GenWriter println(String s) {
    print(s);
    println();
    return this;
  }

  public GenWriter println(String... m) {
    for (String s : m) {
      println(s);
    }
    return this;
  }

  public GenWriter print(String s) {
    if (cols == 0) {
      content.append(" ".repeat(margin));
    }
    content.append(s);
    cols += s.length();
    return this;
  }

  @Override
  public String toString() {
    return content.toString();
  }
}
