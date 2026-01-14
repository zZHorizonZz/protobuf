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
package io.vertx.protobuf.codegen;

import com.google.protobuf.DescriptorProtos;

/**
 * Represents protobuf syntax versions and editions.
 * 
 * <p>Protobuf supports two main syntax versions (proto2 and proto3) and
 * the newer editions system starting with Edition 2023.</p>
 */
public enum Syntax {
  
  /**
   * Proto2 syntax - the original protobuf syntax with required fields and explicit optional.
   */
  PROTO2("proto2", null),
  
  /**
   * Proto3 syntax - simplified syntax with implicit presence for scalar fields.
   */
  PROTO3("proto3", null),
  
  /**
   * Edition 2023 - the first edition-based protobuf version.
   * This provides feature-based customization of protobuf behavior.
   */
  EDITION_2023("editions", DescriptorProtos.Edition.EDITION_2023),
  
  /**
   * Edition 2024 - allows further customization of protobuf behavior.
   */
  EDITION_2024("editions", DescriptorProtos.Edition.EDITION_2024);

  private final String syntaxValue;
  private final DescriptorProtos.Edition edition;

  Syntax(String syntaxValue, DescriptorProtos.Edition edition) {
    this.syntaxValue = syntaxValue;
    this.edition = edition;
  }

  /**
   * Gets the syntax value string for FileDescriptorProto.
   * For editions, this returns "editions".
   * For proto2/proto3, this returns "proto2" or "proto3".
   */
  public String getSyntaxValue() {
    return syntaxValue;
  }

  /**
   * Gets the edition if this is an edition-based syntax.
   * Returns null for proto2 and proto3.
   */
  public DescriptorProtos.Edition getEdition() {
    return edition;
  }

  /**
   * Returns true if this syntax uses the editions system.
   */
  public boolean isEdition() {
    return edition != null;
  }

  /**
   * Applies this syntax to a FileDescriptorProto builder.
   */
  public void applyTo(DescriptorProtos.FileDescriptorProto.Builder builder) {
    builder.setSyntax(syntaxValue);
    if (edition != null) {
      builder.setEdition(edition);
    }
  }

  /**
   * Returns the default syntax (proto3 for backward compatibility).
   */
  public static Syntax getDefault() {
    return PROTO3;
  }

  /**
   * Parses a syntax from a FileDescriptorProto.
   */
  public static Syntax fromFileDescriptor(DescriptorProtos.FileDescriptorProto proto) {
    String syntax = proto.getSyntax();
    if ("editions".equals(syntax)) {
      DescriptorProtos.Edition edition = proto.getEdition();
      for (Syntax s : values()) {
        if (s.edition == edition) {
          return s;
        }
      }
      // Default to latest known edition if unknown
      return EDITION_2024;
    } else if ("proto2".equals(syntax)) {
      return PROTO2;
    } else {
      // Default to proto3 if unspecified or "proto3"
      return PROTO3;
    }
  }
}
