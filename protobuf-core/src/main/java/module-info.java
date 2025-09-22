/*
 * Copyright (c) 2011-2024 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
module io.vertx.protobuf.core {

  requires io.vertx.protobuf.schema;
  requires io.vertx.protobuf.lang;

  // Won't work without but it should be capable of
  requires static com.fasterxml.jackson.core;

  exports io.vertx.protobuf.core.json;
  exports io.vertx.protobuf.well_known_types;
  exports io.vertx.protobuf.core.interop;
  exports io.vertx.protobuf.core.json.jackson;
  exports io.vertx.protobuf.core;

}
