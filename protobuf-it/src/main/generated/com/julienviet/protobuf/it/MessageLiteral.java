package com.julienviet.protobuf.it;

import com.julienviet.protobuf.schema.Schema;
import com.julienviet.protobuf.schema.DefaultSchema;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.schema.DefaultMessageType;
import com.julienviet.protobuf.schema.ScalarType;
import com.julienviet.protobuf.schema.EnumType;
import com.julienviet.protobuf.schema.DefaultEnumType;
import com.julienviet.protobuf.schema.Field;

public enum MessageLiteral implements MessageType {

  SimpleMessage("SimpleMessage");
  final java.util.Map<Integer, FieldLiteral> byNumber;
  final java.util.Map<String, FieldLiteral> byJsonName;
  final java.util.Map<String, FieldLiteral> byName;
  MessageLiteral(String name) {
    this.byNumber = new java.util.HashMap<>();
    this.byJsonName = new java.util.HashMap<>();
    this.byName = new java.util.HashMap<>();
  }
  public Field field(int number) {
    return byNumber.get(number);
  }
  public Field fieldByJsonName(String name) {
    return byJsonName.get(name);
  }
  public Field fieldByName(String name) {
    return byName.get(name);
  }
  static {
    MessageLiteral.SimpleMessage.byNumber.put(1, FieldLiteral.SimpleMessage_string_field);
    MessageLiteral.SimpleMessage.byJsonName.put("stringField", FieldLiteral.SimpleMessage_string_field);
    MessageLiteral.SimpleMessage.byName.put("string_field", FieldLiteral.SimpleMessage_string_field);
    MessageLiteral.SimpleMessage.byNumber.put(2, FieldLiteral.SimpleMessage_long_field);
    MessageLiteral.SimpleMessage.byJsonName.put("longField", FieldLiteral.SimpleMessage_long_field);
    MessageLiteral.SimpleMessage.byName.put("long_field", FieldLiteral.SimpleMessage_long_field);
  }
}
