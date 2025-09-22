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
package io.vertx.protobuf.lang;

import java.util.function.Supplier;

/**
 * Mutable protobuf generic and reusable map entry.
 * @param <K>
 * @param <V>
 */
public class MapEntry<K, V> {

  private final Supplier<K> defaultKeySupplier;
  private final Supplier<V> defaultValueSupplier;

  public MapEntry(Supplier<K> defaultKeySupplier, Supplier<V> defaultValueSupplier) {
    this.defaultKeySupplier = defaultKeySupplier;
    this.defaultValueSupplier = defaultValueSupplier;
  }

  private K key;
  private V value;

  public V getValue() {
    V ret = value;
    return ret == null ? defaultValueSupplier.get() : ret;
  }

  public void setValue(V value) {
    this.value = value;
  }

  public K getKey() {
    K ret = key;
    return ret == null ? defaultKeySupplier.get() : ret;
  }

  public void setKey(K key) {
    this.key = key;
  }
}
