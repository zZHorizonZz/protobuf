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
package io.vertx.protobuf.it;

import io.vertx.protobuf.lang.MessageBase;
import io.vertx.protobuf.lang.ProtoField;
import io.vertx.protobuf.lang.ProtoMessage;

@ProtoMessage
public class SimpleMessage extends MessageBase {

  private String stringField;
  private long longField;

  @ProtoField(number = 1, protoName = "string_field")
  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }

  @ProtoField(number = 2, protoName = "long_field")
  public long getLongField() {
    return longField;
  }

  public void setLongField(long l) {
    this.longField = l;
  }
}
