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
package com.julienviet.protobuf.tests.core;

import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.core.ProtobufWriter;
import com.julienviet.protobuf.tests.core.support.oneof.FieldLiteral;
import com.julienviet.protobuf.tests.core.support.oneof.ProtoWriter;
import com.julienviet.protobuf.tests.core.support.oneof.ProtoReader;
import com.julienviet.protobuf.tests.core.support.oneof.MessageLiteral;
import com.julienviet.protobuf.tests.core.support.oneof.AppleMsg;
import com.julienviet.protobuf.tests.core.support.oneof.BananaMsg;
import com.julienviet.protobuf.tests.core.support.oneof.Container;
import com.julienviet.protobuf.tests.core.support.oneof.OneOfProto;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class OneOfTest {

  @Test
  public void testOneOf() throws Exception {
    byte[] bytes = OneOfProto.Container.newBuilder().setBanana(OneOfProto.BananaMsg.newBuilder().setWeight(15).build()).build().toByteArray();
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Container, reader, bytes);
    Container msg = (Container) reader.stack.pop();
    assertNotNull(msg.getFruit());
    assertEquals(Container.FruitDiscriminant.BANANA, msg.getFruit().discriminant());
    assertEquals(15, (int)msg.getFruit().asBanana().get().getWeight());
    bytes = ProtobufWriter.encodeToByteArray(visitor -> ProtoWriter.emit(msg, visitor));
    OneOfProto.Container c2 = OneOfProto.Container.parseFrom(bytes);
    assertEquals(15, c2.getBanana().getWeight());
  }

  @Test
  public void testAPI() throws Exception {
    BananaMsg bananaMsg = new BananaMsg();
    Container.Fruit<BananaMsg> v = Container.Fruit.ofBanana(bananaMsg);
    assertSame(bananaMsg, v.get());
    Optional<AppleMsg> apple = v.asApple();
    assertFalse(apple.isPresent());
    assertTrue(v.asBanana().isPresent());
    assertEquals(Container.FruitDiscriminant.BANANA, v.discriminant());
  }

  @Test
  public void testDuplicate() {
    RecordingVisitor visitor = new RecordingVisitor();
    visitor.init(MessageLiteral.Container);
    visitor.visitString(FieldLiteral.Container_string, "str");
    visitor.visitInt32(FieldLiteral.Container_integer, 4);
    visitor.destroy();
    byte[] encoded = ProtobufWriter.encodeToByteArray(visitor::apply);
    ProtoReader reader = new ProtoReader();
    ProtobufReader.parse(MessageLiteral.Container, reader, encoded);
    Container msg = (Container) reader.stack.pop();
    assertEquals(Container.ScalarDiscriminant.INTEGER, msg.getScalar().discriminant());
  }
}
