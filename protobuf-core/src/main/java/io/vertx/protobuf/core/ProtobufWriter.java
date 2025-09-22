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
package io.vertx.protobuf.core;

import io.vertx.protobuf.schema.Field;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.schema.WireType;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static java.lang.Character.MIN_SUPPLEMENTARY_CODE_POINT;

public class ProtobufWriter {

  public static int encodeSInt32(int value) {
    return (value << 1) ^ (value >> 31);
  }

  public static long encodeSInt64(long value) {
    return (value << 1) ^ (value >> 63);
  }

  public static byte[] encodeToByteArray(Consumer<ProtoVisitor> consumer) {
    State state = new State();
    ComputePhase visitor = new ComputePhase();
    visitor.state = state;
    consumer.accept(visitor);
    EncodingPhase encoder = new EncodingPhase();
    encoder.state = state;
    consumer.accept(encoder);
    return encoder.encoder.buffer();
  }

  static class State {
    int[] capture = new int[50];
  }

  static class ComputePhase implements ProtoVisitor {

    State state;
    int[] numbers = new int[10];
    int[] lengths = new int[10];
    int[] indices = new int[10];
    int depth;
    int ptr;
    int string_ptr;
    boolean packed;

    private int sizeOf(Field field) {
      return DefaultProtobufEncoder.computeRawVarint32Size(field.number() << 3 | field.type().wireType().id);
    }

    @Override
    public void visitInt32(Field field, int v) {
      visitVarInt32(field, v);
    }

    @Override
    public void visitUInt32(Field field, int v) {
      visitVarInt32(field, v);
    }

    @Override
    public void visitSInt32(Field field, int v) {
      visitVarInt32(field, encodeSInt32(v));
    }

    @Override
    public void visitEnum(Field field, int number) {
      visitVarInt64(field, number);
    }

    public void visitVarInt32(Field field, int v) {
      int delta = (packed ? 0 : sizeOf(field)) + DefaultProtobufEncoder.computeRawVarint32Size(v);
      lengths[depth] += delta;
    }

    @Override
    public void visitInt64(Field field, long v) {
      visitVarInt64(field, v);
    }

    @Override
    public void visitUInt64(Field field, long v) {
      visitVarInt64(field, v);
    }

    @Override
    public void visitSInt64(Field field, long v) {
      visitVarInt64(field, encodeSInt64(v));
    }

    @Override
    public void visitBool(Field field, boolean v) {
      visitVarInt64(field, v ? 1 : 0);
    }

    public void visitVarInt64(Field field, long v) {
      lengths[depth] +=  (packed ? 0 : sizeOf(field)) + DefaultProtobufEncoder.computeRawVarint64Size(v);
    }

    @Override
    public void visitFloat(Field field, float f) {
      visitI32(field, Float.floatToRawIntBits(f));
    }

    @Override
    public void visitFixed32(Field field, int v) {
      visitI32(field, v);
    }

    @Override
    public void visitSFixed32(Field field, int v) {
      visitI32(field, v);
    }

    public void visitI32(Field field, int value) {
      lengths[depth] += (packed ? 0 : sizeOf(field)) + 4;
    }

    @Override
    public void visitDouble(Field field, double d) {
      visitI64(field, Double.doubleToRawLongBits(d));
    }

    @Override
    public void visitFixed64(Field field, long v) {
      visitI64(field, v);
    }

    @Override
    public void visitSFixed64(Field field, long v) {
      visitI64(field, v);
    }

    public void visitI64(Field field, long value) {
      lengths[depth] +=  (packed ? 0 : sizeOf(field)) + 8;
    }

    @Override
    public void visitBytes(Field field, byte[] bytes) {
      enterLengthDelimited(field);
      lengths[depth] += bytes.length;
      leaveLengthDelimited(field);
    }

    @Override
    public void visitString(Field field, String s) {
      enterLengthDelimited(field);
      int length = 0;
      int a = s.length();
      for (int i = 0;i < a;i++) {
        char c = s.charAt(i);
        if (c < 128) {
          length++;
        } else {
          length = s.getBytes(StandardCharsets.UTF_8).length;
          break;
        }
      }
      lengths[depth] += length;
      leaveLengthDelimited(field);
    }

    @Override
    public void init(MessageType type) {
      string_ptr = 0;
      depth = 0;
      ptr = 0;
      indices[0] = ptr++;
    }

    @Override
    public void enter(Field field) {
      enterLengthDelimited(field);
    }

    @Override
    public void enterPacked(Field field) {
      if (field.isPacked()) {
        packed = true;
        enterLengthDelimited(field);
      }
    }

    private void enterLengthDelimited(Field field) {
      numbers[depth] = field.number();
      depth++;
      indices[depth] = ptr++;
      lengths[depth] = 0;
    }

    @Override
    public void leavePacked(Field field) {
      if (field.isPacked()) {
        packed = false;
        leaveLengthDelimited(field);
      }
    }

    @Override
    public void leave(Field field) {
      leaveLengthDelimited(field);
    }

