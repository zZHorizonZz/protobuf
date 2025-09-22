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
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.vertx.protobuf.core.json.ProtoJsonReader;
import io.vertx.protobuf.schema.MessageType;
import io.vertx.protobuf.tests.core.RecordingVisitor;
import io.vertx.protobuf.tests.core.RepetitionTestBase;
import io.vertx.protobuf.tests.core.support.repetition.ProtoReader;
import junit.framework.AssertionFailedError;

public class RepetitionTest extends RepetitionTestBase {

  @Override
  protected void assertRepetition(MessageLite message, MessageType type, RecordingVisitor visitor) {
    String json;
    try {
      json = JsonFormat.printer().print((MessageOrBuilder) message);
    } catch (InvalidProtocolBufferException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    ProtoJsonReader.parse(json, type, visitor.checker());
  }

  @Override
  protected <T> T parseRepetition(MessageLite message, MessageType type) {
    String json;
    try {
      json = JsonFormat.printer().print((MessageOrBuilder) message);
    } catch (InvalidProtocolBufferException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    ProtoReader reader = new ProtoReader();
    ProtoJsonReader.parse(json, type, reader);
    return (T) reader.stack.pop();
  }
}
