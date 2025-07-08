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
package com.julienviet.protobuf.tests.codegen;

import io.vertx.codegen.processor.Compiler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import com.julienviet.protobuf.core.ProtoStream;
import com.julienviet.protobuf.core.ProtobufReader;
import com.julienviet.protobuf.core.ProtobufWriter;
import com.julienviet.protobuf.codegen.ProtoProcessor;
import com.julienviet.protobuf.codegen.ValidationError;
import com.julienviet.protobuf.codegen.ValidationException;
import com.julienviet.protobuf.core.json.ProtoJsonReader;
import com.julienviet.protobuf.core.json.ProtoJsonWriter;
import com.julienviet.protobuf.schema.EnumType;
import com.julienviet.protobuf.schema.Field;
import com.julienviet.protobuf.schema.MessageType;
import com.julienviet.protobuf.schema.ScalarType;
import com.julienviet.protobuf.schema.TypeID;
import com.julienviet.protobuf.tests.codegen.datatypes.DataTypeContainer;
import com.julienviet.protobuf.tests.codegen.datatypes.TestEnum;
import com.julienviet.protobuf.tests.codegen.embedding.Embedded;
import com.julienviet.protobuf.tests.codegen.embedding.EmbeddedContainer;
import com.julienviet.protobuf.tests.codegen.imports.AnnotatedImportedMessageContainer;
import com.julienviet.protobuf.tests.codegen.imports.GeneratedImportedMessageContainer;
import com.julienviet.protobuf.tests.codegen.interop.InteropContainer;
import com.julienviet.protobuf.tests.codegen.maps.MapsContainer;
import com.julienviet.protobuf.tests.codegen.naming.InferFromGetter;
import com.julienviet.protobuf.tests.codegen.naming.InferFromSetter;
import com.julienviet.protobuf.tests.codegen.naming.OverrideJsonName;
import com.julienviet.protobuf.tests.codegen.naming.OverrideProtoName;
import com.julienviet.protobuf.tests.codegen.repetition.RepeatedContainer;
import com.julienviet.protobuf.tests.codegen.unknown.UnknownAware;
import com.julienviet.protobuf.tests.codegen.unknown.UnknownUnaware;
import com.julienviet.protobuf.tests.codegen.validation.AbstractMessage;
import com.julienviet.protobuf.tests.codegen.validation.InterfaceMessage;
import com.julienviet.protobuf.tests.codegen.validation.InvalidEnumField;
import com.julienviet.protobuf.tests.codegen.validation.InvalidFieldMethod;
import com.julienviet.protobuf.tests.codegen.validation.MissingBooleanGetter;
import com.julienviet.protobuf.tests.codegen.validation.MissingBooleanSetter;
import com.julienviet.protobuf.tests.codegen.validation.MissingGetter;
import com.julienviet.protobuf.tests.codegen.validation.MissingSetter;
import com.julienviet.protobuf.tests.codegen.validation.NoDefaultConstructorMessage;
import com.julienviet.protobuf.tests.codegen.validation.PrivateDefaultConstructorMessage;
import com.julienviet.protobuf.well_known_types.Duration;
import com.julienviet.protobuf.well_known_types.ProtoWriter;
import junit.framework.AssertionFailedError;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ProcessorTest {

  @Rule
  public TestName name = new TestName();

  private Compiler compiler(DiagnosticListener<JavaFileObject> diagnosticListener) {
    String sprop = System.getProperty("maven.project.build.directory");
    if (sprop == null) {
      throw new AssertionFailedError("Was expecting maven.project.build.directory system property to be set");
    }
    File target = new File(sprop);
    if (!target.exists() || !target.isDirectory()) {
      throw new AssertionFailedError();
    }
    File sourceOutput = new File(target, "tests-" + name.getMethodName() );
    if (sourceOutput.exists()) {
      File dst;
      int idx = 0;
      while (true) {
        dst = new File(target, "tests-" + name.getMethodName() + "-" + idx);
        if (!dst.exists()) {
          break;
        }
        idx++;
      }
      if (!sourceOutput.renameTo(dst)) {
        throw new AssertionFailedError();
      }
    }
    if (!sourceOutput.mkdirs()) {
      throw new AssertionFailedError();
    }
    Compiler compiler = new Compiler(new ProtoProcessor(true), diagnosticListener);
    compiler.setSourceOutput(sourceOutput);
    return compiler;
  }

  private Exception assertCompilationFailure(Class<?>... types) {
    Compiler compiler = compiler(diagnostic -> {
    });
    try {
      compiler.compile(types);
      throw new AssertionFailedError();
    } catch (Exception e) {
      return e;
    }
  }

  private MessageMappers assertCompile(Class<?>... types) {
    List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<>();
    Compiler compiler = compiler(diagnostic -> {
      if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
        errors.add(diagnostic);
      }
    });
    try {
      if (!compiler.compile(types)) {
        AssertionFailedError afe;
        if (!errors.isEmpty()) {
          Diagnostic<? extends JavaFileObject> error = errors.get(0);
          afe = new AssertionFailedError(error.toString());
        } else {
          afe = new AssertionFailedError();
        }
        throw afe;
      }
    } catch (Exception e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    URL url = null;
    try {
      url = compiler.getClassOutput().toURI().toURL();
    } catch (MalformedURLException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
    return new MessageMappers(classLoader);
  }

  class MessageMappers {
    private final ClassLoader loader;
    public <T> MessageMapper<T> of(Class<T> messageClass) {
      return new MessageMapper<>(loader, messageClass);
    }
    public MessageMappers(ClassLoader loader) {
      this.loader = loader;
    }
  }

  class MessageMapper<T> {

    private final ClassLoader loader;
    private final Class<?> messageClass;
    private final Function<T, ProtoStream> streamOf;
    private final Function<ProtoStream, T> read;
    private final MessageType messageLiteral;

    public MessageMapper(ClassLoader loader, Class<T> messageClass) {
      this.loader = loader;
      this.messageClass = messageClass;

      try {
        Class<?> messageLiteralClazz = loader.loadClass(messageClass.getPackageName() + ".MessageLiteral");
        Class<?> writerClazz =  loader.loadClass(messageClass.getPackageName() + ".ProtoWriter");
        Class<?> readerClazz = loader.loadClass(messageClass.getPackageName() + ".ProtoReader");
        Method streamOfMethod = writerClazz.getDeclaredMethod("streamOf", messageClass);
        Method readMethod = readerClazz.getDeclaredMethod("read" + messageClass.getSimpleName(), ProtoStream.class);
        messageLiteral = (MessageType) messageLiteralClazz.getField(messageClass.getSimpleName()).get(null);
        streamOf = instance -> {
          try {
            return (ProtoStream) streamOfMethod.invoke(null, instance);
          } catch (Exception e) {
            AssertionFailedError afe = new AssertionFailedError();
            afe.initCause(e);
            throw afe;
          }
        };
        read = stream -> {
          try {
            return messageClass.cast(readMethod.invoke(null, stream));
          } catch (Exception e) {
            AssertionFailedError afe = new AssertionFailedError();
            afe.initCause(e);
            throw afe;
          }
        };
      } catch (Exception e) {
        AssertionFailedError afe = new AssertionFailedError();
        afe.initCause(e);
        throw afe;
      }
    }

    public MessageType literal() {
      return messageLiteral;
    }

    public ProtoStream streamOf(T t) {
      return streamOf.apply(t);
    }

    public T read(ProtoStream stream) {
      return read.apply(stream);
    }
  }

  @Test
  public void testDataTypes() {
    MessageMappers mappers = assertCompile(DataTypeContainer.class, TestEnum.class);
    MessageMapper<DataTypeContainer> mapper = mappers.of(DataTypeContainer.class);
    MessageType ml = mapper.literal();
    Field f1 = ml.field(1);
    assertEquals("stringField", f1.jsonName());
    assertEquals(ScalarType.STRING, f1.type());
    Field f2 = ml.field(2);
    assertEquals("longField", f2.jsonName());
    assertEquals(ScalarType.INT64, f2.type());
    Field f3 = ml.field(3);
    assertEquals("booleanField", f3.jsonName());
    assertEquals(ScalarType.BOOL, f3.type());
    Field f4 = ml.field(4);
    assertEquals("enumField", f4.jsonName());
    assertEquals(TypeID.ENUM, f4.type().id());
    EnumType enumType = (EnumType) f4.type();
    assertEquals("DEFAULT", enumType.nameOf(0));
    assertEquals("ANOTHER", enumType.nameOf(1));
    JsonObject json = new JsonObject()
      .put("stringField", "the-string")
      .put("enumField", "ANOTHER");
    DataTypeContainer o = mapper.read(ProtoJsonReader.readStream(ml, json.encode()));
    assertEquals("the-string", o.getStringField());
    assertEquals(TestEnum.ANOTHER, o.getEnumField());
    ProtoStream protoStream = mapper.streamOf(o);
    JsonObject res = new JsonObject(ProtoJsonWriter.encode(protoStream));
    assertEquals(json, res);
  }

  @Test
  public void testEmbedded() {
    MessageMappers mappers = assertCompile(EmbeddedContainer.class, Embedded.class);
    MessageMapper<EmbeddedContainer> mapper = mappers.of(EmbeddedContainer.class);
    Embedded embedded = new Embedded();
    embedded.setStringField("embedded-string");
    EmbeddedContainer embedding = new EmbeddedContainer();
    embedding.setStringField("embedding-string");
    embedding.setEmbeddedField(embedded);
    ProtoStream protoStream = mapper.streamOf(embedding);
    JsonObject res = new JsonObject(ProtoJsonWriter.encode(protoStream));
    JsonObject expected = new JsonObject().put("stringField", "embedding-string").put("embeddedField", new JsonObject().put("stringField", "embedded-string"));
    assertEquals(expected, res);
    embedding = mapper.read(ProtoJsonReader.readStream(mapper.literal(), res.encode()));
    assertEquals("embedding-string", embedding.getStringField());
    embedded = embedding.getEmbeddedField();
    assertEquals("embedded-string", embedded.getStringField());
  }

  @Test
  public void testAnnotatedMessageImport() {
    MessageMappers mappers = assertCompile(AnnotatedImportedMessageContainer.class, Embedded.class);
    MessageMapper<AnnotatedImportedMessageContainer> mapper = mappers.of(AnnotatedImportedMessageContainer.class);
    JsonObject expected = new JsonObject()
      .put("stringField", "importing-string")
      .put("importingField", new JsonObject().put("stringField", "embedded-string"));
    AnnotatedImportedMessageContainer importing = mapper.read(ProtoJsonReader.readStream(mapper.literal(), expected.encode()));
    assertEquals("importing-string", importing.getStringField());
    assertEquals("embedded-string", importing.getImportingField().getStringField());
    JsonObject output = new JsonObject(ProtoJsonWriter.encode(mapper.streamOf(importing)));
    assertEquals(expected, output);
  }

  @Test
  public void testGeneratedMessageImport() {
    MessageMappers mappers = assertCompile(GeneratedImportedMessageContainer.class);
    MessageMapper<GeneratedImportedMessageContainer> mapper = mappers.of(GeneratedImportedMessageContainer.class);
    JsonObject expected = new JsonObject()
      .put("stringField", "importing-string")
      .put("importingField", new JsonObject().put("key", "value"));
    GeneratedImportedMessageContainer importing = mapper.read(ProtoJsonReader.readStream(mapper.literal(), expected.encode()));
    assertEquals("value", importing.getImportingField().getFields().get("key").getKind().asStringValue().get());
    JsonObject output = new JsonObject(ProtoJsonWriter.encode(mapper.streamOf(importing)));
    assertEquals(expected, output);
  }

  @Ignore("Cannot pass for now")
  @Test
  public void testInterop() {
    MessageMappers mappers = assertCompile(InteropContainer.class);
    MessageMapper<InteropContainer> mapper = mappers.of(InteropContainer.class);
    JsonObject expected = new JsonObject()
      .put("stringField", "interop-string")
      .put("interopField", new JsonObject().put("key", "value"));
    InteropContainer importing = mapper.read(ProtoJsonReader.readStream(mapper.literal(), expected.encode()));
    assertEquals("value", importing.getInteropField().getString("key"));
    JsonObject output = new JsonObject(ProtoJsonWriter.encode(mapper.streamOf(importing)));
    assertEquals(expected, output);
  }

  @Test
  public void testMaps() {
    MessageMappers mappers = assertCompile(MapsContainer.class);
    MessageMapper<MapsContainer> mapper = mappers.of(MapsContainer.class);
    JsonObject expected = new JsonObject()
      .put("stringField", "interop-string")
      .put("mapField", new JsonObject().put("key", "value"));
    MapsContainer map = mapper.read(ProtoJsonReader.readStream(mapper.literal(), expected.encode()));
    assertEquals(Map.of("key", "value"), map.getMapField());
    JsonObject output = new JsonObject(ProtoJsonWriter.encode(mapper.streamOf(map)));
    assertEquals(expected, output);
  }

  @Test
  public void testRepetition() {
    MessageMappers mappers = assertCompile(RepeatedContainer.class);
    MessageMapper<RepeatedContainer> mapper = mappers.of(RepeatedContainer.class);
    JsonObject expected = new JsonObject()
      .put("stringsField", new JsonArray().add("s1").add("s2").add("s3"));
    RepeatedContainer map = mapper.read(ProtoJsonReader.readStream(mapper.literal(), expected.encode()));
    assertEquals(Arrays.asList("s1", "s2", "s3"), map.getStringsField());
    JsonObject output = new JsonObject(ProtoJsonWriter.encode(mapper.streamOf(map)));
    assertEquals(expected, output);
  }

  @Test
  public void testUnknown() {
    MessageMappers mappers = assertCompile(UnknownAware.class, UnknownUnaware.class);
    MessageMapper<UnknownAware> awareMapper = mappers.of(UnknownAware.class);
    Duration duration = new Duration();
    duration.setSeconds(4);
    duration.setNanos(10);
    byte[] expected = ProtobufWriter.encodeToByteArray(ProtoWriter.streamOf(duration));
    UnknownAware aware = awareMapper.read(ProtobufReader.readerStream(awareMapper.literal(), expected));
    Iterable<Map.Entry<Field, List<Object>>> field = aware.unknownFields();
    Iterator<Map.Entry<Field, List<Object>>> it = field.iterator();
    Map.Entry<Field, List<Object>> f1 = it.next();
    Map.Entry<Field, List<Object>> f2 = it.next();
    assertEquals(1, f1.getKey().number());
    assertEquals(TypeID.INT64, f1.getKey().type().id());
    assertEquals(List.of(4L), f1.getValue());
    assertEquals(2, f2.getKey().number());
    assertEquals(TypeID.INT64, f2.getKey().type().id());
    assertEquals(List.of(10L), f2.getValue());
    assertFalse(it.hasNext());
    byte[] actual = ProtobufWriter.encodeToByteArray(awareMapper.streamOf(aware));
    assertSame(expected.length, actual.length);

    MessageMapper<UnknownUnaware> unwareMapper = mappers.of(UnknownUnaware.class);
    UnknownUnaware unaware = unwareMapper.read(ProtobufReader.readerStream(unwareMapper.literal(), expected));
  }

  @Test
  public void testInferFromGetter() {
    MessageMappers mappers = assertCompile(InferFromGetter.class);
    MessageMapper<InferFromGetter> mm = mappers.of(InferFromGetter.class);
    MessageType literal = mm.literal();
    assertEquals("one_two", literal.field(1).protoName());
    assertEquals("oneTwo", literal.field(1).jsonName());
    assertEquals("u_r_l", literal.field(2).protoName());
    assertEquals("URL", literal.field(2).jsonName());

  }

  @Test
  public void testInferFromSetter() {
    MessageMappers mappers = assertCompile(InferFromSetter.class);
    MessageMapper<InferFromSetter> mm = mappers.of(InferFromSetter.class);
    MessageType literal = mm.literal();
    assertEquals("one_two", literal.field(1).protoName());
    assertEquals("oneTwo", literal.field(1).jsonName());
    assertEquals("u_r_l", literal.field(2).protoName());
    assertEquals("URL", literal.field(2).jsonName());
  }

  @Test
  public void testOverrideProtoName() {
    MessageMappers mappers = assertCompile(OverrideProtoName.class);
    MessageMapper<OverrideProtoName> mm = mappers.of(OverrideProtoName.class);
    MessageType literal = mm.literal();
    assertEquals("the_one_two", literal.field(1).protoName());
    assertEquals("theOneTwo", literal.field(1).jsonName());
    assertEquals("the_url", literal.field(2).protoName());
    assertEquals("theUrl", literal.field(2).jsonName());
  }

  @Test
  public void testOverrideJsonName() {
    MessageMappers mappers = assertCompile(OverrideJsonName.class);
    MessageMapper<OverrideJsonName> mm = mappers.of(OverrideJsonName.class);
    MessageType literal = mm.literal();
    assertEquals("one_two", literal.field(1).protoName());
    assertEquals("unoDos", literal.field(1).jsonName());
    assertEquals("u_r_l", literal.field(2).protoName());
    assertEquals("the-url", literal.field(2).jsonName());
  }

  @Test
  public void testValidation() {
    ValidationException expected;
    expected = (ValidationException) assertCompilationFailure(MissingGetter.class);
    assertEquals(ValidationError.MISSING_GETTER, expected.getError());
    expected = (ValidationException) assertCompilationFailure(MissingBooleanGetter.class);
    assertEquals(ValidationError.MISSING_GETTER, expected.getError());
    expected = (ValidationException) assertCompilationFailure(MissingSetter.class);
    assertEquals(ValidationError.MISSING_SETTER, expected.getError());
    expected = (ValidationException) assertCompilationFailure(MissingBooleanSetter.class);
    assertEquals(ValidationError.MISSING_SETTER, expected.getError());
    expected = (ValidationException) assertCompilationFailure(InvalidFieldMethod.class);
    assertEquals(ValidationError.INVALID_FIELD_METHOD, expected.getError());
    expected = (ValidationException) assertCompilationFailure(AbstractMessage.class);
    assertEquals(ValidationError.INVALID_MESSAGE_CLASS, expected.getError());
    expected = (ValidationException) assertCompilationFailure(NoDefaultConstructorMessage.class);
    assertEquals(ValidationError.INVALID_MESSAGE_CLASS, expected.getError());
    expected = (ValidationException) assertCompilationFailure(PrivateDefaultConstructorMessage.class);
    assertEquals(ValidationError.INVALID_MESSAGE_CLASS, expected.getError());
    expected = (ValidationException) assertCompilationFailure(InterfaceMessage.class);
    assertEquals(ValidationError.INVALID_MESSAGE_CLASS, expected.getError());
    expected = (ValidationException) assertCompilationFailure(InvalidEnumField.class);
    assertEquals(ValidationError.INVALID_MESSAGE_CLASS, expected.getError());
  }
}
