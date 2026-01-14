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

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Struct;
import io.vertx.protobuf.schema.Syntax;
import org.junit.Test;

import static org.junit.Assert.*;

public class SyntaxTest {

  @Test
  public void testProto2Syntax() {
    assertEquals("proto2", Syntax.PROTO2.getSyntaxValue());
    assertNull(Syntax.PROTO2.getEdition());
    assertFalse(Syntax.PROTO2.isEdition());
  }

  @Test
  public void testProto3Syntax() {
    assertEquals("proto3", Syntax.PROTO3.getSyntaxValue());
    assertNull(Syntax.PROTO3.getEdition());
    assertFalse(Syntax.PROTO3.isEdition());
  }

  @Test
  public void testEdition2023() {
    assertEquals("editions", Syntax.EDITION_2023.getSyntaxValue());
    assertEquals(DescriptorProtos.Edition.EDITION_2023, Syntax.EDITION_2023.getEdition());
    assertTrue(Syntax.EDITION_2023.isEdition());
  }

  @Test
  public void testEdition2024() {
    assertEquals("editions", Syntax.EDITION_2024.getSyntaxValue());
    assertEquals(DescriptorProtos.Edition.EDITION_2024, Syntax.EDITION_2024.getEdition());
    assertTrue(Syntax.EDITION_2024.isEdition());
  }

  @Test
  public void testDefaultSyntax() {
    assertEquals(Syntax.PROTO3, Syntax.getDefault());
  }

  @Test
  public void testFromFileDescriptor() {
    // Test with a known proto3 descriptor (Struct is defined with proto3)
    Syntax syntax = Syntax.fromFileDescriptor(Struct.getDescriptor().getFile());
    assertEquals(Syntax.PROTO3, syntax);
  }

  @Test
  public void testFromFileDescriptorProtoProto2() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("proto2")
        .build();
    assertEquals(Syntax.PROTO2, Syntax.fromFileDescriptorProto(proto));
  }

  @Test
  public void testFromFileDescriptorProtoProto3() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("proto3")
        .build();
    assertEquals(Syntax.PROTO3, Syntax.fromFileDescriptorProto(proto));
  }

  @Test
  public void testFromFileDescriptorProtoEdition2023() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .build();
    assertEquals(Syntax.EDITION_2023, Syntax.fromFileDescriptorProto(proto));
  }

  @Test
  public void testFromFileDescriptorProtoEdition2024() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2024)
        .build();
    assertEquals(Syntax.EDITION_2024, Syntax.fromFileDescriptorProto(proto));
  }

  @Test
  public void testFromFileDescriptorProtoDefaultSyntax() {
    // Empty syntax should default to proto3
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .build();
    assertEquals(Syntax.PROTO3, Syntax.fromFileDescriptorProto(proto));
  }

  @Test
  public void testApplyToProto2() {
    DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.PROTO2.applyTo(builder);
    assertEquals("proto2", builder.getSyntax());
    assertFalse(builder.hasEdition());
  }

  @Test
  public void testApplyToProto3() {
    DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.PROTO3.applyTo(builder);
    assertEquals("proto3", builder.getSyntax());
    assertFalse(builder.hasEdition());
  }

  @Test
  public void testApplyToEdition2023() {
    DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.EDITION_2023.applyTo(builder);
    assertEquals("editions", builder.getSyntax());
    assertEquals(DescriptorProtos.Edition.EDITION_2023, builder.getEdition());
  }

  @Test
  public void testApplyToEdition2024() {
    DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.EDITION_2024.applyTo(builder);
    assertEquals("editions", builder.getSyntax());
    assertEquals(DescriptorProtos.Edition.EDITION_2024, builder.getEdition());
  }
}
