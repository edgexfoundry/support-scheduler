/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-scheduler
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller;

import static org.junit.Assert.assertEquals;

import org.edgexfoundry.controller.impl.CallbackControllerImpl;
import org.edgexfoundry.domain.meta.CallbackAlert;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.scheduling.SchedulerCallbackHandler;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category({RequiresNone.class})
public class CallbackControllerTest {

  @InjectMocks
  private CallbackControllerImpl controller;

  @Mock
  private SchedulerCallbackHandler callbackHandler;

  private CallbackAlert alert;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    alert = new CallbackAlert();
  }

  @Test
  public void testHandlePut() {
    Mockito.when(callbackHandler.handlePut(alert)).thenReturn(true);
    assertEquals("Handle put did not response as expected", "true", controller.handlePut(alert));
  }

  @Test(expected = ServiceException.class)
  public void testHandlePutException() {
    Mockito.when(callbackHandler.handlePut(alert)).thenThrow(new RuntimeException());
    controller.handlePut(alert);
  }

  @Test
  public void testHandlePost() {
    Mockito.when(callbackHandler.handlePost(alert)).thenReturn(true);
    assertEquals("Handle post did not response as expected", "true", controller.handlePost(alert));
  }

  @Test(expected = ServiceException.class)
  public void testHandlePostException() {
    Mockito.when(callbackHandler.handlePost(alert)).thenThrow(new RuntimeException());
    controller.handlePost(alert);
  }

  @Test
  public void testHandleDelete() {
    Mockito.when(callbackHandler.handleDelete(alert)).thenReturn(true);
    assertEquals("Handle delete did not response as expected", "true",
        controller.handleDelete(alert));
  }

  @Test(expected = ServiceException.class)
  public void testHandleDeleteException() {
    Mockito.when(callbackHandler.handleDelete(alert)).thenThrow(new RuntimeException());
    controller.handleDelete(alert);
  }

}
