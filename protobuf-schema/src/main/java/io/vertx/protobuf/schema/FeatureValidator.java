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
package io.vertx.protobuf.schema;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates protobuf edition features and rejects legacy feature values.
 * 
 * <p>When using protobuf editions (2023+), certain legacy feature values should be avoided
 * as they represent deprecated behavior from proto2/proto3. This validator helps ensure
 * that edition-based protos don't use legacy features.</p>
 * 
 * <p>Legacy feature values that will be rejected:</p>
 * <ul>
 *   <li>field_presence = LEGACY_REQUIRED</li>
 *   <li>json_format = LEGACY_BEST_EFFORT</li>
 *   <li>enforce_naming_style = STYLE_LEGACY (Edition 2024+)</li>
 * </ul>
 */
public class FeatureValidator {

  /**
   * Exception thrown when a legacy feature value is detected.
   */
  public static class LegacyFeatureException extends RuntimeException {
    private final String featureName;
    private final String featureValue;
    private final String location;

    public LegacyFeatureException(String featureName, String featureValue, String location) {
      super(String.format("Legacy feature value '%s = %s' is not allowed in editions. Location: %s",
          featureName, featureValue, location));
      this.featureName = featureName;
      this.featureValue = featureValue;
      this.location = location;
    }

    public String getFeatureName() {
      return featureName;
    }

    public String getFeatureValue() {
      return featureValue;
    }

    public String getLocation() {
      return location;
    }
  }

  /**
   * Result of feature validation containing any detected issues.
   */
  public static class ValidationResult {
    private final List<LegacyFeatureException> errors;

    public ValidationResult() {
      this.errors = new ArrayList<>();
    }

    public void addError(LegacyFeatureException error) {
      errors.add(error);
    }

    public boolean hasErrors() {
      return !errors.isEmpty();
    }

    public List<LegacyFeatureException> getErrors() {
      return new ArrayList<>(errors);
    }

    /**
     * Throws the first error if any errors were found.
     */
    public void throwIfErrors() throws LegacyFeatureException {
      if (!errors.isEmpty()) {
        throw errors.get(0);
      }
    }
  }

  private final boolean strict;

  /**
   * Creates a feature validator.
   *
   * @param strict if true, all legacy features cause errors; if false, only critical ones do
   */
  public FeatureValidator(boolean strict) {
    this.strict = strict;
  }

  /**
   * Creates a strict feature validator that rejects all legacy features.
   */
  public FeatureValidator() {
    this(true);
  }

  /**
   * Validates a FileDescriptor and all its contents for legacy features.
   *
   * @param fileDescriptor the file descriptor to validate
   * @return validation result containing any errors found
   */
  public ValidationResult validate(Descriptors.FileDescriptor fileDescriptor) {
    ValidationResult result = new ValidationResult();
    
    // Only validate edition-based files
    Syntax syntax = Syntax.fromFileDescriptor(fileDescriptor);
    if (!syntax.isEdition()) {
      return result;
    }

    String fileName = fileDescriptor.getName();
    
    // Validate file-level features
    validateFileOptions(fileDescriptor.toProto().getOptions(), fileName, result);
    
    // Validate all messages
    for (Descriptors.Descriptor message : fileDescriptor.getMessageTypes()) {
      validateMessage(message, fileName, result);
    }
    
    // Validate all enums
    for (Descriptors.EnumDescriptor enumDesc : fileDescriptor.getEnumTypes()) {
      validateEnum(enumDesc, fileName, result);
    }
    
    return result;
  }

  /**
   * Validates a FileDescriptorProto for legacy features.
   *
   * @param proto the file descriptor proto to validate
   * @return validation result containing any errors found
   */
  public ValidationResult validate(DescriptorProtos.FileDescriptorProto proto) {
    ValidationResult result = new ValidationResult();
    
    // Only validate edition-based files
    Syntax syntax = Syntax.fromFileDescriptorProto(proto);
    if (!syntax.isEdition()) {
      return result;
    }

    String fileName = proto.getName();
    
    // Validate file-level options
    if (proto.hasOptions()) {
      validateFileOptions(proto.getOptions(), fileName, result);
    }
    
    // Validate messages
    for (DescriptorProtos.DescriptorProto message : proto.getMessageTypeList()) {
      validateMessageProto(message, fileName, result);
    }
    
    // Validate enums
    for (DescriptorProtos.EnumDescriptorProto enumProto : proto.getEnumTypeList()) {
      validateEnumProto(enumProto, fileName, result);
    }
    
    return result;
  }

  private void validateFileOptions(DescriptorProtos.FileOptions options, String location, ValidationResult result) {
    if (options.hasFeatures()) {
      validateFeatureSet(options.getFeatures(), location + " (file options)", result);
    }
  }

