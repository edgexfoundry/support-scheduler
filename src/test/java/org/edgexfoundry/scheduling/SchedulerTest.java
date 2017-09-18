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

package org.edgexfoundry.scheduling;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.reflect.FieldUtils;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ScheduleData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Category({RequiresNone.class})
public class SchedulerTest {

  @InjectMocks
  private Scheduler scheduler;

  @Mock
  private ScheduleEventClient scheduleEventClient;

  // Client to fetch schedules
  @Mock
  private ScheduleClient scheduleClient;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCreateScheduleContext() {
    Schedule schedule = ScheduleData.newTestInstance();
    assertTrue("Could not create schedule", scheduler.createScheduleContext(schedule));
  }

  @Test
  public void testCreateScheduleContextForExisting() throws IllegalAccessException {
    Schedule schedule = ScheduleData.newTestInstance();
    ScheduleContext scheduleContext = new ScheduleContext(schedule);
    Map<String, ScheduleContext> map = new HashMap<>();
    map.put(schedule.getId(), scheduleContext);
    FieldUtils.writeField(scheduler, "scheduleIdToScheduleContextMap", map, true);
    assertFalse("Schedule context should already exist", scheduler.createScheduleContext(schedule));
  }

  @Test
  public void testUpdateScheduleContext() {
    Schedule schedule = ScheduleData.newTestInstance();
    scheduler.createScheduleContext(schedule);
    assertTrue("Could not update schedule", scheduler.updateScheduleContext(schedule));
  }

  @Test
  public void testUpdateScheduleContextNotFound() {
    Schedule schedule = ScheduleData.newTestInstance();
    schedule.setId("foo");
    assertFalse("Found schedule context with unkown id", scheduler.updateScheduleContext(schedule));
  }

  @Test
  public void testRemoveScheduleContext() {
    Schedule schedule = ScheduleData.newTestInstance();
    scheduler.createScheduleContext(schedule);
    assertTrue("Could not remove schedule", scheduler.removeScheduleById(schedule.getId()));
  }

  @Test
  public void testRemoveScheduleContextNotFound() {
    Schedule schedule = ScheduleData.newTestInstance();
    schedule.setId("foo");
    assertFalse("Found schedule context with unkown id",
        scheduler.removeScheduleById(schedule.getId()));
  }

  @Test
  public void testSchedule() throws IllegalAccessException {
    scheduler.schedule();
  }

}
