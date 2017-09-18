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
 * @author: Marc Hammons, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.scheduling.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.edgexfoundry.Application;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.scheduling.Scheduler;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;
import org.edgexfoundry.test.data.ScheduleData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Category({RequiresMongoDB.class, RequiresSpring.class, RequiresMetaDataRunning.class})
public class SchedulerTest {

  @Autowired
  private ScheduleClient scheduleClient;

  @Autowired
  private Scheduler scheduler;

  @Before
  public void setup() {
    System.out.println(
        "Note - Mongo DB and IoT Core Meta Data service must be on for this test to run successfully.");
  }

  @After
  public void cleanup() {
    List<Schedule> scheduleList = scheduleClient.schedules();
    for (Schedule schedule : scheduleList) {
      scheduleClient.delete(schedule.getId());
    }
  }

  @Test
  public void testInsert() throws InterruptedException {
    Schedule s = ScheduleData.newTestInstance();
    // assertNotNull(scheduleClient.add(s));
    scheduler.createScheduleContext(s);
    // TODO: need the schedule context to check results :(
    // assertTrue("schedule should execute only once ", sc sc.() == true);
  }

  @Test
  public void testRemoveById() throws InterruptedException {
    Schedule schedule = ScheduleData.newTestInstance();
    scheduler.createScheduleContext(schedule);
    assertTrue("Could not remove Schedule", scheduler.removeScheduleById(schedule.getId()));
  }

  @Test
  public void testRemoveByIdTwice() throws InterruptedException {
    Schedule schedule = ScheduleData.newTestInstance();
    scheduler.createScheduleContext(schedule);
    assertTrue("Could not remove Schedule", scheduler.removeScheduleById(schedule.getId()));
    assertFalse("Could remove twice the same schedule", scheduler.removeScheduleById(schedule.getId()));
  }

  @Test
  public void testRemoveByIdTwice2() throws InterruptedException {
    Schedule schedule = ScheduleData.newTestInstance();
    scheduler.createScheduleContext(schedule);
    scheduler.createScheduleContext(schedule);
    assertTrue("Could not remove Schedule", scheduler.removeScheduleById(schedule.getId()));
    assertFalse("Could remove twice the same schedule", scheduler.removeScheduleById(schedule.getId()));
  }

  @Test
  public void testUpdateScheduleContext() throws InterruptedException {
    Schedule schedule = ScheduleData.newTestInstance();
    Schedule schedule2 = ScheduleData.newTestInstance();
    schedule2.setId("anotherId");
    scheduler.createScheduleContext(schedule);
    schedule.setRunOnce(!schedule.getRunOnce());
    assertTrue("Could not update Schedule", scheduler.removeScheduleById(schedule.getId()));
    assertFalse("Should not be able to update not added schedule",
        scheduler.removeScheduleById(schedule.getId()));
  }

}
