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
package com.julienviet.protobuf.core;


import com.julienviet.protobuf.schema.Field;
import com.julienviet.protobuf.schema.MessageType;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ProtoVisitor {

  void init(MessageType type);

  void destroy();

  void visitInt32(Field field, int v);

  void visitUInt32(Field field, int v);

  void visitSInt32(Field field, int v);

  void visitEnum(Field field, int number);

  void visitInt64(Field field, long v);

  void visitUInt64(Field field, long v);

  void visitSInt64(Field field, long v);

  void visitBool(Field field, boolean v);

  void visitDouble(Field field, double d);

  void visitFixed64(Field field, long v);

  void visitSFixed64(Field field, long v);

  void visitFloat(Field field, float f);

  void visitFixed32(Field field, int v);

  void visitSFixed32(Field field, int v);

  // LEN

  default <T> void visitEmbedded(Field field, T embedded, BiConsumer<T, ProtoVisitor> continuation) {
    enter(field);
    continuation.accept(embedded, this);
    leave(field);
  }

  default <T> void visitEmbedded(Field field, Iterator<T> iterator, BiConsumer<T, ProtoVisitor> continuation) {
    while (iterator.hasNext()) {
      enter(field);
      continuation.accept(iterator.next(), this);
      leave(field);
    }
  }

  default <K, V> void visitMap(Field field, Iterator<Map.Entry<K, V>> entries, BiConsumer<Map.Entry<K, V>, ProtoVisitor> continuation) {
    while (entries.hasNext()) {
      enter(field);
      continuation.accept(entries.next(), this);
      leave(field);
    }
  }

//  void enterMap(Field field);
//
//  void leaveMap(Field field);

  void enter(Field field);

  void leave(Field field);

  void visitString(Field field, String s);

  void visitBytes(Field field, byte[] bytes);

  void enterPacked(Field field);

  void leavePacked(Field field);

  //

  default void visitInt32(Field field, Iterator<Integer> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitInt32(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitInt32(field, iterator.next());
      }
    }
  }

  default void visitUInt32(Field field, Iterator<Integer> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitUInt32(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitUInt32(field, iterator.next());
      }
    }
  }

  default void visitSInt32(Field field, Iterator<Integer> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitSInt32(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitSInt32(field, iterator.next());
      }
    }
  }

  default void visitEnum(Field field, Iterator<Integer> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitEnum(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitEnum(field, iterator.next());
      }
    }
  }

  default void visitInt64(Field field, Iterator<Long> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitInt64(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitInt64(field, iterator.next());
      }
    }
  }

  default void visitUInt64(Field field, Iterator<Long> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitUInt64(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitUInt64(field, iterator.next());
      }
    }
  }

  default void visitSInt64(Field field, Iterator<Long> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitSInt64(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitSInt64(field, iterator.next());
      }
    }
  }

  default void visitBool(Field field, Iterator<Boolean> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitBool(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitBool(field, iterator.next());
      }
    }
  }

  default void visitDouble(Field field, Iterator<Double> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitDouble(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitDouble(field, iterator.next());
      }
    }
  }

  default void visitFixed64(Field field, Iterator<Long> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitFixed64(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitFixed64(field, iterator.next());
      }
    }
  }

  default void visitSFixed64(Field field, Iterator<Long> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitSFixed64(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitSFixed64(field, iterator.next());
      }
    }
  }

  default void visitFloat(Field field, Iterator<Float> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitFloat(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitFloat(field, iterator.next());
      }
    }
  }

  default void visitFixed32(Field field, Iterator<Integer> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitFixed32(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitFixed32(field, iterator.next());
      }
    }
  }

  default void visitSFixed32(Field field, Iterator<Integer> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitSFixed32(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitSFixed32(field, iterator.next());
      }
    }
  }

  //

  default void visitString(Field field, Iterator<String> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitString(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitString(field, iterator.next());
      }
    }
  }

  default void visitBytes(Field field, Iterator<byte[]> iterator) {
    if (field.isPacked()) {
      enterPacked(field);
      while (iterator.hasNext()) {
        visitBytes(field, iterator.next());
      }
      leavePacked(field);
    } else {
      while (iterator.hasNext()) {
        visitBytes(field, iterator.next());
      }
    }
  }
}
