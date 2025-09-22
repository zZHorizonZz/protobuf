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
package io.vertx.protobuf.tests.core.json;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.vertx.protobuf.core.json.ProtoJsonReader;
import io.vertx.protobuf.well_known_types.Duration;
import io.vertx.protobuf.tests.core.support.interop.Container;
import io.vertx.protobuf.tests.core.support.interop.InteropProto;
import io.vertx.protobuf.tests.core.support.interop.MessageLiteral;
import io.vertx.protobuf.tests.core.support.interop.ProtoReader;
import io.vertx.protobuf.tests.core.support.interop.ProtoWriter;
import io.vertx.protobuf.tests.core.InteropTestBase;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class InteropTest extends InteropTestBase {

  @Parameterized.Parameters
  public static Collection<Object[]> params() {
    return ProtoJsonWriterProvider.all();
  }

  private final ProtoJsonWriterProvider writerProvider;

  public InteropTest(ProtoJsonWriterProvider writerProvider) {
    this.writerProvider = writerProvider;
  }

  @Override
  protected Container read(InteropProto.Container src) {
    String json;
    try {
      json = JsonFormat.printer().print(src);
    } catch (InvalidProtocolBufferException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    ProtoReader reader = new ProtoReader();
    ProtoJsonReader.parse(json, MessageLiteral.Container, reader);
    return (Container) reader.stack.pop();
  }

  @Override
  protected InteropProto.Container write(Container src) {
    String json = writerProvider.encodeToString(v -> ProtoWriter.emit(src, v));
    try {
      InteropProto.Container.Builder builder = InteropProto.Container.newBuilder();
      JsonFormat.parser().merge(json, builder);
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
  }

  @Test
  public void testDurationConversion() {
    assertDuration(new Duration(), "0s");
    assertDuration(new Duration().setSeconds(1L), "1s");
    assertDuration(new Duration().setNanos(1), "0.000000001s");
    assertDuration(new Duration().setNanos(10), "0.00000001s");
    assertDuration(new Duration().setNanos(100), "0.0000001s");
    assertDuration(new Duration().setNanos(123456789), "0.123456789s");
    assertDuration(new Duration().setSeconds(-315576000000L).setNanos(-999999999), "-315576000000.999999999s");
  }

  private void assertDuration(Duration expected, String s) {
    Duration parsed = ProtoJsonReader.parseDuration(s);
    assertNotNull(parsed);
    assertEquals(expected.getSeconds(), parsed.getSeconds());
    assertEquals(expected.getNanos(), parsed.getNanos());
  }
}
