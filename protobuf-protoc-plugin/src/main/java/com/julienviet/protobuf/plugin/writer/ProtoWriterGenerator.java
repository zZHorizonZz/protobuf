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
package com.julienviet.protobuf.plugin.writer;

import com.google.protobuf.Descriptors;
import com.julienviet.protobuf.plugin.GenWriter;
import com.julienviet.protobuf.plugin.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ProtoWriterGenerator {

  static class Bilto {
    final String visitMethod;
    final Function<String, String> fn;
    final String javaType;
    final Descriptors.FieldDescriptor.Type type;
    final boolean lengthDelimited;
    Bilto(String visitMethod, Function<String, String> fn, Descriptors.FieldDescriptor.Type type, String javaType) {
      this.visitMethod = visitMethod;
      this.fn = fn;
      this.type = type;
      this.javaType = javaType;
      this.lengthDelimited = (type == Descriptors.FieldDescriptor.Type.BYTES) || (type == Descriptors.FieldDescriptor.Type.STRING) || (type == Descriptors.FieldDescriptor.Type.MESSAGE);
    }
    Bilto(String visitMethod, Descriptors.FieldDescriptor.Type type, String javaType) {
      this(visitMethod, Function.identity(), type, javaType);
    }
  }

  private static final Map<Descriptors.FieldDescriptor.Type, Bilto> TYPE_TO = new HashMap<>();

  private static final Bilto ENUM_TYPE_TO_1  = new Bilto("visitEnum", s -> s + ".ordinal()", Descriptors.FieldDescriptor.Type.ENUM, "java.lang.Integer");
  private static final Bilto ENUM_TYPE_TO_2  = new Bilto("visitEnum", s -> s + ".number()", Descriptors.FieldDescriptor.Type.ENUM, "java.lang.Integer");
  private static final Bilto BYTES_TYPE_TO_1 = new Bilto("visitBytes", s -> s + ".getBytes()", Descriptors.FieldDescriptor.Type.BYTES, "byte[]");
  private static final Bilto BYTES_TYPE_TO_2 = new Bilto("visitBytes", Function.identity(), Descriptors.FieldDescriptor.Type.BYTES, "byte[]");

  static {
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.FLOAT, new Bilto("visitFloat", Descriptors.FieldDescriptor.Type.FLOAT, "java.lang.Float"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.DOUBLE, new Bilto("visitDouble", Descriptors.FieldDescriptor.Type.DOUBLE, "java.lang.Double"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.STRING, new Bilto("visitString", Descriptors.FieldDescriptor.Type.STRING, "java.lang.String"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.BOOL, new Bilto("visitBool", Descriptors.FieldDescriptor.Type.BOOL, "java.lang.Boolean"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.INT32, new Bilto("visitInt32", Descriptors.FieldDescriptor.Type.INT32, "java.lang.Integer"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.UINT32, new Bilto("visitUInt32", Descriptors.FieldDescriptor.Type.UINT32, "java.lang.Integer"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.INT64, new Bilto("visitInt64", Descriptors.FieldDescriptor.Type.INT64, "java.lang.Long"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.UINT64, new Bilto("visitUInt64", Descriptors.FieldDescriptor.Type.UINT64, "java.lang.Long"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.SINT32, new Bilto("visitSInt32", Descriptors.FieldDescriptor.Type.SINT32, "java.lang.Integer"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.SINT64, new Bilto("visitSInt64", Descriptors.FieldDescriptor.Type.SINT64, "java.lang.Long"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.FIXED32, new Bilto("visitFixed32", Descriptors.FieldDescriptor.Type.FIXED32, "java.lang.Integer"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.FIXED64, new Bilto("visitFixed64", Descriptors.FieldDescriptor.Type.FIXED64, "java.lang.Long"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.SFIXED32, new Bilto("visitSFixed32", Descriptors.FieldDescriptor.Type.SFIXED32, "java.lang.Integer"));
    TYPE_TO.put(Descriptors.FieldDescriptor.Type.SFIXED64, new Bilto("visitSFixed64", Descriptors.FieldDescriptor.Type.SFIXED64, "java.lang.Long"));
  }

  private Bilto typeToOf(Descriptors.FieldDescriptor fd) {
    if (fd.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
      if (Utils.useBuffer(fd)) {
        return BYTES_TYPE_TO_1;
      } else {
        return BYTES_TYPE_TO_2;
      }
    } else if (fd.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
      if (useEnumType) {
        return ENUM_TYPE_TO_1;
      } else {
        return ENUM_TYPE_TO_2;
      }
    } else {
      return TYPE_TO.get(fd.getType());
    }
  }

  private final String javaPkgFqn;
  private final List<Descriptors.Descriptor> fileDesc;
  private final boolean useEnumType;
  private final boolean trackPresenceWithField;

  public ProtoWriterGenerator(String javaPkgFqn, boolean useEnumType, boolean trackPresence, List<Descriptors.Descriptor> fileDesc) {
    this.javaPkgFqn = javaPkgFqn;
    this.fileDesc = fileDesc;
    this.useEnumType = useEnumType;
    this.trackPresenceWithField = trackPresence;
  }

  static class Property {
    public String getterMethod;
    public String setterMethod;
    public String fieldName;
    public String javaType;
    public String javaTypeInternal;
    public Function<String, String> defaultValueChecker = s -> s + "." + this.getterMethod + "()" + " != null";
  }

  static class FieldProperty extends Property {
    public Bilto typeTo;
    public boolean map;
    public String identifier;
    public String keyJavaType;
    public Bilto keyTypeTo;
    public String keyIdentifier;
    public String valueJavaType;
    public Bilto valueTypeTo;
    public String valueIdentifier;
    public String protoWriterFqn;
    private boolean repeated;
    private boolean packed;

    // OneOf
    public String discriminant;
    public String typeName;
  }

  static class OneofProperty extends Property {
    final List<FieldProperty> fields = new ArrayList<>();
  }

  public String generate() {

    List<Descriptors.Descriptor> all = new ArrayList<>(fileDesc);

    GenWriter content = new GenWriter();

    content.println(
      "package " + javaPkgFqn + ";",
      "import com.julienviet.protobuf.core.ProtoVisitor;",
      "import com.julienviet.protobuf.schema.MessageType;",
      "import com.julienviet.protobuf.schema.Field;",
      "",
      "public class ProtoWriter {");

    for (Descriptors.Descriptor d : all) {
      if (!d.getOptions().getMapEntry()) {
        content.println(
          "  public static void emit(" + Utils.javaTypeOf(d) + " value, ProtoVisitor visitor) {",
          "    visitor.init(MessageLiteral." + Utils.literalIdentifier(d) + ");",
          "    visit(value, visitor);",
          "    visitor.destroy();",
          "  }");
        content.println(
          "  public static com.julienviet.protobuf.core.ProtoStream streamOf(" + Utils.javaTypeOf(d) + " value) {",
          "    return visitor -> {",
          "    emit(value, visitor);",
          "    };",
          "  }");
      }
    }

    for (Descriptors.Descriptor d : all) {

      if (d.getOptions().getMapEntry()) {
        continue;
      }

      Map<Descriptors.OneofDescriptor, OneofProperty> blah = new HashMap<>();
      Map<Descriptors.FieldDescriptor, OneofProperty> oneOfs__ = new HashMap<>();
      d.getRealOneofs().forEach(oneOf -> oneOf.getFields().forEach(f -> {
        oneOfs__.put(f, blah.computeIfAbsent(oneOf, k -> new OneofProperty()));
      }));

      List<Property> props = new ArrayList<>();
      for (Descriptors.FieldDescriptor fd : d.getFields()) {
        FieldProperty field = new FieldProperty();
        field.identifier = Utils.literalIdentifier(fd);
        field.typeTo = typeToOf(fd);
        field.javaType = Utils.javaTypeOf(fd);
        field.javaTypeInternal = Utils.javaTypeOfInternal(fd);
        field.getterMethod = Utils.getterOf(fd);
        field.setterMethod = Utils.setterOf(fd);
        field.fieldName = Utils.nameOf(fd);
        field.repeated = fd.isRepeated();
        field.packed = fd.isPacked();

        if (fd.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
          if (Utils.isStruct(fd.getMessageType()) && Utils.useJsonObject(fd) ||
              Utils.isDuration(fd.getMessageType()) && Utils.useDuration(fd) ||
              Utils.isTimestamp(fd.getMessageType()) && Utils.useTimestamp(fd)
          ) {
            field.protoWriterFqn = "com.julienviet.protobuf.core.interop.ProtoWriter";
          } else {
            field.protoWriterFqn = Utils.extractJavaPkgFqn(fd.getMessageType().getFile()) + ".ProtoWriter";
          }
        } else {
          field.protoWriterFqn = null;
        }

        if (fd.isMapField()) {
          field.map = true;
          field.keyJavaType = Utils.javaTypeOf(fd.getMessageType().getFields().get(0));
          field.keyTypeTo = typeToOf(fd.getMessageType().getFields().get(0));
          field.keyIdentifier = Utils.literalIdentifier(fd.getMessageType().getFields().get(0));
          field.valueJavaType = Utils.javaTypeOf(fd.getMessageType().getFields().get(1));
          field.valueTypeTo = typeToOf(fd.getMessageType().getFields().get(1));
          field.valueIdentifier = Utils.literalIdentifier(fd.getMessageType().getFields().get(1));
        } else {
          field.map = false;
          if (fd.isRepeated()) {
            field.defaultValueChecker = s -> "!" + s + "." + field.getterMethod + "().isEmpty()";
          } else {
            if (fd.hasPresence() && trackPresenceWithField) {
              field.defaultValueChecker = s -> s + "." + field.fieldName + " != null";
            } else {
              switch (fd.getType()) {
                case INT32:
                case UINT32:
                case SINT32:
                case FIXED32:
                case SFIXED32:
                  field.defaultValueChecker = s -> s + "." + field.getterMethod + "() != 0";
                  break;
                case INT64:
                case UINT64:
                case SINT64:
                case FIXED64:
                case SFIXED64:
                  field.defaultValueChecker = s -> s + "." + field.getterMethod + "() != 0L";
                  break;
                case FLOAT:
                  field.defaultValueChecker = s -> s + "." + field.getterMethod + "() != 0F";
                  break;
                case DOUBLE:
                  field.defaultValueChecker = s -> s + "." + field.getterMethod + "() != 0D";
                  break;
                case STRING:
                  field.defaultValueChecker = s -> "!" + s + "." + field.getterMethod + "().isEmpty()";
                  break;
                case BOOL:
                  field.defaultValueChecker = s -> s + "." + field.getterMethod + "()";
                  break;
                case ENUM:
                  field.defaultValueChecker = s -> s + "." + field.getterMethod + "() != " + Utils.javaTypeOf(fd) + "." + Utils.defaultEnumValue(fd.getEnumType()).getName();
                  break;
                case BYTES:
                  if (Utils.useBuffer(fd)) {
                    field.defaultValueChecker = s -> s + "." + field.getterMethod + "().length() != 0";
                  } else {
                    field.defaultValueChecker = s -> s + "." + field.getterMethod + "().length != 0";
                  }
                  break;
              }
            }
          }
        }

        OneofProperty oneOf = oneOfs__.get(fd);
        if (oneOf != null) {
          field.discriminant = fd.getName().toUpperCase();
          field.typeName = Utils.oneOfTypeName(fd);
          oneOf.fields.add((field));
        } else {
          props.add(field);
        }
      }
      blah.forEach((a, b) -> {
        b.getterMethod = Utils.getterOf(a);
        b.setterMethod = Utils.setterOf(a);
        b.javaType = "";
        b.javaTypeInternal = "";
        props.add(b);
      });

      content.println("  public static void visit(" + Utils.javaTypeOf(d) + " value, ProtoVisitor visitor) {");
      for (Property property : props) {
        content.println("    if (" + property.defaultValueChecker.apply("value") + ") {");
        if (property instanceof FieldProperty) {
          FieldProperty field = (FieldProperty) property;
          content.println("      " + field.javaType + " v = value." + field.getterMethod + "();");
          gen(content, field);
        } else {
          OneofProperty oneof = (OneofProperty)property;
          content.println("      switch (value." + property.getterMethod + "().discriminant()) {");
          oneof.fields.forEach(field -> {
            content.println("        case " + field.discriminant + ": {");
            content.println("          " + field.javaType + " v = value." + property.getterMethod + "().as" + field.typeName + "().get();");
            content.margin(4);
            gen(content, field);
            content.margin(0);
            content.println("          break;");
            content.println("        }");
          });
          content.println("        default:");
          content.println("          throw new AssertionError();");
          content.println("        }");
        }
        content.println("    }");
      }

      content.println(
        "    java.lang.Iterable<java.util.Map.Entry<com.julienviet.protobuf.schema.Field, java.util.List<Object>>> unknownFields = value instanceof com.julienviet.protobuf.lang.Message ? ((com.julienviet.protobuf.lang.Message)value).unknownFields() : null;",
        "    if (unknownFields != null) {",
        "      for (java.util.Map.Entry<com.julienviet.protobuf.schema.Field, java.util.List<Object>> unknownField : unknownFields) {",
        "        for (Object o : unknownField.getValue()) {",
        "          com.julienviet.protobuf.schema.Field field = unknownField.getKey();",
        "          switch (field.type().wireType()) {",
        "            case LEN:",
        "              visitor.visitBytes(field, (byte[])o);",
        "              break;",
        "            case I32:",
        "              visitor.visitFixed32(field, (Integer)o);",
        "              break;",
        "            case I64:",
        "              visitor.visitFixed64(field, (Long)o);",
        "              break;",
        "            case VARINT:",
        "              visitor.visitInt64(field, (Long)o);",
        "              break;",
        "          }",
        "        }",
        "      }",
        "    }");

      content.println("  }");
    }

    content.println("}");
    return content.toString();
  }

  private void gen(GenWriter content, FieldProperty field) {
    if (field.typeTo == null) {
      // Message
      if (field.map) {
        content.println(
          "      visitor.visitMap(FieldLiteral." + field.identifier + ", v.entrySet().iterator(), (entry, v2) -> {",
          "        v2." + field.keyTypeTo.visitMethod + "(FieldLiteral." + field.keyIdentifier + ", " + field.keyTypeTo.fn.apply("entry.getKey()") + ");"
        );
        if (field.valueTypeTo == null) {
          // Message
          content.println(
            "        v2.enter(FieldLiteral." + field.valueIdentifier + ");",
            "        visit(entry.getValue(), v2);",
            "        v2.leave(FieldLiteral." + field.valueIdentifier + ");");
        } else {
          content.println(
            "        v2." + field.valueTypeTo.visitMethod + "(FieldLiteral." + field.valueIdentifier + ", " + field.valueTypeTo.fn.apply("entry.getValue()") + ");");
        }
        content.println(
          "      });");
      } else {
        if (field.repeated) {
          content.println(
            "      visitor.<" + field.javaTypeInternal + ">visitEmbedded(FieldLiteral." + field.identifier + ", v.iterator(), " + field.protoWriterFqn +  "::visit);");
        } else {
          content.println(
            "      visitor.<" + field.javaTypeInternal + ">visitEmbedded(FieldLiteral." + field.identifier + ", v, " + field.protoWriterFqn +  "::visit);");
        }
      }
    } else {
      if (field.repeated) {
        if (field.typeTo.fn == Function.<String>identity()) {
          content.println(
            "      visitor." + field.typeTo.visitMethod + "(FieldLiteral." + field.identifier + ", v.iterator());"
          );
        } else {
          content.println(
            "      visitor." + field.typeTo.visitMethod + "(FieldLiteral." + field.identifier + ", new java.util.Iterator<>() {",
            "        final java.util.Iterator<" + field.javaTypeInternal + "> iterator = v.iterator();",
            "        public boolean hasNext() {",
            "          return iterator.hasNext();",
            "        }",
            "        public " + field.typeTo.javaType + " next() {",
            "          return " + field.typeTo.fn.apply("iterator.next()") + ";",
            "        }",
            "      });");
        }
      } else {
//        if (field.typeTo.lengthDelimited) {
//          content.println("      visitor.enter(FieldLiteral." + field.identifier + ");");
//        }
        content.println("      visitor." + field.typeTo.visitMethod + "(FieldLiteral." + field.identifier + ", " + field.typeTo.fn.apply("v") + ");");
//        if (field.typeTo.lengthDelimited) {
//          content.println("      visitor.leave(FieldLiteral." + field.identifier + ");");
//        }
      }
    }
  }

}
