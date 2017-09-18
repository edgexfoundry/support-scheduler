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
 * @microservice: support-notifications
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.scheduling;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.domain.meta.ActionType;
import org.edgexfoundry.domain.meta.CallbackAlert;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ScheduleData;
import org.edgexfoundry.test.data.ScheduleEventData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category({RequiresNone.class})
public class SchedulerCallbackHandlerTest {

  private static final String TEST_ID = "123";

  @InjectMocks
  private SchedulerCallbackHandler handler;

  @Mock
  private Scheduler scheduler;

  @Mock
  private ScheduleClient scheduleClient;

  @Mock
  private ScheduleEventClient scheduleEventClient;

  private CallbackAlert alert;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    alert = new CallbackAlert();
    alert.setType(ActionType.SCHEDULE);
    alert.setId(TEST_ID);
  }

  @Test
  public void testHandlePutWithSchedule() {
    Schedule schedule = ScheduleData.newTestInstance();
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenReturn(schedule);
    assertTrue("Update of schedule did not happen successfully", handler.handlePut(alert));
  }

  @Test
  public void testHandlePutWithScheduleNotFound() {
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenReturn(null);
    assertTrue("Update should return true by default", handler.handlePut(alert));
  }

  @Test
  public void testHandlePutWithScheduleException() {
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenThrow(new RuntimeException());
    assertFalse("Update of schedule should not happened happen successfully",
        handler.handlePut(alert));
  }

  @Test
  public void testHandlePutWithScheduleEvent() {
    alert.setType(ActionType.SCHEDULEEVENT);
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenReturn(event);
    assertTrue("Update of schedule event did not happen successfully", handler.handlePut(alert));
  }

  @Test
  public void testHandlePutWithScheduleEventNotFound() {
    alert.setType(ActionType.SCHEDULEEVENT);
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenReturn(null);
    assertTrue("Update should return true by default", handler.handlePut(alert));
  }

  @Test
  public void testHandlePutWithScheduleEventException() {
    alert.setType(ActionType.SCHEDULEEVENT);
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenThrow(new RuntimeException());
    assertFalse("Update of schedule event should not happened happen successfully",
        handler.handlePut(alert));
  }

  @Test
  public void testHandlePutWithWrongType() {
    alert.setType(ActionType.DEVICE);
    assertTrue("Update should return true by default", handler.handlePut(alert));
  }

  @Test
  public void testHandlePostWithSchedule() {
    Schedule schedule = ScheduleData.newTestInstance();
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenReturn(schedule);
    assertTrue("Add of schedule did not happen successfully", handler.handlePost(alert));
  }

  @Test
  public void testHandlePostWithScheduleNotFound() {
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenReturn(null);
    assertTrue("Add should return true by default", handler.handlePost(alert));
  }

  @Test
  public void testHandlePostWithScheduleException() {
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenThrow(new RuntimeException());
    assertFalse("Add of schedule should not happened happen successfully",
        handler.handlePost(alert));
  }

  @Test
  public void testHandlePostWithScheduleEvent() {
    alert.setType(ActionType.SCHEDULEEVENT);
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenReturn(event);
    assertTrue("Add of schedule event did not happen successfully", handler.handlePost(alert));
  }

  @Test
  public void testHandlePostWithScheduleEventNotFound() {
    alert.setType(ActionType.SCHEDULEEVENT);
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenReturn(null);
    assertTrue("Add should return true by default", handler.handlePost(alert));
  }

  @Test
  public void testHandlePostWithScheduleEventException() {
    alert.setType(ActionType.SCHEDULEEVENT);
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenThrow(new RuntimeException());
    assertFalse("Add of schedule event should not happened happen successfully",
        handler.handlePost(alert));
  }

  @Test
  public void testHandlePostWithWrongType() {
    alert.setType(ActionType.DEVICE);
    assertTrue("Add should return true by default", handler.handlePost(alert));
  }

  @Test
  public void testHandleDeleteWithSchedule() {
    Schedule schedule = ScheduleData.newTestInstance();
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenReturn(schedule);
    assertTrue("Remove of schedule did not happen successfully", handler.handleDelete(alert));
  }

  @Test
  public void testHandleDeleteWithScheduleNotFound() {
    Mockito.when(scheduleClient.schedule(TEST_ID)).thenReturn(null);
    assertTrue("Remove should return true by default", handler.handleDelete(alert));
  }

  @Test
  public void testHandleDeleteWithScheduleException() {
    Mockito.when(scheduler.removeScheduleById(TEST_ID)).thenThrow(new RuntimeException());
    assertFalse("Add of schedule should not happened happen successfully",
        handler.handleDelete(alert));
  }

  @Test
  public void testHandleDeleteWithScheduleEvent() {
    alert.setType(ActionType.SCHEDULEEVENT);
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenReturn(event);
    assertTrue("Remove of schedule event did not happen successfully", handler.handleDelete(alert));
  }

  @Test
  public void testHandleDeleteWithScheduleEventNotFound() {
    alert.setType(ActionType.SCHEDULEEVENT);
    Mockito.when(scheduleEventClient.scheduleEvent(TEST_ID)).thenReturn(null);
    assertTrue("Remove should return true by default", handler.handleDelete(alert));
  }

  @Test
  public void testHandleDeleteWithScheduleEventException() {
    alert.setType(ActionType.SCHEDULEEVENT);
    Mockito.when(scheduler.removeScheduleEventById(TEST_ID)).thenThrow(new RuntimeException());
    assertFalse("Remove of schedule event should not happened happen successfully",
        handler.handleDelete(alert));
  }

  @Test
  public void testHandleDeleteWithWrongType() {
    alert.setType(ActionType.DEVICE);
    assertTrue("Delete should return true by default", handler.handleDelete(alert));
  }
}
