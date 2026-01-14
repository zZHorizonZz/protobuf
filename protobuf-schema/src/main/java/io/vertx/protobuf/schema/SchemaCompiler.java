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

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Compile {@link com.google.protobuf.Descriptors.FileDescriptor} to a {@link Schema}
 */
public class SchemaCompiler {

  private Map<Descriptors.Descriptor, DefaultMessageType> typeMap = new LinkedHashMap();
  private Map<Descriptors.EnumDescriptor, DefaultEnumType> enumMap = new LinkedHashMap<>();

  public List<DefaultMessageType> compile(Descriptors.FileDescriptor file) {
    List<DefaultMessageType> list = new ArrayList<>();
    for (Descriptors.Descriptor messageDesc : file.getMessageTypes()) {
      list.add(compile(messageDesc));
    }
    return list;
  }

  public DefaultEnumType compile(Descriptors.EnumDescriptor enumDesc) {
    DefaultEnumType enumType = enumMap.get(enumDesc);
    if (enumType == null) {
      enumType = new DefaultEnumType(enumDesc.getName());
      enumMap.put(enumDesc, enumType);
      for (Descriptors.EnumValueDescriptor enumValueDesc : enumDesc.getValues()) {
        enumType.addValue(enumValueDesc.getNumber(), enumValueDesc.getName());
      }
      
      // Determine the enum behavior from the features
      EnumBehavior behavior = determineEnumBehavior(enumDesc);
      enumType.setBehavior(behavior);
    }
    return enumType;
  }

  /**
   * Determines the enum behavior based on protobuf features.
   * 
   * <p>The behavior is determined by checking (in order):
   * <ol>
   *   <li>Enum-level features.enum_type option</li>
   *   <li>File-level features.enum_type option</li>
   *   <li>Default for the file's syntax/edition</li>
   * </ol>
   */
  private EnumBehavior determineEnumBehavior(Descriptors.EnumDescriptor enumDesc) {
    // Check enum-level feature
    DescriptorProtos.EnumOptions enumOptions = enumDesc.toProto().getOptions();
    if (enumOptions.hasFeatures() && enumOptions.getFeatures().hasEnumType()) {
      return convertEnumType(enumOptions.getFeatures().getEnumType());
    }
    
    // Check file-level feature
    Descriptors.FileDescriptor file = enumDesc.getFile();
    DescriptorProtos.FileOptions fileOptions = file.toProto().getOptions();
    if (fileOptions.hasFeatures() && fileOptions.getFeatures().hasEnumType()) {
      return convertEnumType(fileOptions.getFeatures().getEnumType());
    }
    
    // Use default based on syntax
    Syntax syntax = Syntax.fromFileDescriptor(file);
    return EnumBehavior.defaultFor(syntax);
  }

  private EnumBehavior convertEnumType(DescriptorProtos.FeatureSet.EnumType enumType) {
    switch (enumType) {
      case CLOSED:
        return EnumBehavior.CLOSED;
      case OPEN:
      default:
        return EnumBehavior.OPEN;
    }
  }

  public DefaultField compile(Descriptors.FieldDescriptor fieldDesc) {
    DefaultMessageType type = compile(fieldDesc.getContainingType());
    return type.field(fieldDesc.getNumber());
  }

  public DefaultMessageType compile(Descriptors.Descriptor typeDesc) {
    DefaultMessageType messageType = typeMap.get(typeDesc);
    if (messageType == null) {
      messageType = new DefaultMessageType(typeDesc.getName());
      typeMap.put(typeDesc, messageType);
      for (Descriptors.FieldDescriptor field : typeDesc.getFields()) {
        Type type;
        switch (field.getType()) {
          case INT32:
            type = ScalarType.INT32;
            break;
          case STRING:
            type = ScalarType.STRING;
            break;
          case ENUM:
            type = compile(field.getEnumType());
            break;
          case DOUBLE:
            type = ScalarType.DOUBLE;
            break;
          case BOOL:
            type = ScalarType.BOOL;
            break;
          case MESSAGE:
            type = compile(field.getMessageType());
            break;
          default:
            throw new UnsupportedOperationException("" + field.getType());
        }
        boolean isMapEntry = field.getContainingType().toProto().getOptions().getMapEntry();
        messageType.addField(builder -> {
          builder.type(type);
          builder.map(field.isMapField());
          builder.name(field.getName());
          builder.repeated(field.isRepeated());
          builder.optional(field.hasPresence());
          builder.mapKey(isMapEntry && field.getContainingType().getFields().get(0) == field);
          builder.mapValue(isMapEntry && field.getContainingType().getFields().get(1) == field);
          builder.number(field.getNumber());
          builder.packed(field.isPacked());
        });
      }
    }
    return messageType;
  }
}
