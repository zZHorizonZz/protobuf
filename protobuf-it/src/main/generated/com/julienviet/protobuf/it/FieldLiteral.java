package com.julienviet.protobuf.it;

import com.julienviet.protobuf.schema.Schema;
import com.julienviet.protobuf.schema.DefaultSchema;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.schema.DefaultMessageType;
import com.julienviet.protobuf.schema.ScalarType;
import com.julienviet.protobuf.schema.EnumType;
import com.julienviet.protobuf.schema.DefaultEnumType;
import com.julienviet.protobuf.schema.Field;

public enum FieldLiteral implements Field {

  SimpleMessage_string_field(1, false, false, false, false, false, "string_field", "stringField"),
  SimpleMessage_long_field(2, false, false, false, false, false, "long_field", "longField");
  private MessageLiteral owner;
  private com.julienviet.protobuf.schema.Type type;
  private final int number;
  private final boolean map;
  private final boolean mapKey;
  private final boolean mapValue;
  private final boolean repeated;
  private final boolean packed;
  private final String name;
  private final String jsonName;
  FieldLiteral(int number, boolean map, boolean mapKey, boolean mapValue, boolean repeated, boolean packed, String name, String jsonName) {
    this.number = number;
    this.map = map;
    this.mapKey = mapKey;
    this.mapValue = mapValue;
    this.repeated = repeated;
    this.packed = packed;
    this.name = name;
    this.jsonName = jsonName;
  }
  public MessageType owner() {
    return owner;
  }
  public int number() {
    return number;
  }
  public String protoName() {
    return name;
  }
  public String jsonName() {
    return jsonName;
  }
  public boolean isMap() {
    return map;
  }
  public boolean isMapKey() {
    return mapKey;
  }
  public boolean isMapValue() {
    return mapValue;
  }
  public boolean isRepeated() {
    return repeated;
  }
  public boolean isPacked() {
    return packed;
  }
  public com.julienviet.protobuf.schema.Type type() {
    return type;
  }
  static {
    FieldLiteral.SimpleMessage_string_field.owner = MessageLiteral.SimpleMessage;
    FieldLiteral.SimpleMessage_string_field.type = ScalarType.STRING;
    FieldLiteral.SimpleMessage_long_field.owner = MessageLiteral.SimpleMessage;
    FieldLiteral.SimpleMessage_long_field.type = ScalarType.INT64;
  }
}
