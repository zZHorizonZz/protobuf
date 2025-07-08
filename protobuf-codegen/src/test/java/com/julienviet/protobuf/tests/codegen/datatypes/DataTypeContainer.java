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
package com.julienviet.protobuf.tests.codegen.datatypes;

import com.julienviet.protobuf.lang.ProtoField;
import com.julienviet.protobuf.lang.ProtoMessage;

@ProtoMessage
public class DataTypeContainer {

  private String stringField;
  private TestEnum enumField;

  @ProtoField(number = 1, protoName = "string_field")
  public String getStringField() {
    return stringField;
  }

  public void setStringField(String s) {
    this.stringField = s;
  }

  @ProtoField(number = 2, protoName = "long_field")
  public long getLongField() {
    return 0L;
  }

  public void setLongField(long s) {
    throw new UnsupportedOperationException();
  }

  @ProtoField(number = 3, protoName = "boolean_field")
  public boolean getBooleanField() {
    return false;
  }

  public void setBooleanField(boolean b) {
    throw new UnsupportedOperationException();
  }

  @ProtoField(number = 4, protoName = "enum_field")
  public TestEnum getEnumField() {
    return enumField;
  }

  public void setEnumField(TestEnum e) {
    this.enumField = e;
  }
}
