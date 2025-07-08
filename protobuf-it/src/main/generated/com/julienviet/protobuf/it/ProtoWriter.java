package com.julienviet.protobuf.it;
import com.julienviet.protobuf.core.ProtoVisitor;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.schema.Field;

public class ProtoWriter {
  public static void emit(com.julienviet.protobuf.it.SimpleMessage value, ProtoVisitor visitor) {
    visitor.init(MessageLiteral.SimpleMessage);
    visit(value, visitor);
    visitor.destroy();
  }
  public static com.julienviet.protobuf.core.ProtoStream streamOf(com.julienviet.protobuf.it.SimpleMessage value) {
    return visitor -> {
    emit(value, visitor);
    };
  }
  public static void visit(com.julienviet.protobuf.it.SimpleMessage value, ProtoVisitor visitor) {
    if (!value.getStringField().isEmpty()) {
      java.lang.String v = value.getStringField();
      visitor.visitString(FieldLiteral.SimpleMessage_string_field, v);
    }
    if (value.getLongField() != 0L) {
      java.lang.Long v = value.getLongField();
      visitor.visitInt64(FieldLiteral.SimpleMessage_long_field, v);
    }
    java.lang.Iterable<java.util.Map.Entry<com.julienviet.protobuf.schema.Field, java.util.List<Object>>> unknownFields = value instanceof com.julienviet.protobuf.lang.Message ? ((com.julienviet.protobuf.lang.Message)value).unknownFields() : null;
    if (unknownFields != null) {
      for (java.util.Map.Entry<com.julienviet.protobuf.schema.Field, java.util.List<Object>> unknownField : unknownFields) {
        for (Object o : unknownField.getValue()) {
          com.julienviet.protobuf.schema.Field field = unknownField.getKey();
          switch (field.type().wireType()) {
            case LEN:
              visitor.visitBytes(field, (byte[])o);
              break;
            case I32:
              visitor.visitFixed32(field, (Integer)o);
              break;
            case I64:
              visitor.visitFixed64(field, (Long)o);
              break;
            case VARINT:
              visitor.visitInt64(field, (Long)o);
              break;
          }
        }
      }
    }
  }
}