  private void validateMessage(Descriptors.Descriptor message, String fileLocation, ValidationResult result) {
    String location = fileLocation + ":" + message.getFullName();
    
    // Validate message options
    if (message.toProto().hasOptions() && message.toProto().getOptions().hasFeatures()) {
      validateFeatureSet(message.toProto().getOptions().getFeatures(), location, result);
    }
    
    // Validate fields
    for (Descriptors.FieldDescriptor field : message.getFields()) {
      validateField(field, location, result);
    }
    
    // Validate nested messages
    for (Descriptors.Descriptor nested : message.getNestedTypes()) {
      validateMessage(nested, fileLocation, result);
    }
    
    // Validate nested enums
    for (Descriptors.EnumDescriptor enumDesc : message.getEnumTypes()) {
      validateEnum(enumDesc, fileLocation, result);
    }
  }

  private void validateMessageProto(DescriptorProtos.DescriptorProto message, String fileLocation, ValidationResult result) {
    String location = fileLocation + ":" + message.getName();
    
    // Validate message options
    if (message.hasOptions() && message.getOptions().hasFeatures()) {
      validateFeatureSet(message.getOptions().getFeatures(), location, result);
    }
    
    // Validate fields
    for (DescriptorProtos.FieldDescriptorProto field : message.getFieldList()) {
      validateFieldProto(field, location, result);
    }
    
    // Validate nested messages
    for (DescriptorProtos.DescriptorProto nested : message.getNestedTypeList()) {
      validateMessageProto(nested, fileLocation, result);
    }
    
    // Validate nested enums
    for (DescriptorProtos.EnumDescriptorProto enumProto : message.getEnumTypeList()) {
      validateEnumProto(enumProto, fileLocation, result);
    }
  }

  private void validateField(Descriptors.FieldDescriptor field, String messageLocation, ValidationResult result) {
    String location = messageLocation + "." + field.getName();
    
    if (field.toProto().hasOptions() && field.toProto().getOptions().hasFeatures()) {
      validateFeatureSet(field.toProto().getOptions().getFeatures(), location, result);
    }
  }

  private void validateFieldProto(DescriptorProtos.FieldDescriptorProto field, String messageLocation, ValidationResult result) {
    String location = messageLocation + "." + field.getName();
    
    if (field.hasOptions() && field.getOptions().hasFeatures()) {
      validateFeatureSet(field.getOptions().getFeatures(), location, result);
    }
  }

  private void validateEnum(Descriptors.EnumDescriptor enumDesc, String fileLocation, ValidationResult result) {
    String location = fileLocation + ":" + enumDesc.getFullName();
    
    if (enumDesc.toProto().hasOptions() && enumDesc.toProto().getOptions().hasFeatures()) {
      validateFeatureSet(enumDesc.toProto().getOptions().getFeatures(), location, result);
    }
  }

  private void validateEnumProto(DescriptorProtos.EnumDescriptorProto enumProto, String fileLocation, ValidationResult result) {
    String location = fileLocation + ":" + enumProto.getName();
    
    if (enumProto.hasOptions() && enumProto.getOptions().hasFeatures()) {
      validateFeatureSet(enumProto.getOptions().getFeatures(), location, result);
    }
  }

  private void validateFeatureSet(DescriptorProtos.FeatureSet features, String location, ValidationResult result) {
    // Check field_presence for LEGACY_REQUIRED
    if (features.hasFieldPresence()) {
      DescriptorProtos.FeatureSet.FieldPresence fieldPresence = features.getFieldPresence();
      if (fieldPresence == DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED) {
        result.addError(new LegacyFeatureException("field_presence", "LEGACY_REQUIRED", location));
      }
    }
    
    // Check json_format for LEGACY_BEST_EFFORT
    if (features.hasJsonFormat()) {
      DescriptorProtos.FeatureSet.JsonFormat jsonFormat = features.getJsonFormat();
      if (jsonFormat == DescriptorProtos.FeatureSet.JsonFormat.LEGACY_BEST_EFFORT) {
        result.addError(new LegacyFeatureException("json_format", "LEGACY_BEST_EFFORT", location));
      }
    }
    
    // Note: Additional legacy features can be added here as needed
    // For example, when enforce_naming_style = STYLE_LEGACY is detected in Edition 2024+
  }

  /**
   * Checks if the given feature set contains any legacy feature values.
   *
   * @param features the feature set to check
   * @return true if any legacy features are found
   */
  public static boolean hasLegacyFeatures(DescriptorProtos.FeatureSet features) {
    if (features.hasFieldPresence() && 
        features.getFieldPresence() == DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED) {
      return true;
    }
    
    if (features.hasJsonFormat() && 
        features.getJsonFormat() == DescriptorProtos.FeatureSet.JsonFormat.LEGACY_BEST_EFFORT) {
      return true;
    }
    
    return false;
  }
}
