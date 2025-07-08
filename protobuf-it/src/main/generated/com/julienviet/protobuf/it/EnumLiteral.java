package com.julienviet.protobuf.it;

import com.julienviet.protobuf.schema.Schema;
import com.julienviet.protobuf.schema.DefaultSchema;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.schema.DefaultMessageType;
import com.julienviet.protobuf.schema.ScalarType;
import com.julienviet.protobuf.schema.EnumType;
import com.julienviet.protobuf.schema.DefaultEnumType;
import com.julienviet.protobuf.schema.Field;
import java.util.Map;
import java.util.HashMap;
import java.util.OptionalInt;

public enum EnumLiteral implements EnumType {

  ;
  private final Map<String, Integer> numberByName = new HashMap<>();
  private final Map<Integer, String> nameByNumber = new HashMap<>();
  EnumLiteral() {
  }
  public OptionalInt numberOf(String name) {
    Integer number = numberByName.get(name);
    return number == null ? OptionalInt.empty() : OptionalInt.of(number);
  }
  public String nameOf(int number) {
    return nameByNumber.get(number);
  }
  static {
  }
}
