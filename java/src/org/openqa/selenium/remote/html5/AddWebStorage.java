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

package org.openqa.selenium.remote.html5;

import static org.openqa.selenium.remote.Browser.CHROME;
import static org.openqa.selenium.remote.Browser.EDGE;
import static org.openqa.selenium.remote.Browser.FIREFOX;
import static org.openqa.selenium.remote.Browser.OPERA;
import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_WEB_STORAGE;

import java.util.function.Predicate;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.AugmenterProvider;
import org.openqa.selenium.remote.ExecuteMethod;

public class AddWebStorage implements AugmenterProvider<WebStorage> {

  @Override
  public Predicate<Capabilities> isApplicable() {
    return caps ->
        FIREFOX.is(caps)
            || CHROME.is(caps)
            || EDGE.is(caps)
            || OPERA.is(caps)
            || caps.is(SUPPORTS_WEB_STORAGE);
  }

  @Override
  public Class<WebStorage> getDescribedInterface() {
    return WebStorage.class;
  }

  @Override
  public WebStorage getImplementation(Capabilities capabilities, ExecuteMethod executeMethod) {
    return new RemoteWebStorage(executeMethod);
  }
}
