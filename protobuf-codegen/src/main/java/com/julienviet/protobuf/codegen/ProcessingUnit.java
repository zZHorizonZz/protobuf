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
package com.julienviet.protobuf.codegen;

import com.google.protobuf.DescriptorProtos;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ProcessingUnit {

  final String javaPkg;
  final String protoPkg;
  final Map<String, DescriptorProtos.DescriptorProto.Builder> messages;
  final DescriptorProtos.FileDescriptorProto.Builder fileBuilder;
  final Set<String> dependencies;
  final boolean stub;

  public ProcessingUnit(String javaPkg, String protoPkg, boolean stub) {

    DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
    builder.setSyntax("proto3");
    builder.setPackage(protoPkg);
    builder.setOptions(DescriptorProtos.FileOptions.newBuilder()
            .setJavaPackage(javaPkg)
//            .setExtension(VertxProto.typeInterop, true)
            .build());

    this.javaPkg = javaPkg;
    this.protoPkg = protoPkg;
    this.fileBuilder = builder;
    this.dependencies = new HashSet<>();
    this.messages = new LinkedHashMap<>();
    this.stub = stub;
  }
}
