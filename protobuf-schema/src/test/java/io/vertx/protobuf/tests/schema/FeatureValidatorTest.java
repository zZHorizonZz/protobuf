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
package io.vertx.protobuf.tests.schema;

import com.google.protobuf.DescriptorProtos;
import io.vertx.protobuf.schema.FeatureValidator;
import io.vertx.protobuf.schema.FeatureValidator.LegacyFeatureException;
import io.vertx.protobuf.schema.FeatureValidator.ValidationResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class FeatureValidatorTest {

  @Test
  public void testProto3FileSkipsValidation() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("proto3")
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertFalse(result.hasErrors());
  }

  @Test
  public void testProto2FileSkipsValidation() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("proto2")
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertFalse(result.hasErrors());
  }

  @Test
  public void testEditionFileWithNoFeaturesIsValid() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertFalse(result.hasErrors());
  }

  @Test
  public void testEditionFileWithValidFeaturesIsValid() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.EXPLICIT)
        .setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat.ALLOW)
        .build();
    
    DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
        .setFeatures(features)
        .build();
    
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .setOptions(options)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertFalse(result.hasErrors());
  }

  @Test
  public void testEditionFileWithLegacyRequiredFieldPresenceIsInvalid() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED)
        .build();
    
    DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
        .setFeatures(features)
        .build();
    
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .setOptions(options)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertTrue(result.hasErrors());
    assertEquals(1, result.getErrors().size());
    
    LegacyFeatureException error = result.getErrors().get(0);
    assertEquals("field_presence", error.getFeatureName());
    assertEquals("LEGACY_REQUIRED", error.getFeatureValue());
  }

  @Test
  public void testEditionFileWithLegacyBestEffortJsonFormatIsInvalid() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat.LEGACY_BEST_EFFORT)
        .build();
    
    DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
        .setFeatures(features)
        .build();
    
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2024)
        .setOptions(options)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertTrue(result.hasErrors());
    assertEquals(1, result.getErrors().size());
    
    LegacyFeatureException error = result.getErrors().get(0);
    assertEquals("json_format", error.getFeatureName());
    assertEquals("LEGACY_BEST_EFFORT", error.getFeatureValue());
  }

  @Test
  public void testFieldWithLegacyFeatureIsInvalid() {
    DescriptorProtos.FeatureSet fieldFeatures = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED)
        .build();
    
    DescriptorProtos.FieldOptions fieldOptions = DescriptorProtos.FieldOptions.newBuilder()
        .setFeatures(fieldFeatures)
        .build();
    
    DescriptorProtos.FieldDescriptorProto field = DescriptorProtos.FieldDescriptorProto.newBuilder()
        .setName("test_field")
        .setNumber(1)
        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32)
        .setOptions(fieldOptions)
        .build();
    
    DescriptorProtos.DescriptorProto message = DescriptorProtos.DescriptorProto.newBuilder()
        .setName("TestMessage")
        .addField(field)
        .build();
    
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .addMessageType(message)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertTrue(result.hasErrors());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().get(0).getLocation().contains("test_field"));
  }

  @Test
  public void testThrowIfErrorsThrowsWhenErrorsExist() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED)
        .build();
    
    DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
        .setFeatures(features)
        .build();
    
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .setOptions(options)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    try {
      result.throwIfErrors();
      fail("Expected LegacyFeatureException to be thrown");
    } catch (LegacyFeatureException e) {
      assertEquals("field_presence", e.getFeatureName());
    }
  }

  @Test
  public void testThrowIfErrorsDoesNotThrowWhenNoErrors() {
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    // Should not throw
    result.throwIfErrors();
  }

  @Test
  public void testHasLegacyFeaturesDetectsLegacyRequired() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED)
        .build();
    
    assertTrue(FeatureValidator.hasLegacyFeatures(features));
  }

  @Test
  public void testHasLegacyFeaturesDetectsLegacyBestEffort() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat.LEGACY_BEST_EFFORT)
        .build();
    
    assertTrue(FeatureValidator.hasLegacyFeatures(features));
  }

  @Test
  public void testHasLegacyFeaturesReturnsFalseForValidFeatures() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.EXPLICIT)
        .setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat.ALLOW)
        .build();
    
    assertFalse(FeatureValidator.hasLegacyFeatures(features));
  }

  @Test
  public void testMultipleLegacyFeaturesReportsAllErrors() {
    DescriptorProtos.FeatureSet features = DescriptorProtos.FeatureSet.newBuilder()
        .setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED)
        .setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat.LEGACY_BEST_EFFORT)
        .build();
    
    DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
        .setFeatures(features)
        .build();
    
    DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName("test.proto")
        .setSyntax("editions")
        .setEdition(DescriptorProtos.Edition.EDITION_2023)
        .setOptions(options)
        .build();
    
    FeatureValidator validator = new FeatureValidator();
    ValidationResult result = validator.validate(proto);
    
    assertTrue(result.hasErrors());
    assertEquals(2, result.getErrors().size());
  }
}
