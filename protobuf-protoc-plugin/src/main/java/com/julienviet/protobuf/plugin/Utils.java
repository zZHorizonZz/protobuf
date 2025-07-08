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
package com.julienviet.protobuf.plugin;

import com.google.common.base.Strings;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import com.julienviet.protobuf.extension.ExtensionProto;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

  static List<Descriptors.FieldDescriptor> actualFields(Descriptors.Descriptor descriptor) {
    return descriptor.getFields().stream().filter(field -> field.getRealContainingOneof() == null).collect(Collectors.toList());
  }

  public static boolean isStruct(Descriptors.FieldDescriptor desc) {
    return desc.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && isStruct(desc.getMessageType());
  }

  public static boolean isDuration(Descriptors.FieldDescriptor desc) {
    return desc.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && isDuration(desc.getMessageType());
  }

  public static boolean isTimestamp(Descriptors.FieldDescriptor desc) {
    return desc.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && isTimestamp(desc.getMessageType());
  }

  public static boolean isBuffer(Descriptors.FieldDescriptor desc) {
    return desc.getType() == Descriptors.FieldDescriptor.Type.BYTES;
  }

  public static boolean isStruct(Descriptors.Descriptor desc) {
    return desc.getFullName().equals("google.protobuf.Struct");
  }

  public static boolean isDuration(Descriptors.Descriptor desc) {
    return desc.getFullName().equals("google.protobuf.Duration");
  }

  public static boolean isTimestamp(Descriptors.Descriptor desc) {
    return desc.getFullName().equals("google.protobuf.Timestamp");
  }

  public static boolean useBuffer(Descriptors.FieldDescriptor fd) {
    return fd.getOptions().getExtension(ExtensionProto.typeInterop);
  }

  public static boolean useJsonObject(Descriptors.FieldDescriptor fd) {
    return fd.getOptions().getExtension(ExtensionProto.typeInterop);
  }

  public static boolean useDuration(Descriptors.FieldDescriptor fd) {
    return fd.getOptions().getExtension(ExtensionProto.typeInterop);
  }

  public static boolean useTimestamp(Descriptors.FieldDescriptor fd) {
    return fd.getOptions().getExtension(ExtensionProto.typeInterop);
  }

  public static String setterOf(Descriptors.FieldDescriptor field) {
    String setter = field.getOptions().getExtension(ExtensionProto.writerMethod);
    if (setter.isEmpty()) {
      setter = "set" + Character.toUpperCase(field.getJsonName().charAt(0)) + field.getJsonName().substring(1);
    }
    return setter;
  }

  public static String getterOf(Descriptors.FieldDescriptor field) {
    String getter = field.getOptions().getExtension(ExtensionProto.readerMethod);
    if (getter.isEmpty()) {
      getter = "get" + Character.toUpperCase(field.getJsonName().charAt(0)) + field.getJsonName().substring(1);
    }
    return getter;
  }

  public static String setterOf(Descriptors.OneofDescriptor oneOf) {
    return "set" + nameOf(oneOf);
  }

  public static String getterOf(Descriptors.OneofDescriptor oneOf) {
    return "get" + nameOf(oneOf);
  }

  public static String literalIdentifier(Descriptors.FieldDescriptor field) {
    return literalIdentifier(field.getContainingType()) + "_" + field.getName();
  }

  public static String literalIdentifier(Descriptors.EnumDescriptor enumDesc) {
    if (enumDesc.getContainingType() != null) {
      return literalIdentifier(enumDesc.getContainingType()) + "_" + enumDesc.getName();
    } else {
      return enumDesc.getName();
    }
  }

  public static String literalIdentifier(Descriptors.Descriptor type) {
    if (type.getContainingType() != null) {
      return literalIdentifier(type.getContainingType()) + "_" + type.getName();
    } else {
      return type.getName();
    }
  }

  public static boolean isMapKey(Descriptors.FieldDescriptor field) {
    return field.getContainingType().toProto().getOptions().getMapEntry() && field.getContainingType().getFields().get(0) == field;
  }

  public static boolean isMapValue(Descriptors.FieldDescriptor field) {
    return field.getContainingType().toProto().getOptions().getMapEntry() && field.getContainingType().getFields().get(1) == field;
  }

  static Map<String, Descriptors.Descriptor> transitiveClosure(List<Descriptors.Descriptor> descriptors) {
    Map<String, Descriptors.Descriptor> all = new LinkedHashMap<>();
    descriptors.forEach(messageType -> {
      transitiveClosure(messageType, all);
    });
    return all;
  }

  private static void transitiveClosure(Descriptors.Descriptor descriptor, Map<String, Descriptors.Descriptor> result) {
    result.put(descriptor.getFullName(), descriptor);
    descriptor.getNestedTypes().forEach(nested -> transitiveClosure(nested, result));
  }

  public static String extractJavaPkgFqn(Descriptors.FileDescriptor proto) {
    return extractJavaPkgFqn(proto.toProto());
  }

  static String extractJavaPkgFqn(DescriptorProtos.FileDescriptorProto proto) {
    String googlePrefix = "com.google.protobuf";
    if (proto.getOptions().getJavaPackage().startsWith(googlePrefix) && (proto.getPackage().equals("google.protobuf") || proto.getPackage().equals("pb"))) {
      return "com.julienviet.protobuf.well_known_types" + proto.getOptions().getJavaPackage().substring(googlePrefix.length());
    }
    DescriptorProtos.FileOptions options = proto.getOptions();
    String javaPackage = options.getJavaPackage();
    if (!Strings.isNullOrEmpty(javaPackage)) {
      return javaPackage;
    }
    return Strings.nullToEmpty(proto.getPackage());
  }

  public static String nameOf(Descriptors.FieldDescriptor descriptor) {
    String name = descriptor.getJsonName();
    switch (name) {
      case "package":
        name = "package_";
        break;
    }
    return name;
  }

  static String nameOf(Descriptors.OneofDescriptor descriptor) {
    String name = descriptor.getName();
    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  public static String oneOfTypeName(Descriptors.FieldDescriptor descriptor) {
    String name = descriptor.getJsonName();
    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  public static String javaTypeOf(Descriptors.OneofDescriptor mt) {
    return javaTypeOf(mt.getContainingType()) + "." + nameOf(mt);
  }

  public static String javaTypeOf(Descriptors.Descriptor mt) {
    String pkg = extractJavaPkgFqn(mt.getFile());
    return pkg + "." + simpleNameOf(mt);
  }

  public static Descriptors.EnumValueDescriptor defaultEnumValue(Descriptors.EnumDescriptor desc) {
    return desc.getValues().stream().filter(vd -> vd.getIndex() == 0).findAny().get();
  }

  public static String javaTypeOf(Descriptors.FieldDescriptor field) {
    return javaTypeOf(field, true);
  }

  public static String javaTypeOf(Descriptors.FieldDescriptor field, boolean boxed) {
    if (field.isMapField()) {
      String keyType = javaTypeOf(field.getMessageType().getFields().get(0), true);
      String valueType = javaTypeOf(field.getMessageType().getFields().get(1), true);
      return "java.util.Map<" + keyType + ", " + valueType + ">";
    } else {
      if (field.isRepeated()) {
        String componentType = javaTypeOfInternal(field, true);
        if (componentType != null) {
          return "java.util.List<" + componentType + ">";
        } else {
          return null;
        }
      } else {
        return javaTypeOfInternal(field, boxed);
      }
    }
  }

  public static String javaTypeOfInternal(Descriptors.FieldDescriptor field) {
    return javaTypeOfInternal(field, true);
  }

  public static String javaTypeOfInternal(Descriptors.FieldDescriptor field, boolean boxed) {
    String pkg;
    switch (field.getType()) {
      case BYTES:
        return useBuffer(field) ? "io.vertx.core.buffer.Buffer" : "byte[]";
      case BOOL:
        return boxed ? "java.lang.Boolean" : "boolean";
      case STRING:
        return "java.lang.String";
      case FLOAT:
        return boxed ? "java.lang.Float" : "float";
      case DOUBLE:
        return boxed ? "java.lang.Double" : "double";
      case INT32:
      case UINT32:
      case SINT32:
      case FIXED32:
      case SFIXED32:
        return boxed ? "java.lang.Integer" : "int";
      case INT64:
      case UINT64:
      case SINT64:
      case FIXED64:
      case SFIXED64:
        return boxed ? "java.lang.Long" : "long";
      case ENUM:
        pkg = extractJavaPkgFqn(field.getEnumType().getFile());
        return pkg + "." + simpleNameOf(field.getEnumType());
      case MESSAGE:
        Descriptors.Descriptor messageType = field.getMessageType();
        if (isStruct(messageType) && useJsonObject(field)) {
          return "io.vertx.core.json.JsonObject";
        } else if (isDuration(messageType) && useDuration(field)) {
          return Duration.class.getName();
        } else if (isTimestamp(messageType) && useTimestamp(field)) {
          return OffsetDateTime.class.getName();
        }
        pkg = extractJavaPkgFqn(messageType.getFile());
        return pkg + "." + simpleNameOf(messageType);
      default:
        return null;
    }
  }

  public static boolean isPrimitive(Descriptors.FieldDescriptor field) {
    switch (field.getType()) {
      case BOOL:
      case INT32:
      case SINT32:
      case UINT32:
      case INT64:
      case SINT64:
      case UINT64:
      case FIXED32:
      case SFIXED32:
      case FIXED64:
      case SFIXED64:
      case FLOAT:
      case DOUBLE:
        return true;
      default:
        return false;
    }
  }

  private static String simpleNameOf(Descriptors.Descriptor type) {
    Descriptors.Descriptor containing = type.getContainingType();
    return containing == null ? type.getName() : (simpleNameOf(containing) + "." + type.getName());
  }

  private static String simpleNameOf(Descriptors.EnumDescriptor descriptor) {
    Descriptors.Descriptor containing = descriptor.getContainingType();
    return containing == null ? descriptor.getName() : (simpleNameOf(containing) + "." + descriptor.getName());
  }

  static String absoluteJavaFileName(String javaPkgFqn, Descriptors.GenericDescriptor messageType) {
    return absoluteJavaFileName(javaPkgFqn, messageType.getName());
  }

  public static String absoluteJavaFileName(String javaPkgFqn, String simpleName) {
    return absoluteFileName(javaPkgFqn, simpleName + ".java");
  }

  public static String absoluteFileName(String javaPkgFqn, String simpleName) {
    String dir = javaPkgFqn.replace('.', '/');
    if (dir.isEmpty()) {
      return simpleName;
    } else {
      return dir + "/" + simpleName;
    }
  }

  static PluginProtos.CodeGeneratorResponse.File buildFile(String javaPkgFqn, Descriptors.GenericDescriptor messageType, String content) {
    return PluginProtos.CodeGeneratorResponse.File
      .newBuilder()
      .setName(absoluteJavaFileName(javaPkgFqn, messageType))
      .setContent(content)
      .build();
  }
}
