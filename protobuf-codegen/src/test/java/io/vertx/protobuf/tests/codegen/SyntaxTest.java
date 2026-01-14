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
package io.vertx.protobuf.tests.codegen;

import com.google.protobuf.DescriptorProtos;
import io.vertx.protobuf.codegen.Syntax;
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
  public void testApplyToFileDescriptor() {
    // Test applying proto3 syntax
    DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.PROTO3.applyTo(builder);
    assertEquals("proto3", builder.getSyntax());
    assertFalse(builder.hasEdition());

    // Test applying proto2 syntax
    builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.PROTO2.applyTo(builder);
    assertEquals("proto2", builder.getSyntax());
    assertFalse(builder.hasEdition());

    // Test applying edition 2023
    builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.EDITION_2023.applyTo(builder);
    assertEquals("editions", builder.getSyntax());
    assertEquals(DescriptorProtos.Edition.EDITION_2023, builder.getEdition());

    // Test applying edition 2024
    builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    Syntax.EDITION_2024.applyTo(builder);
    assertEquals("editions", builder.getSyntax());
    assertEquals(DescriptorProtos.Edition.EDITION_2024, builder.getEdition());
  }

  @Test
  public void testFromFileDescriptor() {
    // Test parsing proto2
    DescriptorProtos.FileDescriptorProto proto2 = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("proto2")
        .build();
    assertEquals(Syntax.PROTO2, Syntax.fromFileDescriptor(proto2));

    // Test parsing proto3
    DescriptorProtos.FileDescriptorProto proto3 = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("proto3")
        .build();
    assertEquals(Syntax.PROTO3, Syntax.fromFileDescriptor(proto3));

    // Test parsing edition 2023
    DescriptorProtos.FileDescriptorProto edition2023 = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .build();
    assertEquals(Syntax.EDITION_2023, Syntax.fromFileDescriptor(edition2023));

    // Test parsing edition 2024
    DescriptorProtos.FileDescriptorProto edition2024 = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2024)
        .build();
    assertEquals(Syntax.EDITION_2024, Syntax.fromFileDescriptor(edition2024));

    // Test default (empty syntax)
    DescriptorProtos.FileDescriptorProto empty = DescriptorProtos.FileDescriptorProto.newBuilder()
        .build();
    assertEquals(Syntax.PROTO3, Syntax.fromFileDescriptor(empty));
  }
}
