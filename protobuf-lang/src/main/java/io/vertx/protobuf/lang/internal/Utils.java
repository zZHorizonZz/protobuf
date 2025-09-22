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
package io.vertx.protobuf.lang.internal;

public class Utils {
  public static String lowerCamelToSnake(String s) {
    StringBuilder sb = new StringBuilder();
    char prev = 'a'; // Whatever
    for (int i = 0;i < s.length();i++) {
      char c = s.charAt(i);
      if (c >= 'A' && c <= 'Z') {
        if (i > 0 && prev != '_') {
          sb.append('_');
        }
        c += 'a' - 'A';
      } /*else if (i == 0 && c >= 'a' && c <= 'z') {

      }*/
      sb.append(c);
      prev = c;
    }
    return sb.toString();
  }

  public static String snakeToLowerCamel(String fieldName) {
    StringBuilder sb = new StringBuilder(fieldName.length());
    char prev = ' ';
    for (int i = 0;i < fieldName.length();i++) {
      char ch = fieldName.charAt(i);
      boolean upperCase = ch >= 'a' && ch <= 'z' && (prev == '_');
      prev = ch;
      if (ch != '_') {
        ch = (char)(upperCase ? ch + ('A' - 'a') : ch);
        sb.append(ch);
      }
    }
    return sb.toString();
  }
}
