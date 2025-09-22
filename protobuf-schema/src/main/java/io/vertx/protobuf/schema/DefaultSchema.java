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
package io.vertx.protobuf.schema;

import java.util.HashMap;
import java.util.Map;

public class DefaultSchema implements Schema {

  private final Map<String, DefaultMessageType> messages = new HashMap<>();

  public DefaultMessageType of(String messageName) {
    return messages.computeIfAbsent(messageName, DefaultMessageType::new);
  }

  public DefaultMessageType peek(String messageName) {
    return messages.get(messageName);
  }
}
