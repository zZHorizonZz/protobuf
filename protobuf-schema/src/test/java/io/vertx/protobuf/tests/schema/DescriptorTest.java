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
import io.vertx.protobuf.schema.DefaultMessageType;
import io.vertx.protobuf.schema.DefaultSchema;
import io.vertx.protobuf.schema.ScalarType;
import org.junit.Test;

public class DescriptorTest {

  @Test
  public void testSome() {

    DefaultSchema schema = new DefaultSchema();

    DefaultMessageType field = schema.of("entry");
    DefaultMessageType struct = schema.of("Struct");
    struct.addField(1, field);

    DefaultMessageType list = schema.of("ListValue");

    DefaultMessageType value = schema.of("Value");
    value.addField(1, new DefaultEnumType());
    value.addField(2, ScalarType.DOUBLE);
    value.addField(3, ScalarType.STRING);
    value.addField(4, ScalarType.BOOL);
    value.addField(5, struct);
    value.addField(6, list);

    list.addField(1, value);

    field.addField(1, ScalarType.STRING);
    field.addField(2, value);

  }

}
