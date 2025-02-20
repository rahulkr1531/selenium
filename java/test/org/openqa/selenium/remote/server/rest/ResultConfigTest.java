// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.remote.server.rest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.SessionId;

class ResultConfigTest {
  private Logger LOG = Logger.getLogger(ResultConfigTest.class.getName());
  private static final SessionId dummySessionId = new SessionId("Test");

  @Test
  void testShouldNotAllowNullToBeUsedAsTheUrl() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new ResultConfig(null, () -> () -> null, null, LOG));
  }

  @Test
  void testShouldNotAllowNullToBeUsedForTheHandler() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> new ResultConfig("/cheese", (Supplier<RestishHandler<?>>) null, null, LOG));
  }

  @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
  @Test
  void testShouldGracefullyHandleNullInputs() {
    ResultConfig config = new ResultConfig("/foo/:bar", () -> () -> null, null, LOG);
    assertNull(config.getRootExceptionCause(null));
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  void testCanPeelNestedExceptions() {
    RuntimeException runtime = new RuntimeException("root of all evils");
    InvocationTargetException invocation =
        new InvocationTargetException(runtime, "Got Runtime Exception");
    WebDriverException webdriverException =
        new WebDriverException("Invocation problems", invocation);
    ExecutionException execution =
        new ExecutionException("General WebDriver error", webdriverException);

    ResultConfig config = new ResultConfig("/foo/:bar", () -> () -> null, null, LOG);
    Throwable toClient = config.getRootExceptionCause(execution);
    assertEquals(toClient, runtime);
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  void testDoesNotPeelTooManyLayersFromNestedExceptions() {
    RuntimeException runtime = new RuntimeException("root of all evils");
    NoSuchElementException noElement = new NoSuchElementException("no soup for you", runtime);
    InvocationTargetException invocation = new InvocationTargetException(noElement);
    UndeclaredThrowableException undeclared = new UndeclaredThrowableException(invocation);

    ResultConfig config = new ResultConfig("/foo/:bar", () -> () -> null, null, LOG);
    Throwable toClient = config.getRootExceptionCause(undeclared);
    assertEquals(noElement, toClient);
  }
}
