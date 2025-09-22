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
package io.vertx.protobuf.codegen;

import javax.lang.model.element.Element;
import java.util.Objects;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ValidationException extends RuntimeException {

  final Element element;
  final ValidationError error;
  final String message;

  public ValidationException(Element element, ValidationError error, String message) {
    super(message);
    this.element = element;
    this.error = error;
    this.message = message;
  }

  public ValidationError getError() {
    return error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    } else {
      ValidationException exception = (ValidationException) o;
      return Objects.equals(element, exception.element) && error == exception.error && Objects.equals(message, exception.message);
    }
  }

  @Override
  public int hashCode() {
    int result = element != null ? element.hashCode() : 0;
    result = 31 * result + (error != null ? error.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    return result;
  }
}
