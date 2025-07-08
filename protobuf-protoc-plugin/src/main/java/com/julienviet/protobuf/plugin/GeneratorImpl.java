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
package com.julienviet.protobuf.plugin;

import com.google.protobuf.AnyProto;
import com.google.protobuf.ApiProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DurationProto;
import com.google.protobuf.EmptyProto;
import com.google.protobuf.FieldMaskProto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.JavaFeaturesProto;
import com.google.protobuf.SourceContextProto;
import com.google.protobuf.StructProto;
import com.google.protobuf.TimestampProto;
import com.google.protobuf.TypeProto;
import com.google.protobuf.WrappersProto;
import com.google.protobuf.compiler.PluginProtos;
import com.google.protobuf.util.JsonFormat;
import com.salesforce.jprotoc.Generator;
import com.salesforce.jprotoc.GeneratorException;
import com.julienviet.protobuf.plugin.reader.ProtoReaderGenerator;
import com.julienviet.protobuf.plugin.schema.SchemaGenerator;
import com.julienviet.protobuf.plugin.writer.ProtoWriterGenerator;
import com.julienviet.protobuf.extension.ExtensionProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneratorImpl extends Generator {

  public GeneratorImpl() {
  }

  @Override
  protected List<PluginProtos.CodeGeneratorResponse.Feature> supportedFeatures() {
    return Collections.singletonList(PluginProtos.CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL);
  }

  private static class Node {
    final DescriptorProtos.FileDescriptorProto fileDescProto;
    final List<Node> dependencies;
    final boolean generate;
    Descriptors.FileDescriptor fileDesc;
    Node(DescriptorProtos.FileDescriptorProto fileDescProto, boolean generate) {
      this.fileDescProto = fileDescProto;
      this.dependencies = new ArrayList<>();
      this.generate = generate;
    }
    Descriptors.FileDescriptor build() throws Descriptors.DescriptorValidationException {
      if (fileDesc == null) {
        List<Descriptors.FileDescriptor> deps = new ArrayList<>();
        for (Node dep : dependencies) {
          deps.add(dep.build());
        }
        fileDesc = Descriptors.FileDescriptor.buildFrom(fileDescProto, deps.toArray(new Descriptors.FileDescriptor[0]));
      }
      return fileDesc;
    }
  }

  @Override
  public List<PluginProtos.CodeGeneratorResponse.File> generateFiles(PluginProtos.CodeGeneratorRequest request) throws GeneratorException {

    List<DescriptorProtos.FileDescriptorProto> protosToGenerate = request.getProtoFileList().stream()
      .filter(protoFile -> request.getFileToGenerateList().contains(protoFile.getName()))
      .collect(Collectors.toList());

    Map<String, Node> nodeMap = new LinkedHashMap<>();
    for (DescriptorProtos.FileDescriptorProto fileDescProto : protosToGenerate) {
      nodeMap.put(fileDescProto.getName(), new Node(fileDescProto, true));
    }
    Map<String, Node> wellKnownDependencies = new LinkedHashMap<>();
    for (Node node : nodeMap.values()) {
      for (String dependency : node.fileDescProto.getDependencyList()) {
        Node depNode = nodeMap.get(dependency);
        if (depNode == null) {
          switch (dependency) {
            case "google/protobuf/any.proto":
              depNode = new Node(AnyProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/api.proto":
              depNode = new Node(ApiProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/descriptor.proto":
              depNode = new Node(DescriptorProtos.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/duration.proto":
              depNode = new Node(DurationProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/empty.proto":
              depNode = new Node(EmptyProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/field_mask.proto":
              depNode = new Node(FieldMaskProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/java_features.proto":
              depNode = new Node(JavaFeaturesProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/source_context.proto":
              depNode = new Node(SourceContextProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/struct.proto":
              depNode = new Node(StructProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/timestamp.proto":
              depNode = new Node(TimestampProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/type.proto":
              depNode = new Node(TypeProto.getDescriptor().toProto(), false);
              break;
            case "google/protobuf/wrappers.proto":
              depNode = new Node(WrappersProto.getDescriptor().toProto(), false);
              break;
            case "com/julienviet/protobuf/extension.proto":
              depNode = new Node(ExtensionProto.getDescriptor().toProto(), false);
              break;
            default:
              throw new UnsupportedOperationException("Import not found " + dependency);
          }
          wellKnownDependencies.put(dependency, depNode);
        }
      }
    }
    nodeMap.putAll(wellKnownDependencies);
    nodeMap.values().forEach(node -> {
      for (String dependency : node.fileDescProto.getDependencyList()) {
        node.dependencies.add(nodeMap.get(dependency));
      }
    });

    List<PluginProtos.CodeGeneratorResponse.File> files = new ArrayList<>();

    Map<String, List<Descriptors.FileDescriptor>> byPkg = new LinkedHashMap<>();
    nodeMap
      .values()
      .stream()
      .filter(node -> node.generate)
      .forEach(fileDescProto -> {
      Descriptors.FileDescriptor fileDesc;
      try {
        fileDesc = fileDescProto.build();
      } catch (Descriptors.DescriptorValidationException e) {
        GeneratorException ex = new GeneratorException(e.getMessage());
        ex.initCause(e);
        throw ex;
      }

      String key = Utils.extractJavaPkgFqn(fileDesc);
      byPkg.computeIfAbsent(key, k -> new ArrayList<>()).add(fileDesc);
    });

    byPkg.forEach((javaPkgFqn, v) -> {
      Map<String, Descriptors.Descriptor> messages = new LinkedHashMap<>();
      List<Descriptors.EnumDescriptor> enums = new ArrayList<>();
      DescriptorProtos.FileDescriptorSet.Builder set = DescriptorProtos.FileDescriptorSet.newBuilder();
      for (Descriptors.FileDescriptor f : v) {
        Map<String, Descriptors.Descriptor> res = Utils.transitiveClosure(f.getMessageTypes());
        messages.putAll(res);
        enums.addAll(f.getEnumTypes());
        res
          .values()
          .stream()
          .flatMap(descriptor -> descriptor.getEnumTypes().stream())
          .forEach(enums::add);
        set.addFile(f.toProto());
      }
      byte[] proto = set.build().toByteArray();
      files.add(PluginProtos.CodeGeneratorResponse.File
        .newBuilder()
        .setName(Utils.absoluteFileName(javaPkgFqn, "descriptor.bin"))
        .setContentBytes(ByteString.copyFrom(proto))
        .build());
      try {
        String json = JsonFormat.printer().print(set.build());
        files.add(PluginProtos.CodeGeneratorResponse.File
          .newBuilder()
          .setName(Utils.absoluteFileName(javaPkgFqn, "descriptor.json"))
          .setContent(json)
          .build());
      } catch (InvalidProtocolBufferException e) {
      }

      List<Descriptors.Descriptor> toGen = messages.values().stream().filter(d -> !d.getOptions().getMapEntry()).collect(Collectors.toList());
      files.addAll(new ElementGenerator(javaPkgFqn, new ArrayList<>(toGen), enums).generate());
//      files.add(new SchemaGenerator(javaPkgFqn, new ArrayList<>(messages.values())).generate());

      SchemaGenerator generator = new SchemaGenerator(javaPkgFqn);
      generator.init(new ArrayList<>(messages.values()), enums);
      files.addAll(generate(javaPkgFqn, generator));
      files.add(generate(javaPkgFqn, new ProtoReaderGenerator(javaPkgFqn, false, new ArrayList<>(messages.values()))));
      files.add(generate(javaPkgFqn, new ProtoWriterGenerator(javaPkgFqn, false, true, new ArrayList<>(messages.values()))));
    });

    return files;
  }

  private PluginProtos.CodeGeneratorResponse.File generate(String javaPkgFqn, ProtoWriterGenerator writerGenerator) {
    return PluginProtos.CodeGeneratorResponse.File
      .newBuilder()
      .setName(Utils.absoluteJavaFileName(javaPkgFqn, "ProtoWriter"))
      .setContent(writerGenerator.generate())
      .build();
  }

  private PluginProtos.CodeGeneratorResponse.File generate(String javaPkgFqn, ProtoReaderGenerator readerGenerator) {
    return PluginProtos.CodeGeneratorResponse.File
      .newBuilder()
      .setName(Utils.absoluteJavaFileName(javaPkgFqn, "ProtoReader"))
      .setContent(readerGenerator.generate())
      .build();
  }

  private List<PluginProtos.CodeGeneratorResponse.File> generate(String javaPkgFqn, SchemaGenerator schemaGenerator) {
    return Arrays.asList(
      PluginProtos.CodeGeneratorResponse.File
        .newBuilder()
        .setName(Utils.absoluteJavaFileName(javaPkgFqn, "FieldLiteral"))
        .setContent(schemaGenerator.generateFieldLiterals())
        .build(),
      PluginProtos.CodeGeneratorResponse.File
        .newBuilder()
        .setName(Utils.absoluteJavaFileName(javaPkgFqn, "MessageLiteral"))
        .setContent(schemaGenerator.generateMessageLiterals())
        .build(),
      PluginProtos.CodeGeneratorResponse.File
        .newBuilder()
        .setName(Utils.absoluteJavaFileName(javaPkgFqn, "EnumLiteral"))
        .setContent(schemaGenerator.generateEnumLiterals())
        .build()
    );
  }
}
