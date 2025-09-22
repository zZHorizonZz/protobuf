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
package io.vertx.protobuf.tests.core;

import com.google.protobuf.Duration;
import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import io.vertx.protobuf.tests.core.support.interop.InteropProto;
import io.vertx.protobuf.tests.core.support.interop.Container;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;

public abstract class InteropTestBase {

  @Ignore
  @Test
  public void testStruct() {
    InteropProto.Container expected = InteropProto.Container.newBuilder()
      .setStruct(Struct.newBuilder()
        .putFields("string-key", Value.newBuilder().setStringValue("string-value").build())
        .putFields("null-key", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
        .putFields("number-key", Value.newBuilder().setNumberValue(3.14).build())
        .putFields("true-key", Value.newBuilder().setBoolValue(true).build())
        .putFields("false-key", Value.newBuilder().setBoolValue(false).build())
        .putFields("array-key", Value.newBuilder().setListValue(ListValue.newBuilder().addValues(Value.newBuilder().setStringValue("the-string").build()).build()).build())
        .putFields("object-key", Value.newBuilder().setStructValue(Struct.newBuilder().putFields("the-key", Value.newBuilder().setStringValue("the-value").build()).build()).build())
        .build())
      .build();
    Container msg = read(expected);
    InteropProto.Container encoded = write(msg);
    assertEquals(expected, encoded);
  }

  @Test
  public void testDuration() {
    testDuration(1, 1);
    testDuration(0, 5);
    testDuration(1, 0);
    testDuration(1, 123456789);
    testDuration(-1, -1);
    testDuration(0, 500_000_000);
  }

  private void testDuration(long seconds, int nano) {
    InteropProto.Container expected = InteropProto.Container.newBuilder()
      .setDuration(Duration.newBuilder().setSeconds(seconds).setNanos(nano))
      .build();
    Container msg = read(expected);
    java.time.Duration duration = msg.getDuration();
    assertEquals(java.time.Duration.ofSeconds(seconds, nano), duration);
    InteropProto.Container actual = write(msg);
    assertEquals(java.time.Duration.ofSeconds(expected.getDuration().getSeconds(), expected.getDuration().getNanos()),
      java.time.Duration.ofSeconds(actual.getDuration().getSeconds(), actual.getDuration().getNanos()));
  }

  @Test
  public void testTimestamp() {
    testTimestamp(1, 1);
    testTimestamp(0, 5);
    testTimestamp(1, 0);
    testTimestamp(1, 123456789);
    testTimestamp(0, 500_000_000);
  }

  private void testTimestamp(long seconds, int nano) {
    InteropProto.Container expected = InteropProto.Container.newBuilder()
      .setTimestamp(Timestamp.newBuilder().setSeconds(seconds).setNanos(nano))
      .build();
    Container msg = read(expected);
    java.time.OffsetDateTime duration = msg.getTimestamp();
    assertEquals(java.time.OffsetDateTime.ofInstant(Instant.ofEpochSecond(seconds, nano), ZoneId.of("UTC")), duration);
    InteropProto.Container actual = write(msg);
    assertEquals(
      java.time.OffsetDateTime.ofInstant(Instant.ofEpochSecond(expected.getDuration().getSeconds(), expected.getDuration().getNanos()), ZoneId.of("UTC")),
      java.time.OffsetDateTime.ofInstant(Instant.ofEpochSecond(actual.getDuration().getSeconds(), actual.getDuration().getNanos()), ZoneId.of("UTC"))
    );
  }

  protected abstract Container read(InteropProto.Container src);
  protected abstract InteropProto.Container write(Container src);

}