    private void leaveLengthDelimited(Field field) {
      int l = lengths[depth];
      lengths[depth] = 0;
      int index = indices[depth];
      state.capture[index] = l;
      l += sizeOf(field) + DefaultProtobufEncoder.computeRawVarint32Size(l);
      depth--;
      lengths[depth] += l;
    }

    @Override
    public void destroy() {
      int l = lengths[depth];
      state.capture[indices[depth]] = l;
    }
  }

  static class EncodingPhase implements ProtoVisitor {

    State state;
    ProtobufEncoder encoder;
    int ptr_;
    boolean packed;


    @Override
    public void init(MessageType type) {
      ptr_ = 0;
      int size = state.capture[ptr_++];
      encoder = new DefaultProtobufEncoder(size);
    }

    @Override
    public void visitInt32(Field field, int v) {
      visitVarInt32(field, v);
    }

    @Override
    public void visitUInt32(Field field, int v) {
      visitVarInt32(field, v);
    }

    @Override
    public void visitSInt32(Field field, int v) {
      visitVarInt32(field, encodeSInt32(v));
    }

    @Override
    public void visitEnum(Field field, int number) {
      visitVarInt64(field, number);
    }

    public void visitVarInt32(Field field, int v) {
      if (!packed) {
        encoder.writeTag(field.number(), WireType.VARINT.id);
      }
      encoder.writeVarInt32(v);
    }

    @Override
    public void visitInt64(Field field, long v) {
      visitVarInt64(field, v);
    }

    @Override
    public void visitUInt64(Field field, long v) {
      visitVarInt64(field, v);
    }

    @Override
    public void visitSInt64(Field field, long v) {
      visitVarInt64(field, encodeSInt64(v));
    }

    @Override
    public void visitBool(Field field, boolean v) {
      visitVarInt64(field, v ? 1 : 0);
    }

    public void visitVarInt64(Field field, long v) {
      if (!packed) {
        encoder.writeTag(field.number(), WireType.VARINT.id);
      }
      encoder.writeVarInt64(v);
    }

    @Override
    public void visitFloat(Field field, float f) {
      visitI32(field, Float.floatToRawIntBits(f));
    }

    @Override
    public void visitFixed32(Field field, int v) {
      visitI32(field, v);
    }

    @Override
    public void visitSFixed32(Field field, int v) {
      visitI32(field, v);
    }

    public void visitI32(Field field, int value) {
      if (!packed) {
        encoder.writeTag(field.number(), WireType.I32.id);
      }
      encoder.writeInt(value);
    }

    @Override
    public void visitDouble(Field field, double d) {
      visitI64(field, Double.doubleToRawLongBits(d));
    }

    @Override
    public void visitFixed64(Field field, long v) {
      visitI64(field, v);
    }

    @Override
    public void visitSFixed64(Field field, long v) {
      visitI64(field, v);
    }

    public void visitI64(Field field, long value) {
      if (!packed) {
        encoder.writeTag(field.number(), WireType.I64.id);
      }
      encoder.writeLong(value);
    }

    @Override
    public void visitBytes(Field field, byte[] bytes) {
      enterLengthDelimited(field);
      encoder.writeBinary(bytes);
      leaveLengthDelimited(field);
    }

    @Override
    public void visitString(Field field, String s) {
      enterLengthDelimited(field);
      encoder.writeString(s);
      leaveLengthDelimited(field);
    }

    @Override
    public void enterPacked(Field field) {
      if (field.isPacked()) {
        packed = true;
        enterLengthDelimited(field);
      }
    }

    @Override
    public void enter(Field field) {
      enterLengthDelimited(field);
    }

    private void enterLengthDelimited(Field field) {
      encoder.writeTag(field.number(), WireType.LEN.id);
      encoder.writeVarInt32(state.capture[ptr_++]);
    }

    @Override
    public void leavePacked(Field field) {
      if (field.isPacked()) {
        packed = false;
        leaveLengthDelimited(field);
      }
    }

    @Override
    public void leave(Field field) {
      leaveLengthDelimited(field);
    }

    private void leaveLengthDelimited(Field field) {
    }

    @Override
    public void destroy() {
    }
  }

  private static int encodedLengthGeneral(String string, int start) {
    int utf16Length = string.length();
    int utf8Length = 0;
    for (int i = start; i < utf16Length; i++) {
      char c = string.charAt(i);
      if (c < 0x800) {
        utf8Length += (0x7f - c) >>> 31; // branch free!
      } else {
        utf8Length += 2;
        // jdk7+: if (Character.isSurrogate(c)) {
        if (Character.MIN_SURROGATE <= c && c <= Character.MAX_SURROGATE) {
          // Check that we have a well-formed surrogate pair.
          int cp = Character.codePointAt(string, i);
          if (cp < MIN_SUPPLEMENTARY_CODE_POINT) {
            throw new EncodeException();
          }
          i++;
        }
      }
    }
    return utf8Length;
  }
}
